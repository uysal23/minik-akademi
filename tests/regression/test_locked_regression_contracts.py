from __future__ import annotations

import json
import subprocess
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
BASELINE = ROOT / "tests" / "regression" / "locked_baseline_v1.json"
LOCK_INDEX = ROOT / "docs" / "LOCK_INDEX.md"
BUILD_PROCESS = ROOT / "BUILD_PROCESS.md"
STEP13_DOC = ROOT / "docs" / "MANUAL_UI_PEDAGOGICAL_REVIEW_V1.md"
STEP14_DOC = ROOT / "docs" / "REGRESSION_TESTS_V1.md"
STEP14_WORKFLOW = ROOT / ".github" / "workflows" / "regression-tests.yml"


def git_object_sha(path: str) -> str:
    return subprocess.check_output(
        ["git", "rev-parse", f"HEAD:{path}"],
        cwd=ROOT,
        text=True,
    ).strip()


class LockedRegressionContractsTest(unittest.TestCase):

    @classmethod
    def setUpClass(cls) -> None:
        cls.baseline = json.loads(BASELINE.read_text(encoding="utf-8"))

    def test_locked_runtime_trees_are_unchanged(self) -> None:
        for path, expected_sha in self.baseline["protectedTrees"].items():
            self.assertEqual(
                expected_sha,
                git_object_sha(path),
                msg=f"LOCKED runtime tree changed without a versioned unlock: {path}",
            )

    def test_locked_documents_are_unchanged(self) -> None:
        for path, expected_sha in self.baseline["protectedFiles"].items():
            self.assertEqual(
                expected_sha,
                git_object_sha(path),
                msg=f"LOCKED document changed without a versioned unlock: {path}",
            )

    def test_lock_index_contains_steps_1_through_14(self) -> None:
        text = LOCK_INDEX.read_text(encoding="utf-8")
        required = {
            1: "PROJECT_STRUCTURE_V1.md",
            2: "PRODUCT_SPEC_V1.md",
            3: "CURRICULUM_LOCK_V1.md",
            4: "AVATAR_SYSTEM_V1.md",
            5: "CONTENT_SCHEMA_V1.md",
            6: "Android App Shell",
            7: "Pre-Writing / Tracing",
            8: "LITERACY_MODULE_V1.md",
            9: "Matematik",
            10: "Mini Oyunlar",
            11: "ASSET_INTEGRATION_V1.md",
            12: "AUTOMATED_TESTS_V1.md",
            13: "MANUAL_UI_PEDAGOGICAL_REVIEW_V1.md",
            14: "REGRESSION_TESTS_V1.md",
        }
        for step, token in required.items():
            self.assertIn(token, text, msg=f"Missing locked Step {step}: {token}")
        self.assertGreaterEqual(text.count("| LOCKED |"), 24)

    def test_build_order_keeps_regression_before_release(self) -> None:
        text = BUILD_PROCESS.read_text(encoding="utf-8")
        step14 = "14. Regresyon testlerini tamamla."
        step15 = "15. Release APK build al."
        self.assertIn(step14, text)
        self.assertIn(step15, text)
        self.assertLess(text.index(step14), text.index(step15))

    def test_step13_remains_locked(self) -> None:
        text = STEP13_DOC.read_text(encoding="utf-8")
        self.assertIn("**Status:** LOCKED", text)
        self.assertIn("17 / 17", text)
        self.assertIn("Pedagogical UI Review #25", text)

    def test_step14_is_locked_in_baseline_and_document(self) -> None:
        self.assertEqual(14, self.baseline["lockedThroughStep"])
        text = STEP14_DOC.read_text(encoding="utf-8")
        self.assertIn("**Status:** LOCKED", text)
        self.assertIn("Regression Tests #11", text)
        self.assertIn("17 / 17 UI kanıt ekranı", text)

    def test_step14_never_builds_release_artifacts(self) -> None:
        text = STEP14_WORKFLOW.read_text(encoding="utf-8")
        forbidden = ["assembleRelease", "bundleRelease", "signingConfig", "apksigner"]
        for token in forbidden:
            self.assertNotIn(token, text)
        self.assertIn(":app:assembleDebug", text)

    def test_step14_replays_all_previous_quality_gates(self) -> None:
        text = STEP14_WORKFLOW.read_text(encoding="utf-8")
        required = [
            'python -m unittest discover -s tests -p "test_*.py" -v',
            "validate_content.py --mode authoring",
            "validate_audio_assets.py",
            "validate_visual_assets.py",
            ":core:model:testDebugUnitTest",
            ":core:navigation:testDebugUnitTest",
            "RegressionPersistenceTest",
            "PedagogicalUiReviewTest",
        ]
        for token in required:
            self.assertIn(token, text)


if __name__ == "__main__":
    unittest.main()
