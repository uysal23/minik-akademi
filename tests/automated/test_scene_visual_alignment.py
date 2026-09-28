from __future__ import annotations

import json
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CONTENT = ROOT / "content"
MATH = ROOT / "feature/mathematics/src/main/java/com/uysal/minikakademi/feature/mathematics/MathematicsScreens.kt"
OBJECTS = ROOT / "core/design-system/src/main/java/com/uysal/minikakademi/core/designsystem/LearningObjectArt.kt"

FLAGGED = {
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
}


class SceneVisualAlignmentTest(unittest.TestCase):

    @classmethod
    def setUpClass(cls) -> None:
        cls.activities = []
        for path in CONTENT.rglob("*.json"):
            if path.name in {"activity_schema.json", "curriculum_manifest.json"}:
                continue
            cls.activities.append(json.loads(path.read_text(encoding="utf-8")))
        cls.ids = {x["id"] for x in cls.activities}
        cls.math = MATH.read_text(encoding="utf-8")
        cls.objects = OBJECTS.read_text(encoding="utf-8")

    def test_all_125_authored_scenes_remain_present(self) -> None:
        self.assertEqual(125, len(self.activities))
        self.assertEqual(125, len(self.ids))

    def test_all_34_flagged_scenes_are_accounted_for(self) -> None:
        self.assertEqual(34, len(FLAGGED))
        self.assertTrue(FLAGGED.issubset(self.ids))
        for scene_id in sorted(FLAGGED - {"ACT-EXT-SUB-01"}):
            self.assertIn(f'"{scene_id}"', self.math)

    def test_subtraction_visually_separates_removed_objects(self) -> None:
        self.assertIn('Text("$b nesne ayrıldı"', self.math)
        self.assertIn('Text("kalan: $remain"', self.math)
        self.assertIn('DotGroup(remain, "balon")', self.math)

    def test_semantically_dangerous_generic_cmp_eq_fallback_is_removed(self) -> None:
        self.assertNotIn('"CMP" in activityId || "EQ" in activityId', self.math)
        self.assertIn('QuantityComparison(left = 3, right = 3, relation = "=")', self.math)
        self.assertIn('QuantityComparison(left = 6, right = 4, relation = ">")', self.math)

    def test_tens_patterns_measurement_and_mass_have_dedicated_visuals(self) -> None:
        for token in (
            "TensOnesVisual(",
            "AbacusVisual(",
            "PatternConceptVisual(",
            "MeasurementChips(",
            "EqualBalanceVisual(",
            "OrdinalRow(",
        ):
            self.assertIn(token, self.math)

    def test_v2_object_renderer_contains_scene_specific_objects_and_depth(self) -> None:
        for label in (
            "kitap", "tüy", "pamuk", "karpuz", "çilek", "kalemlik",
            "sandalye", "ağaç", "muz", "kamyon", "pembe balon", "yeşil balon",
        ):
            self.assertIn(f'"{label}"', self.objects)
        self.assertIn("Soft grounding shadow", self.objects)
        self.assertIn("2.5D/CGI-inspired", self.objects)


if __name__ == "__main__":
    unittest.main()
