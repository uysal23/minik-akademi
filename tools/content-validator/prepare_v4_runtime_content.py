#!/usr/bin/env python3
from __future__ import annotations

import json
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "content"
TARGET = ROOT / "content_v4" / "runtime"
OVERRIDES = ROOT / "content_v4" / "instruction_overrides_v1.json"


def main() -> None:
    data = json.loads(OVERRIDES.read_text(encoding="utf-8"))
    overrides = data.get("overrides", {})
    if data.get("status") != "OWNER_APPROVED" or len(overrides) != 20:
        raise SystemExit("Expected exactly 20 owner-approved instruction overrides")

    if TARGET.exists():
        shutil.rmtree(TARGET)
    shutil.copytree(SOURCE, TARGET)

    applied = 0
    for source_path, new_text in overrides.items():
        rel = Path(source_path).relative_to("content")
        target = TARGET / rel
        if not target.is_file():
            raise SystemExit(f"Override target missing: {source_path}")
        doc = json.loads(target.read_text(encoding="utf-8"))
        instruction = doc.get("instruction")
        if not isinstance(instruction, dict) or not instruction.get("audioId"):
            raise SystemExit(f"Override target has no spoken instruction: {source_path}")
        instruction["text"] = str(new_text)
        target.write_text(json.dumps(doc, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        applied += 1

    activities = [
        p for p in TARGET.rglob("*.json")
        if p.name not in {"activity_schema.json", "curriculum_manifest.json"}
    ]
    if len(activities) != 125:
        raise SystemExit(f"Runtime content activity count mismatch: {len(activities)}")
    if applied != 20:
        raise SystemExit(f"Instruction override count mismatch: {applied}")
    marker = {
        "contentRevision": "V4_AUDIO_TEXT_QA",
        "base": "LOCKED_V1_CONTENT",
        "instructionOverrides": 20,
        "ownerApproved": True,
    }
    (TARGET / "V4_CONTENT_READY.json").write_text(
        json.dumps(marker, ensure_ascii=False, indent=2) + "\n", encoding="utf-8"
    )
    print("V4 runtime content ready: 125 activities, 20 spoken-text overrides")


if __name__ == "__main__":
    main()
