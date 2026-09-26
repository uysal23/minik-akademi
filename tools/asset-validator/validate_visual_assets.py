#!/usr/bin/env python3
from __future__ import annotations

import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CONTENT = ROOT / "content"
DESIGN = ROOT / "core" / "design-system" / "src" / "main" / "java" / "com" / "uysal" / "minikakademi" / "core" / "designsystem"
AVATAR_FILE = DESIGN / "MinikAkademiDesignSystem.kt"
OBJECT_FILE = DESIGN / "LearningObjectArt.kt"

REQUIRED_POSES = {
    "IDLE",
    "SMILE",
    "WAVE",
    "LISTEN",
    "POINT_LEFT",
    "POINT_RIGHT",
    "POINT_UP",
    "POINT_DOWN",
    "THINKING",
    "WALK_LEFT",
    "WALK_RIGHT",
    "HOLD_OBJECT",
    "CELEBRATE",
    "CLAP",
    "SIT",
    "SURPRISED_SOFT",
}


def collect_sound_object_labels() -> set[str]:
    labels: set[str] = set()
    for path in CONTENT.rglob("*.json"):
        if path.name in {"activity_schema.json", "curriculum_manifest.json"}:
            continue
        try:
            data = json.loads(path.read_text(encoding="utf-8"))
        except Exception:
            continue
        if data.get("activityType") != "FIND_SOUND_OBJECT":
            continue
        for option in data.get("interaction", {}).get("options", []):
            label = option.get("label")
            if isinstance(label, str) and label.strip():
                labels.add(label.strip().lower())
    return labels


def main() -> int:
    errors: list[str] = []

    if not AVATAR_FILE.exists():
        errors.append("avatar vector source missing")
        avatar_source = ""
    else:
        avatar_source = AVATAR_FILE.read_text(encoding="utf-8")

    if not OBJECT_FILE.exists():
        errors.append("learning object vector source missing")
        object_source = ""
    else:
        object_source = OBJECT_FILE.read_text(encoding="utf-8")

    avatar_count = len(re.findall(r"AvatarVisualSpec\(Color\(", avatar_source))
    if avatar_count != 12:
        errors.append(f"expected 12 avatar visual specs, found {avatar_count}")

    missing_poses = sorted(pose for pose in REQUIRED_POSES if pose not in avatar_source)
    if missing_poses:
        errors.append(f"missing avatar poses: {missing_poses}")

    labels = collect_sound_object_labels()
    missing_labels = sorted(
        label
        for label in labels
        if f'"{label}"' not in object_source
    )
    if missing_labels:
        errors.append(f"missing original object pictograms for literacy labels: {missing_labels}")

    required_visual_hooks = [
        ("literacy", ROOT / "feature" / "literacy" / "src" / "main" / "java" / "com" / "uysal" / "minikakademi" / "feature" / "literacy" / "LiteracyScreens.kt"),
        ("mathematics", ROOT / "feature" / "mathematics" / "src" / "main" / "java" / "com" / "uysal" / "minikakademi" / "feature" / "mathematics" / "MathematicsScreens.kt"),
        ("mini-games", ROOT / "feature" / "mini-games" / "src" / "main" / "java" / "com" / "uysal" / "minikakademi" / "feature" / "minigames" / "MiniGamesScreens.kt"),
    ]
    for name, path in required_visual_hooks:
        if not path.exists():
            errors.append(f"{name} screen source missing")
            continue
        source = path.read_text(encoding="utf-8")
        if "LearningObjectArt" not in source:
            errors.append(f"{name} does not connect LearningObjectArt")

    if "AvatarPlaceholder(" not in avatar_source or "Canvas(" not in avatar_source:
        errors.append("avatar UI is not backed by the final offline vector renderer")

    if errors:
        print(f"VISUAL VALIDATION FAILED ({len(errors)} error(s))")
        for error in errors:
            print(" -", error)
        return 1

    print("VISUAL VALIDATION PASSED")
    print(f" avatar identities: {avatar_count}")
    print(f" avatar poses: {len(REQUIRED_POSES)}")
    print(f" literacy object pictograms: {len(labels)}")
    print(" visual hooks: literacy, mathematics, mini-games")
    return 0


if __name__ == "__main__":
    sys.exit(main())
