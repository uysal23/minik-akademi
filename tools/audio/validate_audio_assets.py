#!/usr/bin/env python3
from __future__ import annotations

import json
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CONTENT = ROOT / "content"
SPEECH = ROOT / "audio" / "runtime" / "speech"
SFX = ROOT / "audio" / "runtime" / "sfx"
MANIFEST = ROOT / "audio" / "manifest" / "audio_manifest.json"
REPORT = ROOT / "audio" / "manifest" / "generation_report.json"

EXPECTED_SFX = {
    "sfx_soft_pop",
    "sfx_gentle_tap",
    "sfx_success_chime",
    "sfx_star_sparkle",
    "sfx_slide_soft",
    "sfx_retry_soft",
    "sfx_complete",
}

EXPECTED_STATIC_SPEECH = {
    "aud_common_success_01",
    "aud_common_retry_01",
    "aud_common_welcome_01",
    "aud_common_ready_01",
    *{f"aud_phoneme_{x}" for x in ["a", "n", "e", "t", "i", "l", "o", "k", "u"]},
    *{f"aud_number_{n:02d}" for n in range(21)},
}


def is_ogg(path: Path) -> bool:
    try:
        return path.stat().st_size > 256 and path.read_bytes()[:4] == b"OggS"
    except OSError:
        return False


def activity_files() -> list[Path]:
    result: list[Path] = []
    for path in CONTENT.rglob("*.json"):
        if path.name in {"activity_schema.json", "curriculum_manifest.json"}:
            continue
        result.append(path)
    return sorted(result)


def main() -> int:
    if not SPEECH.exists() or len(list(SPEECH.glob("*.ogg"))) != 159:
        preparer = ROOT / "tools" / "audio" / "prepare_v4_runtime_audio.py"
        subprocess.run([sys.executable, str(preparer)], cwd=ROOT, check=True)

    errors: list[str] = []
    required_speech: set[str] = set()
    required_sfx: set[str] = set()

    for path in activity_files():
        try:
            data = json.loads(path.read_text(encoding="utf-8"))
        except Exception as exc:
            errors.append(f"{path.relative_to(ROOT)}: invalid JSON: {exc}")
            continue

        instruction = data.get("instruction", {})
        feedback = data.get("feedback", {})
        extra = data.get("audio", {}).get("extraAudioIds", [])

        for value in [
            instruction.get("audioId"),
            feedback.get("successAudioId"),
            feedback.get("retryAudioId"),
            *extra,
        ]:
            if isinstance(value, str) and value:
                required_speech.add(value)

        for value in [feedback.get("successSfx"), feedback.get("retrySfx")]:
            if isinstance(value, str) and value:
                required_sfx.add(value.lower())

    required_speech.update(EXPECTED_STATIC_SPEECH)

    for audio_id in sorted(required_speech):
        path = SPEECH / f"{audio_id}.ogg"
        if not is_ogg(path):
            errors.append(f"missing/invalid speech OGG: {audio_id} -> {path.relative_to(ROOT)}")

    required_sfx.update(EXPECTED_SFX)
    for sfx_id in sorted(required_sfx):
        path = SFX / f"{sfx_id}.ogg"
        if not is_ogg(path):
            errors.append(f"missing/invalid SFX OGG: {sfx_id} -> {path.relative_to(ROOT)}")

    if not MANIFEST.exists():
        errors.append("audio manifest missing")
        manifest = {}
        manifest_items = []
    else:
        manifest = json.loads(MANIFEST.read_text(encoding="utf-8"))
        manifest_items = manifest.get("items", [])
        if manifest.get("audioRevision") != "V4_C":
            errors.append(f"active audio manifest must be V4_C, got {manifest.get('audioRevision')!r}")
        if manifest.get("voice") != "C":
            errors.append(f"active audio voice must be C, got {manifest.get('voice')!r}")
        if manifest.get("speechCount") != 159:
            errors.append(f"active audio speechCount must be 159, got {manifest.get('speechCount')!r}")
        if manifest.get("phonemeQa") != "USER_APPROVED":
            errors.append("active audio phonemeQa must be USER_APPROVED")
        if manifest.get("paidApiUsed") is not False:
            errors.append("active audio manifest must declare paidApiUsed=false")
        manifest_ids = {str(x) for x in manifest.get("speechIds", [])}
        if manifest_ids != required_speech:
            missing = sorted(required_speech - manifest_ids)
            extra_ids = sorted(manifest_ids - required_speech)
            errors.append(f"V4 C manifest speechIds mismatch: missing={missing} extra={extra_ids}")

