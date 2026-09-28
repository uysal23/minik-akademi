#!/usr/bin/env python3
from __future__ import annotations

import json
import re
from collections import Counter
from pathlib import Path
from typing import Any

ROOT = Path(__file__).resolve().parents[2]
CONTENT = ROOT / "content"

PRESSURE_OR_PUNITIVE = (
    "kaybettin",
    "başarısız",
    "ceza",
    "cezalı",
    "çok yavaş",
    "hızlı ol",
    "çabuk ol",
    "süre doldu",
    "puan kaybettin",
    "yanlış yaptın",
)

PREWRITING_TYPES = {"TRACE_PATH", "TRACE_SHAPE", "FIND_DIFFERENCE"}
LITERACY_TYPES = {
    "LETTER_INTRO",
    "FIND_LETTER",
    "FIND_SOUND_OBJECT",
    "TRACE_LETTER",
    "BUILD_SYLLABLE",
    "BUILD_WORD",
}
MATH_TYPES = {
    "TRACE_NUMBER",
    "MATCH_PAIR",
    "NUMBER_INTRO",
    "COUNT_OBJECTS",
    "RHYTHMIC_COUNT",
    "PATTERN_COMPLETE",
    "BUILD_SEQUENCE",
    "ADD_OBJECTS",
    "SUBTRACT_OBJECTS",
    "GROUP_OBJECTS",
    "POSITION_SELECT",
    "COMPARE_QUANTITY",
    "COMPARE_LENGTH",
    "COMPARE_MASS",
    "MEASURE_NONSTANDARD",
    "ORDER_ITEMS",
    "NUMBER_OBJECT_MATCH",
    "TAP_CHOICE",
    "MULTI_SELECT",
}
MINI_GAME_IDS = {
    "GAME-LIT-HUNT-001",
    "GAME-MATH-COUNT-001",
    "GAME-MATH-MATCH-001",
    "GAME-PRE-MAZE-001",
    "GAME-MATH-GROUP-001",
}
MULTI_CORRECT_TYPES = {
    "FIND_LETTER",
    "FIND_SOUND_OBJECT",
    "FIND_TARGET_SYMBOL",
    "MULTI_SELECT",
}
SINGLE_CORRECT_TYPES = {
    "COUNT_OBJECTS",
    "RHYTHMIC_COUNT",
    "PATTERN_COMPLETE",
    "BUILD_SEQUENCE",
    "ADD_OBJECTS",
    "SUBTRACT_OBJECTS",
    "GROUP_OBJECTS",
    "POSITION_SELECT",
    "COMPARE_QUANTITY",
    "COMPARE_LENGTH",
    "COMPARE_MASS",
    "MEASURE_NONSTANDARD",
    "ORDER_ITEMS",
    "NUMBER_OBJECT_MATCH",
    "TAP_CHOICE",
    "MAZE_TARGET_SYMBOL",
    "MATCH_PAIR",
}


def load_activities() -> list[tuple[Path, dict[str, Any]]]:
    out: list[tuple[Path, dict[str, Any]]] = []
    for path in sorted(CONTENT.rglob("*.json")):
        if path.name in {"activity_schema.json", "curriculum_manifest.json"}:
            continue
        out.append((path, json.loads(path.read_text(encoding="utf-8"))))
    return out


def option_value(option: dict[str, Any]) -> str:
    value = option.get("value", option.get("label", ""))
    return str(value).strip()


def tr_fold(value: str) -> str:
    # Turkish dotted/dotless I needs explicit normalization before Unicode casefold.
    return (
        value.replace("İ", "i")
        .replace("I", "ı")
        .casefold()
        .replace("\u0307", "")
    )


