#!/usr/bin/env python3
from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from typing import Any

ROOT = Path(__file__).resolve().parents[2]
CONTENT = ROOT / "content"

EXPECTED_COUNTS = {
    "literacy": 52,
    "prewriting": 5,
    "mathematics": 63,
    "mini_games": 5,
}
EXPECTED_TOTAL = sum(EXPECTED_COUNTS.values())

LITERACY_TYPES = {
    "LETTER_INTRO",
    "FIND_LETTER",
    "FIND_SOUND_OBJECT",
    "TRACE_LETTER",
    "BUILD_SYLLABLE",
    "BUILD_WORD",
}
PREWRITING_TYPES = {"TRACE_PATH", "TRACE_SHAPE", "FIND_DIFFERENCE"}
MATH_TYPES = {
    "ADD_OBJECTS",
    "SUBTRACT_OBJECTS",
    "GROUP_OBJECTS",
    "COMPARE_LENGTH",
    "MEASURE_NONSTANDARD",
    "COMPARE_MASS",
    "ORDER_ITEMS",
    "RHYTHMIC_COUNT",
    "COMPARE_QUANTITY",
    "COUNT_OBJECTS",
    "TRACE_NUMBER",
    "NUMBER_INTRO",
    "PATTERN_COMPLETE",
    "TAP_CHOICE",
    "MATCH_PAIR",
    "POSITION_SELECT",
}
MINI_TYPES = {
    "MATCH_PAIR",
    "MULTI_SELECT",
    "FIND_TARGET_SYMBOL",
    "COUNT_OBJECTS",
    "MAZE_TARGET_SYMBOL",
}

SOUND_ART_LABELS = {
    "armut", "araba", "masa", "limon", "inek",
    "elma", "ceket", "tren", "balon", "telefon", "aslan",
    "tavşan", "mont", "terlik", "incir", "silgi", "kayık",
    "kelebek", "kalem", "koyun", "okul", "orman",
    "yumurta", "uçurtma", "uçak",
}

UNSAFE_PATTERNS = [
    r"\bsilah\b",
    r"\btabanca\b",
    r"\btüfek\b",
    r"\bbıçak\b",
    r"\balkol\b",
    r"\bsigara\b",
    r"\bkumar\b",
    r"\buyuşturucu\b",
    r"\bcinsel\b",
    r"\bporn",
    r"\böldür",
    r"\bintihar",
    r"\baptal\b",
    r"\bsalak\b",
    r"\bceza\b",
    r"\bsatın al\b",
    r"\breklam\b",
]

SELECTION_TYPES = {
    "FIND_LETTER", "FIND_SOUND_OBJECT", "COUNT_OBJECTS",
    "RHYTHMIC_COUNT", "PATTERN_COMPLETE", "TAP_CHOICE",
    "MATCH_PAIR", "POSITION_SELECT", "COMPARE_LENGTH",
    "MEASURE_NONSTANDARD", "COMPARE_MASS", "ORDER_ITEMS",
    "COMPARE_QUANTITY", "ADD_OBJECTS", "SUBTRACT_OBJECTS",
    "GROUP_OBJECTS", "FIND_TARGET_SYMBOL", "MULTI_SELECT",
    "MAZE_TARGET_SYMBOL",
}

TR_LOWER_TABLE = str.maketrans({"I": "ı", "İ": "i"})


def tr_lower(value: str) -> str:
    return value.translate(TR_LOWER_TABLE).lower()


def load(path: Path) -> dict[str, Any]:
    return json.loads(path.read_text(encoding="utf-8"))


def activity_files() -> list[Path]:
    return sorted(
        p
        for p in CONTENT.rglob("*.json")
        if p.name not in {"activity_schema.json", "curriculum_manifest.json"}
    )


def option_label(option: dict[str, Any]) -> str:
    value = option.get("label", option.get("value", ""))
    return str(value).strip()


def all_visible_strings(value: Any) -> list[str]:
    out: list[str] = []
    if isinstance(value, str):
        out.append(value)
    elif isinstance(value, dict):
        for key, child in value.items():
            if key in {"notes", "sourceCode", "audioId", "successAudioId", "retryAudioId"}:
                continue
            out.extend(all_visible_strings(child))
    elif isinstance(value, list):
        for child in value:
            out.extend(all_visible_strings(child))
    return out


def folder_group(path: Path) -> str:
    rel = path.relative_to(CONTENT).as_posix()
    if rel.startswith("literacy/group_"):
        return "literacy"
    if rel.startswith("literacy/preparation/"):
        return "prewriting"
    if rel.startswith("mathematics/"):
        return "mathematics"
    if rel.startswith("mini_games/"):
        return "mini_games"
    return "unknown"


def parse_int_label(label: str) -> int | None:
    return int(label) if re.fullmatch(r"-?\d+", label.strip()) else None


