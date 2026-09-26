from __future__ import annotations

import json
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CONTENT = ROOT / "content"
APP_MANIFEST = ROOT / "app" / "src" / "main" / "AndroidManifest.xml"


def activity_files() -> list[Path]:
    result = []
    for path in CONTENT.rglob("*.json"):
        if path.name in {"activity_schema.json", "curriculum_manifest.json"}:
            continue
        result.append(path)
    return sorted(result)


class ProjectContractsTest(unittest.TestCase):

    @classmethod
    def setUpClass(cls) -> None:
        cls.activities = []
        for path in activity_files():
            cls.activities.append((path, json.loads(path.read_text(encoding="utf-8"))))
        cls.manifest = json.loads((CONTENT / "curriculum_manifest.json").read_text(encoding="utf-8"))
        cls.curriculum_ids = {
            node["id"]
            for sequence in cls.manifest["sequences"]
            for node in sequence["nodes"]
        }

    def test_locked_content_counts(self) -> None:
        groups = {
            "prewriting": 0,
            "literacy": 0,
            "mathematics": 0,
            "mini_games": 0,
        }
        for path, _ in self.activities:
            rel = path.relative_to(CONTENT).as_posix()
            if rel.startswith("literacy/preparation/"):
                groups["prewriting"] += 1
            elif rel.startswith("literacy/"):
                groups["literacy"] += 1
            elif rel.startswith("mathematics/"):
                groups["mathematics"] += 1
            elif rel.startswith("mini_games/"):
                groups["mini_games"] += 1

        self.assertEqual(125, len(self.activities))
        self.assertEqual(5, groups["prewriting"])
        self.assertEqual(52, groups["literacy"])
        self.assertEqual(63, groups["mathematics"])
        self.assertEqual(5, groups["mini_games"])

    def test_activity_ids_are_unique_and_curriculum_mapped(self) -> None:
        ids = [data["id"] for _, data in self.activities]
        self.assertEqual(len(ids), len(set(ids)))
        for path, data in self.activities:
            self.assertIn(
                data["curriculumId"],
                self.curriculum_ids,
                msg=f"{path.relative_to(ROOT)} has unknown curriculumId",
            )

    def test_child_safety_flags_stay_non_punitive(self) -> None:
        for path, data in self.activities:
            feedback = data["feedback"]
            safety = data["safety"]
            self.assertFalse(feedback["punitive"], msg=str(path.relative_to(ROOT)))
            self.assertFalse(safety["prohibitedContentPresent"], msg=str(path.relative_to(ROOT)))
            self.assertFalse(safety["externalUrl"], msg=str(path.relative_to(ROOT)))
            self.assertFalse(safety["ads"], msg=str(path.relative_to(ROOT)))
            self.assertFalse(safety["purchase"], msg=str(path.relative_to(ROOT)))

    def test_extension_content_is_explicit(self) -> None:
        extensions = [data for _, data in self.activities if data["sourceType"] == "EXTENSION"]
        self.assertEqual({"EXT-ADD-01", "EXT-SUB-01", "EXT-MUL-01"}, {x["curriculumId"] for x in extensions})
        for data in extensions:
            self.assertIsNone(data["sourceRef"])

    def test_android_manifest_has_no_network_permission(self) -> None:
        manifest = APP_MANIFEST.read_text(encoding="utf-8")
        self.assertNotIn("android.permission.INTERNET", manifest)
        self.assertNotIn("android.permission.ACCESS_FINE_LOCATION", manifest)
        self.assertNotIn("android.permission.ACCESS_COARSE_LOCATION", manifest)
        self.assertNotIn("android.permission.RECORD_AUDIO", manifest)
        self.assertIn('android:usesCleartextTraffic="false"', manifest)
        self.assertIn('android:allowBackup="false"', manifest)

    def test_runtime_source_has_no_tts_or_network_client(self) -> None:
        forbidden = [
            "TextToSpeech(",
            "android.speech.tts",
            "HttpURLConnection",
            "OkHttpClient",
            "Retrofit.Builder",
            "WebView(",
        ]
        runtime_roots = [ROOT / "app", ROOT / "core", ROOT / "feature"]
        for base in runtime_roots:
            for path in base.rglob("*.kt"):
                text = path.read_text(encoding="utf-8")
                for token in forbidden:
                    self.assertNotIn(token, text, msg=f"{path.relative_to(ROOT)} contains {token}")

    def test_navigation_wires_every_locked_learning_module(self) -> None:
        route_source = (ROOT / "core/navigation/src/main/java/com/uysal/minikakademi/core/navigation/AppRoute.kt").read_text(encoding="utf-8")
        nav_source = (ROOT / "app/src/main/java/com/uysal/minikakademi/app/MinikAkademiNavHost.kt").read_text(encoding="utf-8")
        required_routes = [
            "TRACING_HOME",
            "TRACING_ACTIVITY_PATTERN",
            "LITERACY_HOME",
            "LITERACY_LETTER_PATTERN",
            "LITERACY_ACTIVITY_PATTERN",
            "MATHEMATICS_HOME",
            "MATHEMATICS_CATEGORY_PATTERN",
            "MATHEMATICS_ACTIVITY_PATTERN",
            "MINI_GAMES_HOME",
            "MINI_GAME_PATTERN",
            "PARENT_GATE",
            "PARENT_DASHBOARD",
            "PARENT_SETTINGS",
        ]
        for route in required_routes:
            self.assertIn(f"const val {route}", route_source)
            self.assertIn(f"AppRoute.{route}", nav_source)

    def test_progress_persists_for_all_learning_modules(self) -> None:
        source = (ROOT / "core/datastore/src/main/java/com/uysal/minikakademi/core/datastore/AppPreferencesRepository.kt").read_text(encoding="utf-8")
        for key in [
            "completed_tracing_activities",
            "completed_literacy_activities",
            "completed_mathematics_activities",
            "completed_mini_games",
        ]:
            self.assertIn(key, source)
        for method in [
            "markTracingActivityComplete",
            "markLiteracyActivityComplete",
            "markMathematicsActivityComplete",
            "markMiniGameComplete",
        ]:
            self.assertIn(f"suspend fun {method}", source)
        self.assertIn('MessageDigest.getInstance("SHA-256")', source)
        self.assertRegex(source, r"value\.coerceIn\(5, 30\)")

    def test_audio_and_visual_runtime_hooks_exist(self) -> None:
        nav_source = (ROOT / "app/src/main/java/com/uysal/minikakademi/app/MinikAkademiNavHost.kt").read_text(encoding="utf-8")
        for token in [
            "narrationEnabled = settings.narrationEnabled",
            "sfxEnabled = settings.sfxEnabled",
            "speechRate = settings.speechRate.multiplier",
        ]:
            self.assertGreaterEqual(nav_source.count(token), 4)

        audio_source = (ROOT / "core/audio/src/main/java/com/uysal/minikakademi/core/audio/OfflineAudioPlayer.kt").read_text(encoding="utf-8")
        self.assertIn('"speech/$audioId.ogg"', audio_source)
        self.assertIn('"sfx/$sfxId.ogg"', audio_source)
        self.assertNotIn("TextToSpeech", audio_source)

        avatar_source = (ROOT / "core/design-system/src/main/java/com/uysal/minikakademi/core/designsystem/MinikAkademiDesignSystem.kt").read_text(encoding="utf-8")
        self.assertEqual(12, len(re.findall(r"AvatarVisualSpec\(Color\(", avatar_source)))


if __name__ == "__main__":
    unittest.main()