def audit() -> tuple[list[str], dict[str, Any]]:
    errors: list[str] = []
    activities = load_activities()
    ids = [a.get("id") for _, a in activities]
    counts = Counter(a.get("activityType", "<missing>") for _, a in activities)
    domains = Counter(a.get("domain", "<missing>") for _, a in activities)

    if len(activities) != 125:
        errors.append(f"Expected 125 authored activities, found {len(activities)}")
    if len(set(ids)) != len(ids):
        errors.append("Duplicate activity IDs exist")

    for path, a in activities:
        rel = path.relative_to(ROOT)
        aid = str(a.get("id", ""))
        domain = a.get("domain")
        typ = a.get("activityType")
        instruction = str(a.get("instruction", {}).get("text", "")).strip()
        title = str(a.get("title", "")).strip()
        target = a.get("learningTarget", {})
        target_symbol = str(target.get("targetSymbol", "")).strip()
        interaction = a.get("interaction", {})
        options = interaction.get("options") or []
        progression = a.get("progression", {})
        feedback = a.get("feedback", {})
        safety = a.get("safety", {})

        if not aid or not title or not instruction:
            errors.append(f"{rel}: id/title/instruction must be non-empty")

        lowered = f"{title} {instruction}".casefold()
        for phrase in PRESSURE_OR_PUNITIVE:
            if phrase in lowered:
                errors.append(f"{rel}: punitive/pressure wording found: {phrase!r}")

        if feedback.get("punitive") is not False:
            errors.append(f"{rel}: feedback.punitive must be false")
        for key in ("prohibitedContentPresent", "externalUrl", "ads", "purchase"):
            if safety.get(key) is not False:
                errors.append(f"{rel}: safety.{key} must be false")

        if progression.get("completionRule") == "COMPLETE_ON_ATTEMPT":
            errors.append(f"{rel}: pedagogical activities must not complete merely on attempt")

        if domain == "PREWRITING" and aid not in MINI_GAME_IDS and typ not in PREWRITING_TYPES:
            errors.append(f"{rel}: unsupported PREWRITING activityType {typ}")
        if domain == "LITERACY" and aid not in MINI_GAME_IDS and typ not in LITERACY_TYPES:
            errors.append(f"{rel}: unsupported LITERACY activityType {typ}")
        if domain == "MATHEMATICS" and aid not in MINI_GAME_IDS and typ not in MATH_TYPES:
            errors.append(f"{rel}: unsupported MATHEMATICS activityType {typ}")

        if options:
            option_ids = [o.get("id") for o in options if isinstance(o, dict)]
            if len(option_ids) != len(set(option_ids)):
                errors.append(f"{rel}: option IDs must be unique")
            if any(not str(o.get("label", o.get("value", ""))).strip() for o in options):
                errors.append(f"{rel}: every option must have a visible label/value")
            correct = [o for o in options if o.get("correct") is True]
            if not correct:
                errors.append(f"{rel}: options exist but none is correct")
            if typ in SINGLE_CORRECT_TYPES and len(correct) != 1:
                errors.append(f"{rel}: {typ} must have exactly one correct option, found {len(correct)}")
            if typ == "MATCH_PAIR" and aid == "GAME-MATH-MATCH-001":
                if len(correct) != 2:
                    errors.append(f"{rel}: pair-finding mini-game must contain exactly two correct cards")
                elif len({str(o.get("label", "")) for o in correct}) != 1:
                    errors.append(f"{rel}: pair-finding mini-game correct cards must visibly match")
        elif typ in SINGLE_CORRECT_TYPES | MULTI_CORRECT_TYPES:
            errors.append(f"{rel}: {typ} requires selectable options")

        if typ == "COUNT_OBJECTS":
            try:
                expected = int(target_symbol)
            except ValueError:
                errors.append(f"{rel}: COUNT_OBJECTS targetSymbol must be an integer")
            else:
                correct_values = {option_value(o) for o in options if o.get("correct") is True}
                if str(expected) not in correct_values:
                    errors.append(
                        f"{rel}: COUNT_OBJECTS target {expected} does not match correct option {sorted(correct_values)}"
                    )
                nr = target.get("numberRange")
                if isinstance(nr, dict) and not (nr.get("min", expected) <= expected <= nr.get("max", expected)):
                    errors.append(f"{rel}: count target is outside numberRange")

        if typ in {"ADD_OBJECTS", "SUBTRACT_OBJECTS", "GROUP_OBJECTS"}:
            m = re.search(r"(\d+)\s*([+\-x×])\s*(\d+)", target_symbol)
            if not m:
                errors.append(f"{rel}: arithmetic targetSymbol is not parseable: {target_symbol!r}")
            else:
                left, op, right = int(m.group(1)), m.group(2), int(m.group(3))
                expected = left + right if op == "+" else left - right if op == "-" else left * right
                correct_values = {option_value(o) for o in options if o.get("correct") is True}
                if str(expected) not in correct_values:
                    errors.append(
                        f"{rel}: arithmetic result {expected} does not match correct option {sorted(correct_values)}"
                    )

        if typ == "RHYTHMIC_COUNT":
            nums = [int(x) for x in re.findall(r"\d+", target_symbol)]
            correct = [o for o in options if o.get("correct") is True]
            if len(nums) >= 3 and len(correct) == 1:
                diffs = [b - a for a, b in zip(nums, nums[1:])]
                if len(set(diffs)) == 1:
                    expected = nums[-1] + diffs[-1]
                    if option_value(correct[0]) != str(expected):
                        errors.append(
                            f"{rel}: rhythmic sequence implies {expected}, correct option is {option_value(correct[0])}"
                        )

        if typ == "FIND_LETTER":
            correct = [o for o in options if o.get("correct") is True]
            for o in correct:
                label = tr_fold(str(o.get("label", o.get("value", ""))).strip())
                if target_symbol and label != tr_fold(target_symbol):
                    errors.append(f"{rel}: FIND_LETTER correct option {label!r} != target {target_symbol!r}")

        if typ in {"BUILD_SYLLABLE", "BUILD_WORD"}:
            allowed = target.get("allowedLetters")
            if isinstance(allowed, list) and target_symbol:
                extra = sorted({ch.casefold() for ch in target_symbol if ch.isalpha()} - {x.casefold() for x in allowed})
                if extra:
                    errors.append(f"{rel}: target uses letters not yet allowed: {extra}")

        if aid == "GAME-MATH-GROUP-001":
            if target_symbol != "10" or len(options) != 10 or not all(o.get("correct") is True for o in options):
                errors.append(f"{rel}: grouping mini-game must visibly build exactly one ten from 10 valid objects")

        if aid == "GAME-MATH-COUNT-001":
            if typ != "COUNT_OBJECTS":
                errors.append(f"{rel}: count mini-game must use COUNT_OBJECTS")

        if typ in {"TRACE_NUMBER", "TRACE_LETTER", "TRACE_PATH", "TRACE_SHAPE"}:
            if not target_symbol:
                errors.append(f"{rel}: trace activity requires targetSymbol")
            if interaction.get("mode") != "TRACE":
                errors.append(f"{rel}: trace activity must use TRACE interaction mode")
            if progression.get("completionRule") != "COMPLETE_ON_SUCCESS":
                errors.append(f"{rel}: trace activity must complete only on success")

    # Runtime engine guards: these are semantic requirements, not just syntax checks.
    tracing = (ROOT / "feature/tracing/src/main/java/com/uysal/minikakademi/feature/tracing/TracingScreens.kt").read_text(encoding="utf-8")
    literacy = (ROOT / "feature/literacy/src/main/java/com/uysal/minikakademi/feature/literacy/LiteracyScreens.kt").read_text(encoding="utf-8")
    mathematics = (ROOT / "feature/mathematics/src/main/java/com/uysal/minikakademi/feature/mathematics/MathematicsScreens.kt").read_text(encoding="utf-8")
    mini = (ROOT / "feature/mini-games/src/main/java/com/uysal/minikakademi/feature/minigames/MiniGamesScreens.kt").read_text(encoding="utf-8")

    forbidden_runtime = [
        (tracing, "visited.size.toFloat() / normalizedGuide.size.toFloat()", "prewriting trace unordered hit-map"),
        (tracing, "ratio >= 0.72f", "prewriting trace weak completion threshold"),
        (literacy, "visited.size.toFloat() / guide.size.toFloat()", "letter trace unordered hit-map"),
        (literacy, "ratio >= 0.70f", "letter trace weak completion threshold"),
        (mathematics, "ratio >= 0.68f", "digit trace weak completion threshold"),
    ]
    for text, token, label in forbidden_runtime:
        if token in text:
            errors.append(f"runtime: {label} is still present")

    required_runtime = [
        (tracing, "ratio >= 0.90f && reachedEnd", "prewriting ordered endpoint completion"),
        (literacy, "ratio >= 0.90f && reachedEnd", "letter ordered endpoint completion"),
        (mathematics, "ratio >= 0.90f && reachedEnd", "digit ordered endpoint completion"),
        (tracing, "onRetry()", "find-difference retry feedback"),
        (literacy, "private fun letterGuide(symbol: String): List<List<Offset>>", "multi-stroke letter guides"),
        (mathematics, "private fun digitGuide(digit: String): List<List<Offset>>", "multi-stroke digit guides"),
        (mini, '"GAME-LIT-HUNT-001"', "mini-game runtime coverage"),
        (mini, '"GAME-MATH-COUNT-001"', "mini-game runtime coverage"),
        (mini, '"GAME-MATH-MATCH-001"', "mini-game runtime coverage"),
        (mini, '"GAME-PRE-MAZE-001"', "mini-game runtime coverage"),
        (mini, '"GAME-MATH-GROUP-001"', "mini-game runtime coverage"),
    ]
    for text, token, label in required_runtime:
        if token not in text:
            errors.append(f"runtime: missing {label}")

    report = {
        "activities": len(activities),
        "domains": dict(sorted(domains.items())),
        "activityTypes": dict(sorted(counts.items())),
        "errors": len(errors),
    }
    return errors, report


def main() -> int:
    errors, report = audit()
    print("FULL CONTENT PEDAGOGICAL/SEMANTIC AUDIT")
    print(json.dumps(report, ensure_ascii=False, indent=2))
    if errors:
        print(f"AUDIT FAILED ({len(errors)} issue(s))")
        for err in errors:
            print(f" - {err}")
        return 1
    print("AUDIT PASSED")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