def exactly_one_correct(options: list[dict[str, Any]]) -> bool:
    return sum(o.get("correct") is True for o in options) == 1


def audit_activity(path: Path, doc: dict[str, Any], errors: list[str]) -> None:
    rel = path.relative_to(ROOT).as_posix()
    aid = str(doc.get("id", ""))
    kind = str(doc.get("activityType", ""))
    target = str(doc.get("learningTarget", {}).get("targetSymbol", ""))
    instruction = str(doc.get("instruction", {}).get("text", "")).strip()
    options = doc.get("interaction", {}).get("options", [])
    if not isinstance(options, list):
        options = []

    if not aid or not instruction or not kind:
        errors.append(f"{rel}: missing id/activityType/instruction")

    if len(instruction) < 5 or len(instruction) > 220:
        errors.append(f"{rel}: instruction length is not child-UI appropriate ({len(instruction)})")

    # Safety: this audit is the explicit source-content review gate.
    safety = doc.get("safety", {})
    if safety.get("reviewed") is not True:
        errors.append(f"{rel}: safety.reviewed must be true after Activity Quality Audit V1")
    for key in ("prohibitedContentPresent", "externalUrl", "ads", "purchase"):
        if safety.get(key) is not False:
            errors.append(f"{rel}: safety.{key} must be false")
    if doc.get("feedback", {}).get("punitive") is not False:
        errors.append(f"{rel}: feedback.punitive must be false")

    visible = "\n".join(all_visible_strings(doc))
    if re.search(r"https?://|www\.", visible, flags=re.I):
        errors.append(f"{rel}: child-visible content contains an external URL")
    lowered = tr_lower(visible)
    for pattern in UNSAFE_PATTERNS:
        if re.search(pattern, lowered, flags=re.I):
            errors.append(f"{rel}: child-visible content matched unsafe pattern {pattern!r}")

    # Options must be internally sound.
    if kind in SELECTION_TYPES:
        if len(options) < 2:
            errors.append(f"{rel}: selection activity needs at least two options")
        ids = [str(o.get("id", "")) for o in options if isinstance(o, dict)]
        if any(not x for x in ids) or len(ids) != len(set(ids)):
            errors.append(f"{rel}: option ids must be non-empty and unique")
        labels = [option_label(o) for o in options if isinstance(o, dict)]
        if any(not x for x in labels):
            errors.append(f"{rel}: option labels must be non-empty")
        if not any(o.get("correct") is True for o in options if isinstance(o, dict)):
            errors.append(f"{rel}: selection activity has no correct option")

    group = folder_group(path)
    allowed = {
        "literacy": LITERACY_TYPES,
        "prewriting": PREWRITING_TYPES,
        "mathematics": MATH_TYPES,
        "mini_games": MINI_TYPES,
    }.get(group, set())
    if kind not in allowed:
        errors.append(f"{rel}: activity type {kind!r} is not implemented for {group}")

    # Literacy semantics.
    if kind == "FIND_LETTER":
        for option in options:
            label = option_label(option)
            matches = tr_lower(label) == tr_lower(target)
            if bool(option.get("correct")) != matches:
                errors.append(f"{rel}: letter option {label!r} correctness disagrees with target {target!r}")

    if kind == "FIND_SOUND_OBJECT":
        t = tr_lower(target)
        for option in options:
            label = tr_lower(option_label(option))
            contains = t in label
            if bool(option.get("correct")) != contains:
                errors.append(f"{rel}: sound option {label!r} correctness disagrees with sound {target!r}")
            if option_label(option) not in SOUND_ART_LABELS:
                errors.append(f"{rel}: sound object {option_label(option)!r} has no approved object-art mapping")

    if kind == "TRACE_LETTER" and target not in set("anetiloku"):
        errors.append(f"{rel}: unsupported trace letter {target!r}")

    if kind in {"BUILD_SYLLABLE", "BUILD_WORD"}:
        allowed_letters = set("anet") if "group_01_anet" in rel else set("anetiloku")
        chars = {c for c in tr_lower(target) if c.isalpha()}
        if not chars or not chars.issubset(allowed_letters):
            errors.append(f"{rel}: target {target!r} uses letters outside the unlocked group")

    # Mathematical correctness.
    if kind == "TRACE_NUMBER":
        if target not in {str(i) for i in range(10)}:
            errors.append(f"{rel}: TRACE_NUMBER target must be 0..9")

    if kind == "COUNT_OBJECTS":
        expected = parse_int_label(target)
        correct = [parse_int_label(option_label(o)) for o in options if o.get("correct") is True]
        if expected is None or correct != [expected]:
            errors.append(f"{rel}: COUNT_OBJECTS correct answer must equal target {target!r}")

    if kind in {"ADD_OBJECTS", "SUBTRACT_OBJECTS", "GROUP_OBJECTS"}:
        op = {"ADD_OBJECTS": "+", "SUBTRACT_OBJECTS": "-", "GROUP_OBJECTS": "×"}[kind]
        parts = [x.strip() for x in target.split(op)]
        if len(parts) != 2 or not all(re.fullmatch(r"\d+", x) for x in parts):
            errors.append(f"{rel}: malformed arithmetic target {target!r}")
        else:
            a, b = map(int, parts)
            expected = a + b if op == "+" else a - b if op == "-" else a * b
            correct = [parse_int_label(option_label(o)) for o in options if o.get("correct") is True]
            if correct != [expected]:
                errors.append(f"{rel}: arithmetic correct answer {correct} != {expected}")

    if kind == "RHYTHMIC_COUNT":
        nums = [int(x) for x in re.findall(r"\d+", target)]
        if len(nums) >= 2 and "?" in target:
            step = nums[1] - nums[0]
            if any(nums[i] - nums[i - 1] != step for i in range(2, len(nums))):
                errors.append(f"{rel}: rhythmic sequence has inconsistent step")
            expected = nums[-1] + step
            correct = [parse_int_label(option_label(o)) for o in options if o.get("correct") is True]
            if correct != [expected]:
                errors.append(f"{rel}: rhythmic correct answer {correct} != {expected}")
        else:
            errors.append(f"{rel}: malformed rhythmic target {target!r}")

    if kind == "POSITION_SELECT":
        correct = [tr_lower(option_label(o)) for o in options if o.get("correct") is True]
        if correct != [tr_lower(target)]:
            errors.append(f"{rel}: position correct answer {correct} != target {target!r}")

    if aid == "ACT-MAT-CMP-01":
        correct = [option_label(o) for o in options if o.get("correct") is True]
        if correct != ["eşit"]:
            errors.append(f"{rel}: 3-vs-3 comparison must resolve to eşit")
    if aid == "ACT-MAT-CMP-02":
        correct = [option_label(o) for o in options if o.get("correct") is True]
        if correct != ["solda"]:
            errors.append(f"{rel}: 6-vs-4 comparison must resolve to solda")

    pattern_expectations = {
        "ACT-MAT-PAT-01": "sarı",
        "ACT-MAT-PAT-02": "üçgen",
        "ACT-MAT-PAT-03": "kare",
        "ACT-MAT-PAT-04": "yıldız",
    }
    if aid in pattern_expectations:
        correct = [option_label(o) for o in options if o.get("correct") is True]
        if correct != [pattern_expectations[aid]]:
            errors.append(f"{rel}: pattern answer {correct} != {pattern_expectations[aid]!r}")

    # Most single-choice activities must have exactly one correct answer.
    multi_correct_ids = {
        "GAME-MATH-MATCH-001",
        "GAME-MATH-GROUP-001",
        "GAME-LIT-HUNT-001",
    }
    if kind in SELECTION_TYPES and aid not in multi_correct_ids and not exactly_one_correct(options):
        errors.append(f"{rel}: expected exactly one correct option")

    if aid == "GAME-MATH-MATCH-001":
        if sum(o.get("correct") is True for o in options) != 2:
            errors.append(f"{rel}: pair-match game must contain exactly two matching correct cards")
    if aid == "GAME-MATH-GROUP-001":
        if len(options) != 10 or not all(o.get("correct") is True for o in options):
            errors.append(f"{rel}: grouping game must present exactly ten selectable unit objects")
    if aid == "GAME-LIT-HUNT-001":
        for option in options:
            matches = tr_lower(option_label(option)) == tr_lower(target)
            if bool(option.get("correct")) != matches:
                errors.append(f"{rel}: mini-game letter option correctness mismatch")


