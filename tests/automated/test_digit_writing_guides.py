from __future__ import annotations

import json
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SOURCE = (
    ROOT
    / "feature/mathematics/src/main/java/com/uysal/minikakademi/feature/mathematics/MathematicsScreens.kt"
)
DIGIT_DIR = ROOT / "content/mathematics/numbers"


class DigitWritingGuidesTest(unittest.TestCase):

    @classmethod
    def setUpClass(cls) -> None:
        cls.source = SOURCE.read_text(encoding="utf-8")
        start = cls.source.index("private fun DigitGuide".replace("DigitGuide", "digitGuide"))
        end = cls.source.index("\n\nprivate fun mathObjectFor", start)
        cls.guide = cls.source[start:end]

    def test_all_digits_have_explicit_stroke_guides(self) -> None:
        self.assertIn("private fun digitGuide(digit: String): List<List<Offset>>", self.guide)
        for digit in map(str, range(10)):
            self.assertIn(f'"{digit}" -> listOf(', self.guide)

    def test_known_legacy_geometry_is_removed(self) -> None:
        legacy = [
            'arc(0.48f, 0.38f, 0.23f, 0.17f, 200f, 20f, 30)',
            'arc(0.45f, 0.39f, 0.22f, 0.17f, 230f, 80f, 28)',
            'arc(0.51f, 0.58f, 0.22f, 0.25f, -40f, 300f, 48)',
            'arc(0.50f, 0.39f, 0.18f, 0.15f, -90f, 270f, 34)',
        ]
        for token in legacy:
            self.assertNotIn(token, self.guide)

    def test_zero_direction_and_two_shape_are_explicit(self) -> None:
        self.assertIn(
            "arc(0.50f, 0.52f, 0.22f, 0.32f, -90f, -450f, 64)",
            self.guide,
        )
        for token in (
            "Offset(0.30f, 0.36f)",
            "Offset(0.70f, 0.34f)",
            "Offset(0.31f, 0.78f)",
            "line(0.31f, 0.78f, 0.70f, 0.78f, 24)",
        ):
            self.assertIn(token, self.guide)

    def test_complex_digits_use_continuous_or_explicit_multistroke_forms(self) -> None:
        self.assertIn("// 1 iki hareket", self.guide)
        self.assertIn("// 4: eğik-aşağı + yatay hareket, sonra ayrı dik hareket.", self.guide)
        self.assertIn("// 5: önce aşağı inip alt kavsi tamamla", self.guide)
        self.assertIn("// 8 tek kesintisiz hareket", self.guide)
        self.assertIn("// 9: üst halkayı tamamla", self.guide)
        self.assertIn("fun cubic(", self.guide)
        self.assertIn("fun join(vararg parts: List<Offset>)", self.guide)

    def test_trace_engine_enforces_start_order_and_forward_progress(self) -> None:
        trace_start = self.source.index("private fun DigitTraceGame")
        trace_end = self.source.index("private fun DigitModelPreview", trace_start)
        trace = self.source[trace_start:trace_end]

        required = [
            "activeStrokeIndex",
            "activeProgressIndex",
            "distance(expectedStart, point) <= startTolerance()",
            "(from + 10).coerceAtMost(active.lastIndex)",
            "bestIndex >= activeProgressIndex",
            "if (ratio >= 0.80f)",
            "nextStroke >= guide.size",
            "Math.atan2",
            "middleY = size.height * 0.52f",
            "baselineY = size.height * 0.84f",
        ]
        for token in required:
            self.assertIn(token, trace)

        self.assertNotIn("visited.size.toFloat() / guide.size.toFloat()", trace)
        self.assertNotIn("ratio >= 0.68f", trace)

    def test_model_preview_uses_exact_same_vector_geometry(self) -> None:
        start = self.source.index("private fun DigitModelPreview")
        end = self.source.index("private fun digitGuide", start)
        preview = self.source[start:end]
        self.assertIn("val guide = remember(digit) { digitGuide(digit) }", preview)
        self.assertIn("stroke.zipWithNext()", preview)
        self.assertIn('text = "$digit rakamı"', preview)

    def test_all_digit_activities_request_directional_tracing(self) -> None:
        for digit in map(str, range(10)):
            path = DIGIT_DIR / f"mat_digit_{digit}.json"
            data = json.loads(path.read_text(encoding="utf-8"))
            self.assertEqual(f"ACT-MAT-DIGIT-{digit}", data["id"])
            self.assertEqual("TRACE_NUMBER", data["activityType"])
            self.assertEqual(digit, data["learningTarget"]["targetSymbol"])
            self.assertIn("yazılış yönüne uygun", data["instruction"]["text"])


if __name__ == "__main__":
    unittest.main()
