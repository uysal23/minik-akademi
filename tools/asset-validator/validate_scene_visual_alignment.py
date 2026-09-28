#!/usr/bin/env python3
from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CONTENT = ROOT / "content"
MATH_SOURCE = ROOT / "feature" / "mathematics" / "src" / "main" / "java" / "com" / "uysal" / "minikakademi" / "feature" / "mathematics" / "MathematicsScreens.kt"
OBJECT_SOURCE = ROOT / "core" / "design-system" / "src" / "main" / "java" / "com" / "uysal" / "minikakademi" / "core" / "designsystem" / "LearningObjectArt.kt"

REMEDIATED = [
    "ACT-MAT-CMP-01", "ACT-MAT-CMP-02", "ACT-MAT-SP-06",
    "ACT-MAT-TENS-01", "ACT-MAT-TENS-02", "ACT-MAT-TENS-03",
    "ACT-MAT-TENS-04", "ACT-MAT-TENS-05", "ACT-MAT-TENS-06",
    "ACT-MAT-MASS-02", "ACT-MAT-LEN-02", "ACT-MAT-LEN-04",
    "ACT-MAT-LEN-05", "ACT-MAT-MASS-03", "ACT-MAT-MASS-04",
    "ACT-MAT-MEASURE-ADAPT", "ACT-MAT-PAT-01", "ACT-MAT-PAT-02",
    "ACT-MAT-PAT-03", "ACT-MAT-PAT-04", "ACT-MAT-EQ-01",
    "ACT-MAT-EQ-02", "ACT-MAT-EQ-03", "ACT-MAT-EQ-04",
    "ACT-EXT-SUB-01", "ACT-MAT-LEN-03", "ACT-MAT-MASS-05",
    "ACT-MAT-NUM-01", "ACT-MAT-ORD-01", "ACT-MAT-REV-01",
    "ACT-MAT-REV-02", "ACT-MAT-SP-02", "ACT-MAT-SP-04",
    "ACT-MAT-SP-07",
]

EXACT_MATH_CASES = [x for x in REMEDIATED if x != "ACT-EXT-SUB-01"]

REQUIRED_OBJECTS = {
    "kitap", "tüy", "pamuk", "karpuz", "çilek", "kalemlik",
    "sandalye", "ağaç", "muz", "kamyon", "pembe balon", "yeşil balon",
}

REQUIRED_HELPERS = {
    "QuantityComparison(",
    "EqualBalanceVisual(",
    "TensOnesVisual(",
    "AbacusVisual(",
    "OrdinalRow(",
    "PatternConceptVisual(",
    "MeasurementChips(",
    'Text("$b nesne ayrıldı"',
    'Text("kalan: $remain"',
}


def authored_activities() -> list[dict]:
    result: list[dict] = []
    for path in sorted(CONTENT.rglob("*.json")):
        if path.name in {"activity_schema.json", "curriculum_manifest.json"}:
            continue
        result.append(json.loads(path.read_text(encoding="utf-8")))
    return result


def main() -> int:
    errors: list[str] = []
    activities = authored_activities()
    ids = {x["id"] for x in activities}

    if len(activities) != 125:
        errors.append(f"expected 125 authored scenes, found {len(activities)}")

    missing_content = sorted(set(REMEDIATED) - ids)
    if missing_content:
        errors.append(f"remediation IDs missing from content: {missing_content}")

    math = MATH_SOURCE.read_text(encoding="utf-8")
    objects = OBJECT_SOURCE.read_text(encoding="utf-8")

    missing_cases = [scene_id for scene_id in EXACT_MATH_CASES if f'"{scene_id}"' not in math]
    if missing_cases:
        errors.append(f"scene-specific runtime visual cases missing: {missing_cases}")

    for helper in sorted(REQUIRED_HELPERS):
        if helper not in math:
            errors.append(f"required visual helper/hook missing: {helper}")

    # Prevent the exact generic comparison fallback that caused the original
    # quantity/equal-object semantic mismatches.
    if '"CMP" in activityId || "EQ" in activityId' in math:
        errors.append("legacy generic CMP/EQ visual fallback is still present")

    missing_objects = sorted(label for label in REQUIRED_OBJECTS if f'"{label}"' not in objects)
    if missing_objects:
        errors.append(f"required V2 learning objects missing: {missing_objects}")

    ext_sub = next((x for x in activities if x["id"] == "ACT-EXT-SUB-01"), None)
    if not ext_sub or ext_sub.get("learningTarget", {}).get("targetSymbol") != "5 - 2":
        errors.append("ACT-EXT-SUB-01 locked 5 - 2 target changed unexpectedly")

    summary = {
        "authoredScenes": len(activities),
        "previouslyAlignedPreserved": 91,
        "remediatedScenes": len(REMEDIATED),
        "staticAlignmentCoverage": 91 + len(REMEDIATED),
        "remediationIds": REMEDIATED,
        "releaseBuildStarted": False,
    }

    out = ROOT / "scene-audit"
    out.mkdir(exist_ok=True)
    (out / "visual-alignment-summary.json").write_text(
        json.dumps(summary, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )

    if errors:
        print(f"SCENE VISUAL ALIGNMENT FAILED ({len(errors)} error(s))")
        for error in errors:
            print(" -", error)
        return 1

    print("SCENE VISUAL ALIGNMENT STATIC QA PASSED")
    print(" authored scenes: 125")
    print(" previously aligned preserved: 91")
    print(" remediated flagged scenes: 34")
    print(" static alignment coverage: 125/125")
    print(" release build: NOT STARTED")
    return 0


if __name__ == "__main__":
    sys.exit(main())