def audit_runtime_contracts(errors: list[str]) -> None:
    literacy = (
        ROOT
        / "feature/literacy/src/main/java/com/uysal/minikakademi/feature/literacy/LiteracyScreens.kt"
    ).read_text(encoding="utf-8")
    tracing = (
        ROOT
        / "feature/tracing/src/main/java/com/uysal/minikakademi/feature/tracing/TracingScreens.kt"
    ).read_text(encoding="utf-8")
    math = (
        ROOT
        / "feature/mathematics/src/main/java/com/uysal/minikakademi/feature/mathematics/MathematicsScreens.kt"
    ).read_text(encoding="utf-8")
    minigames = (
        ROOT
        / "feature/mini-games/src/main/java/com/uysal/minikakademi/feature/minigames/MiniGamesScreens.kt"
    ).read_text(encoding="utf-8")
    art = (
        ROOT
        / "core/design-system/src/main/java/com/uysal/minikakademi/core/designsystem/LearningObjectArt.kt"
    ).read_text(encoding="utf-8")

    # Letter tracing must be ordered, endpoint-aware and use the same geometry for the model.
    for token in (
        "activeStrokeIndex",
        "activeProgressIndex",
        "if (ratio >= 0.90f && reachedEnd)",
        "LetterModelPreview",
        "val guide = remember(symbol) { letterGuide(symbol) }",
        "Önce küçük harf",
        "val isVowel = normalized in setOf",
    ):
        if token not in literacy:
            errors.append(f"runtime literacy contract missing: {token}")
    for forbidden in (
        "visited.size.toFloat() / guide.size.toFloat()",
        "ratio >= 0.70f",
    ):
        if forbidden in literacy:
            errors.append(f"runtime literacy still contains legacy unordered tracing: {forbidden}")

    # Pre-writing paths must start at the designated point and reach the actual endpoint.
    for token in (
        "distance(expectedStart, point) <= startTolerance()",
        "if (!completed && ratio >= 0.90f && reachedEnd)",
        "progressIndex",
        "onRetry = ::playRetryAudio",
    ):
        if token not in tracing:
            errors.append(f"runtime prewriting contract missing: {token}")
    if "visited.size.toFloat() / normalizedGuide.size.toFloat()" in tracing:
        errors.append("runtime prewriting still contains legacy unordered completion")

    # Digit tracing is already locked by Digit Writing Audit V1; keep its core safeguards.
    for token in (
        "private fun digitGuide(digit: String): List<List<Offset>>",
        "if (ratio >= 0.90f && reachedEnd)",
        "DigitModelPreview",
    ):
        if token not in math:
            errors.append(f"runtime mathematics digit contract missing: {token}")

    # Every sound-object label must have a dedicated non-fallback drawing branch.
    for label in sorted(SOUND_ART_LABELS):
        if f'"{label}"' not in art:
            errors.append(f"LearningObjectArt is missing dedicated label {label!r}")

    # Authored mini games must have explicit runtime routes.
    for game_id in (
        "GAME-LIT-HUNT-001",
        "GAME-MATH-COUNT-001",
        "GAME-MATH-MATCH-001",
        "GAME-PRE-MAZE-001",
        "GAME-MATH-GROUP-001",
    ):
        if f'"{game_id}"' not in minigames:
            errors.append(f"mini-game runtime route missing: {game_id}")


