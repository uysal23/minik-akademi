#!/usr/bin/env python3
from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CONTENT = ROOT / "content"
SPEECH = ROOT / "audio" / "generated" / "speech"
SFX = ROOT / "audio" / "generated" / "sfx"
MANIFEST = ROOT / "audio" / "manifest" / "audio_manifest.json"
REPORT = ROOT / "audio" / "manifest" / "generation_report.json"


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

    for audio_id in sorted(required_speech):
        path = SPEECH / f"{audio_id}.ogg"
        if not is_ogg(path):
            errors.append(f"missing/invalid speech OGG: {audio_id} -> {path.relative_to(ROOT)}")

    for sfx_id in sorted(required_sfx):
        path = SFX / f"{sfx_id}.ogg"
        if not is_ogg(path):
            errors.append(f"missing/invalid SFX OGG: {sfx_id} -> {path.relative_to(ROOT)}")

    if not MANIFEST.exists():
        errors.append("audio manifest missing")
        manifest_items = []
    else:
        manifest = json.loads(MANIFEST.read_text(encoding="utf-8"))
        manifest_items = manifest.get("items", [])
        manifest_ids = {
            item.get("audioId")
            for item in manifest_items
            if isinstance(item, dict) and isinstance(item.get("audioId"), str)
        }
        missing_from_manifest = sorted(required_speech - manifest_ids)
        for audio_id in missing_from_manifest:
            errors.append(f"required speech id absent from audio manifest: {audio_id}")

    if not REPORT.exists():
        errors.append("generation report missing")
        report = {}
    else:
        report = json.loads(REPORT.read_text(encoding="utf-8"))
        if report.get("failed") != 0:
            errors.append(f"generation report has failures: {report.get('failed')}")
        if report.get("generated", 0) + report.get("skipped", 0) != report.get("requested", 0):
            errors.append("generation report is incomplete")

    generated_speech = sorted(SPEECH.glob("*.ogg")) if SPEECH.exists() else []
    generated_sfx = sorted(SFX.glob("*.ogg")) if SFX.exists() else []

    if len(generated_speech) < len(required_speech):
        errors.append(
            f"speech file count {len(generated_speech)} is below required unique IDs {len(required_speech)}"
        )
    if len(generated_sfx) < len(required_sfx):
        errors.append(
            f"SFX file count {len(generated_sfx)} is below required unique IDs {len(required_sfx)}"
        )

    if errors:
        print(f"AUDIO VALIDATION FAILED ({len(errors)} error(s))")
        for error in errors:
            print(" -", error)
        return 1

    print("AUDIO VALIDATION PASSED")
    print(f" activity JSON files: {len(activity_files())}")
    print(f" required speech IDs: {len(required_speech)}")
    print(f" generated speech OGG: {len(generated_speech)}")
    print(f" required SFX IDs: {len(required_sfx)}")
    print(f" generated SFX OGG: {len(generated_sfx)}")
    print(f" manifest speech items: {len(manifest_items)}")
    print(
        " generation report:",
        {
            "requested": report.get("requested"),
            "generated": report.get("generated"),
            "skipped": report.get("skipped"),
            "failed": report.get("failed"),
        },
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
