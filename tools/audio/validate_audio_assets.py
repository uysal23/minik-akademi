#!/usr/bin/env python3
from __future__ import annotations

import json
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CONTENT = ROOT / "content"
SPEECH = ROOT / "audio_v4" / "runtime" / "speech"
SFX = ROOT / "audio_v4" / "runtime" / "sfx"
MANIFEST = ROOT / "audio_v4" / "manifest" / "audio_manifest.json"
REPORT = ROOT / "audio_v4" / "manifest" / "generation_report.json"

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

    if not REPORT.exists():
        errors.append("audio generation report missing")
        report = {}
    else:
        report = json.loads(REPORT.read_text(encoding="utf-8"))
        if report.get("audioRevision") != "V4_C":
            errors.append(f"generation report audioRevision must be V4_C, got {report.get('audioRevision')!r}")
        if report.get("voice") != "C":
            errors.append(f"generation report voice must be C, got {report.get('voice')!r}")
        if report.get("requested") != 159 or report.get("generated") != 159 or report.get("failed") != 0:
            errors.append(
                "generation report must declare requested=159, generated=159, failed=0"
            )
        if report.get("phonemeQa") != "USER_APPROVED":
            errors.append("generation report phonemeQa must be USER_APPROVED")

    speech_files = sorted(SPEECH.glob("*.ogg"))
    if len(speech_files) != 159:
        errors.append(f"runtime speech file count must be 159, got {len(speech_files)}")

    sfx_files = sorted(SFX.glob("*.ogg"))
    if len(sfx_files) != 7:
        errors.append(f"runtime SFX file count must be 7, got {len(sfx_files)}")

    ffprobe = None
    try:
        ffprobe = subprocess.run(
            ["ffprobe", "-version"],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            check=False,
        ).returncode == 0
    except OSError:
        ffprobe = False

    if ffprobe:
        for path in speech_files:
            probe = subprocess.run(
                [
                    "ffprobe", "-v", "error", "-select_streams", "a:0",
                    "-show_entries", "stream=codec_name,sample_rate,channels",
                    "-of", "json", str(path),
                ],
                capture_output=True,
                text=True,
                check=False,
            )
            if probe.returncode != 0:
                errors.append(f"ffprobe failed for {path.relative_to(ROOT)}")
                continue
            try:
                streams = json.loads(probe.stdout).get("streams", [])
                stream = streams[0] if streams else {}
            except Exception:
                stream = {}
            if stream.get("codec_name") != "vorbis":
                errors.append(f"{path.relative_to(ROOT)}: codec must be vorbis")
            if str(stream.get("sample_rate")) != "24000":
                errors.append(f"{path.relative_to(ROOT)}: sample rate must be 24000 Hz")
            if int(stream.get("channels", 0) or 0) != 1:
                errors.append(f"{path.relative_to(ROOT)}: channels must be mono")

    if errors:
        print(f"AUDIO VALIDATION FAILED ({len(errors)} error(s))")
        for err in errors:
            print(f" - {err}")
        return 1

    print("AUDIO VALIDATION PASSED")
    print(" audio revision: V4_C")
    print(" voice: C")
    print(" speech: 159/159")
    print(" phoneme QA: USER_APPROVED")
    print(" sfx: 7/7")
    if ffprobe:
        print(" format: OGG/Vorbis, mono, 24 kHz")
    return 0


if __name__ == "__main__":
    sys.exit(main())