def main() -> int:
    errors: list[str] = []
    files = activity_files()

    grouped = {key: 0 for key in EXPECTED_COUNTS}
    ids: set[str] = set()
    curriculum_ids: set[str] = set()
    extension_ids: set[str] = set()

    for path in files:
        group = folder_group(path)
        if group in grouped:
            grouped[group] += 1
        else:
            errors.append(f"{path.relative_to(ROOT)}: unexpected content folder")

        try:
            doc = load(path)
        except Exception as exc:
            errors.append(f"{path.relative_to(ROOT)}: invalid JSON: {exc}")
            continue

        aid = doc.get("id")
        cid = doc.get("curriculumId")
        if not isinstance(aid, str) or not aid:
            errors.append(f"{path.relative_to(ROOT)}: invalid activity id")
        elif aid in ids:
            errors.append(f"duplicate activity id: {aid}")
        else:
            ids.add(aid)

        if not isinstance(cid, str) or not cid:
            errors.append(f"{path.relative_to(ROOT)}: invalid curriculumId")
        else:
            curriculum_ids.add(cid)

        if doc.get("sourceType") == "EXTENSION":
            extension_ids.add(str(cid))

        audit_activity(path, doc, errors)

    if len(files) != EXPECTED_TOTAL:
        errors.append(f"expected {EXPECTED_TOTAL} activities, found {len(files)}")
    for group, expected in EXPECTED_COUNTS.items():
        actual = grouped[group]
        if actual != expected:
            errors.append(f"{group}: expected {expected} activities, found {actual}")

    if extension_ids != {"EXT-ADD-01", "EXT-SUB-01", "EXT-MUL-01"}:
        errors.append(f"unexpected extension set: {sorted(extension_ids)}")

    audit_runtime_contracts(errors)

    if errors:
        print(f"ACTIVITY QUALITY AUDIT FAILED ({len(errors)} issue(s))")
        for error in errors:
            print(f" - {error}")
        return 1

    print("ACTIVITY QUALITY AUDIT PASSED")
    print(f" total activities: {len(files)}")
    print(f" literacy: {grouped['literacy']}")
    print(f" prewriting: {grouped['prewriting']}")
    print(f" mathematics: {grouped['mathematics']}")
    print(f" mini games: {grouped['mini_games']}")
    print(" safety review: 125/125")
    print(" semantic answer checks: PASS")
    print(" sound-object visual coverage: PASS")
    print(" ordered tracing contracts (letters/digits/prewriting): PASS")
    return 0


if __name__ == "__main__":
    sys.exit(main())
