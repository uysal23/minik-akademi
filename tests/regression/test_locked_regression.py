from __future__ import annotations

import json
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CONTENT = ROOT / "content"

LOCKED_DOCS = [
    ("docs/PROJECT_STRUCTURE_V1.md", 1),
    ("docs/PRODUCT_SPEC_V1.md", 2),
    ("docs/UI_FLOW_V1.md", 2),
    ("docs/CURRICULUM_LOCK_V1.md", 3),
    ("docs/SOURCE_MAPPING.md", 3),
    ("docs/AVATAR_SYSTEM_V1.md", 4),
    ("docs/VISUAL_STYLE_GUIDE.md", 4),
    ("docs/ANIMATION_RULES.md", 4),
    ("docs/AUDIO_STYLE_GUIDE.md", 4),
    ("docs/PARENT_SYSTEM_V1.md", 4),
    ("docs/ADMIN_PANEL_V1.md", 4),
    ("docs/THEME_ACCESSIBILITY_V1.md", 4),
    ("docs/CHILD_SAFETY_RULES.md", 4),
    ("docs/CONTENT_SCHEMA_V1.md", 5),
    ("docs/LITERACY_MODULE_V1.md", 8),
    ("docs/ASSET_INTEGRATION_V1.md", 11),
    ("docs/AUTOMATED_TESTS_V1.md", 12),
    ("docs/MANUAL_UI_PEDAGOGICAL_REVIEW_V1.md", 13),
]


def activity_files() -> list[Path]:
    return sorted(
        path
        for path in CONTENT.rglob("*.json")
        if path.name not in {"activity_schema.json", "curriculum_manifest.json"}
    )


class LockedRegressionContractsTest(unittest.TestCase):

    def test_build_process_keeps_release_as_step_15(self) -> None:
        text = (ROOT / "BUILD_PROCESS.md").read_text(encoding="utf-8")
        numbered = re.findall(r"(?m)^(\d+)\. ", text)
        self.assertEqual([str(i) for i in range(1, 16)], numbered[:15])
        self.assertIn("14. Regresyon testlerini tamamla.", text)
        self.assertIn("15. Release APK build al.", text)
        self.assertIn("**LOCKED — Build Process V1.0**", text)

    def test_steps_1_through_13_remain_locked_in_index(self) -> None:
        index = (ROOT / "docs" / "LOCK_INDEX.md").read_text(encoding="utf-8")
        rows = [line for line in index.splitlines() if line.startswith("|") and "LOCKED" in line]
        represented_steps = set()
        for row in rows:
            match = re.search(r"\|\s*(\d+)\s*\|\s*$", row)
            if match:
                represented_steps.add(int(match.group(1)))
        self.assertTrue(set(range(1, 14)).issubset(represented_steps))

    def test_locked_governance_documents_stay_locked(self) -> None:
        for relative, step in LOCKED_DOCS:
            path = ROOT / relative
            self.assertTrue(path.exists(), msg=f"Missing locked document: {relative}")
            text = path.read_text(encoding="utf-8")
            self.assertRegex(text, r"(?i)(Status:\*\* LOCKED|\*\*LOCKED)")
            if "Build Step:" in text:
                self.assertIn(f"**Build Step:** {step}", text)

    def test_curriculum_manifest_and_content_baseline_are_unchanged(self) -> None:
        manifest = json.loads((CONTENT / "curriculum_manifest.json").read_text(encoding="utf-8"))
        self.assertEqual("LOCKED", manifest["status"])
        self.assertEqual("tr-TR", manifest["locale"])
        self.assertEqual(["TR-01", "TR-02", "TR-03", "MA-01", "MA-02", "MA-03"], manifest["sourceSet"])

        activities = [json.loads(path.read_text(encoding="utf-8")) for path in activity_files()]
        self.assertEqual(125, len(activities))
        self.assertEqual(125, len({activity["id"] for activity in activities}))
        extensions = {a["curriculumId"] for a in activities if a["sourceType"] == "EXTENSION"}
        self.assertEqual({"EXT-ADD-01", "EXT-SUB-01", "EXT-MUL-01"}, extensions)

    def test_all_release_content_has_completed_safety_review(self) -> None:
        for path in activity_files():
            data = json.loads(path.read_text(encoding="utf-8"))
            self.assertTrue(data["safety"]["reviewed"], msg=str(path.relative_to(ROOT)))
            self.assertFalse(data["feedback"]["punitive"], msg=str(path.relative_to(ROOT)))
            for key in ("prohibitedContentPresent", "externalUrl", "ads", "purchase"):
                self.assertFalse(data["safety"][key], msg=f"{path.relative_to(ROOT)}: {key}")

    def test_android_stays_offline_and_child_safe(self) -> None:
        manifest = (ROOT / "app/src/main/AndroidManifest.xml").read_text(encoding="utf-8")
        for permission in (
            "android.permission.INTERNET",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.RECORD_AUDIO",
        ):
            self.assertNotIn(permission, manifest)
        self.assertIn('android:usesCleartextTraffic="false"', manifest)
        self.assertIn('android:allowBackup="false"', manifest)

    def test_step13_evidence_flow_still_covers_all_17_screens(self) -> None:
        source = (
            ROOT
            / "app/src/androidTest/java/com/uysal/minikakademi/app/PedagogicalUiReviewTest.kt"
        ).read_text(encoding="utf-8")
        expected = [
            "01_welcome",
            "02_adult_setup_gate",
            "03_child_profile",
            "04_learning_level",
            "05_avatar_selection",
            "06_offline_voice_speed",
            "07_theme",
            "08_parent_pin",
            "09_setup_summary",
            "10_child_home",
            "11_tracing_home",
            "12_tracing_activity",
            "13_literacy_home",
            "14_math_home",
            "15_math_category",
            "16_math_activity",
            "17_mini_games_locked",
        ]
        for name in expected:
            self.assertIn(f'shot("{name}")', source)
        self.assertEqual(17, source.count('shot("'))
        self.assertIn("previousShotDigest", source)
        self.assertIn("stableFrameCount", source)

    def test_locked_child_experience_strings_are_not_regressed(self) -> None:
        roots = [ROOT / "app", ROOT / "feature"]
        joined = "\n".join(
            path.read_text(encoding="utf-8")
            for base in roots
            for path in base.rglob("*.kt")
        )
        for required in (
            "Çiziyorum",
            "Harfleri Öğreniyorum",
            "Matematik Öğreniyorum",
            "Oyun Zamanı",
            "Biraz daha çalışınca açılacak",
            "Örneği Dinle",
            "🔊 Dinle",
        ):
            self.assertIn(required, joined)


if __name__ == "__main__":
    unittest.main()
