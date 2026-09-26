from __future__ import annotations

import copy
import importlib.util
import json
import unittest
from pathlib import Path

from jsonschema import Draft202012Validator

ROOT = Path(__file__).resolve().parents[2]
VALIDATOR_PATH = ROOT / "tools" / "content-validator" / "validate_content.py"

spec = importlib.util.spec_from_file_location("minik_content_validator", VALIDATOR_PATH)
validator_module = importlib.util.module_from_spec(spec)
assert spec.loader is not None
spec.loader.exec_module(validator_module)


def load(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


class ContentValidatorContractTest(unittest.TestCase):

    @classmethod
    def setUpClass(cls) -> None:
        cls.schema = load(ROOT / "content" / "activity_schema.json")
        cls.manifest = load(ROOT / "content" / "curriculum_manifest.json")
        cls.curriculum_nodes, manifest_errors = validator_module.flatten_manifest(cls.manifest)
        assert not manifest_errors, manifest_errors
        cls.schema_validator = Draft202012Validator(cls.schema)

        cls.paths = sorted(
            p for p in (ROOT / "content").rglob("*.json")
            if p.name not in {"activity_schema.json", "curriculum_manifest.json"}
        )
        cls.activities = [(p, load(p)) for p in cls.paths]
        cls.activity_ids = {a["id"] for _, a in cls.activities}
        cls.audio_stems = validator_module.collect_stems(
            validator_module.AUDIO_DIR,
            validator_module.AUDIO_EXTS,
        )
        cls.visual_stems = validator_module.collect_stems(
            validator_module.ASSETS_DIR,
            validator_module.VISUAL_EXTS,
        )

    def validate(self, path: Path, activity: dict, mode: str = "authoring") -> list[str]:
        return validator_module.validate_activity(
            path,
            activity,
            self.schema_validator,
            self.curriculum_nodes,
            self.activity_ids,
            mode,
            self.audio_stems,
            self.visual_stems,
        )

    def find_activity(self, curriculum_id: str):
        for path, activity in self.activities:
            if activity["curriculumId"] == curriculum_id:
                return path, activity
        raise AssertionError(f"Missing fixture activity for {curriculum_id}")

    def test_real_authored_activity_passes(self) -> None:
        path, activity = self.find_activity("MAT-NUM-02")
        self.assertEqual([], self.validate(path, activity))

    def test_unknown_curriculum_id_fails(self) -> None:
        path, activity = self.find_activity("MAT-NUM-02")
        bad = copy.deepcopy(activity)
        bad["curriculumId"] = "UNKNOWN-NODE"
        errors = self.validate(path, bad)
        self.assertTrue(any("unknown curriculumId" in e for e in errors))

    def test_punitive_feedback_fails_schema(self) -> None:
        path, activity = self.find_activity("MAT-NUM-02")
        bad = copy.deepcopy(activity)
        bad["feedback"]["punitive"] = True
        errors = self.validate(path, bad)
        self.assertTrue(any("punitive" in e for e in errors))

    def test_external_url_flag_fails_schema(self) -> None:
        path, activity = self.find_activity("MAT-NUM-02")
        bad = copy.deepcopy(activity)
        bad["safety"]["externalUrl"] = True
        errors = self.validate(path, bad)
        self.assertTrue(any("externalUrl" in e for e in errors))

    def test_locked_number_range_violation_fails(self) -> None:
        path, activity = self.find_activity("MAT-NUM-02")
        bad = copy.deepcopy(activity)
        bad["learningTarget"]["numberRange"] = {"min": 0, "max": 999}
        errors = self.validate(path, bad)
        self.assertTrue(any("numberRange" in e or "number" in e for e in errors))

    def test_locked_letter_violation_fails(self) -> None:
        path, activity = self.find_activity("LIT-G1-A")
        bad = copy.deepcopy(activity)
        bad["learningTarget"]["allowedLetters"] = ["a", "z"]
        errors = self.validate(path, bad)
        self.assertTrue(any("letter" in e.lower() or "allowedLetters" in e for e in errors))

    def test_release_mode_rejects_unreviewed_activity(self) -> None:
        path, activity = self.find_activity("MAT-NUM-02")
        bad = copy.deepcopy(activity)
        bad["safety"]["reviewed"] = False
        errors = self.validate(path, bad, mode="release")
        self.assertTrue(any("safety.reviewed" in e for e in errors))

    def test_release_mode_rejects_missing_audio(self) -> None:
        path, activity = self.find_activity("MAT-NUM-02")
        bad = copy.deepcopy(activity)
        bad["safety"]["reviewed"] = True
        bad["instruction"]["audioId"] = "aud_missing_test_fixture"
        errors = self.validate(path, bad, mode="release")
        self.assertTrue(any("missing offline audio asset" in e for e in errors))

    def test_release_mode_rejects_missing_visual(self) -> None:
        path, activity = self.find_activity("MAT-NUM-02")
        bad = copy.deepcopy(activity)
        bad["safety"]["reviewed"] = True
        bad["assets"]["visualIds"] = ["visual_missing_test_fixture"]
        errors = self.validate(path, bad, mode="release")
        self.assertTrue(any("missing offline visual asset" in e for e in errors))

    def test_duplicate_ids_do_not_exist_in_repository(self) -> None:
        ids = [activity["id"] for _, activity in self.activities]
        self.assertEqual(len(ids), len(set(ids)))

    def test_malformed_json_fixture_is_rejected_by_parser(self) -> None:
        with self.assertRaises(json.JSONDecodeError):
            json.loads('{"id": "broken", }')


if __name__ == "__main__":
    unittest.main()
