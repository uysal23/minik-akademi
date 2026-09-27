#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path
from typing import Any

from jsonschema import Draft202012Validator

ROOT = Path(__file__).resolve().parents[2]
CONTENT_DIR = ROOT / "content_v4" / "runtime"
ASSETS_DIR = ROOT / "assets"
AUDIO_DIR = ROOT / "audio"

FORBIDDEN_KEYS = {
    "url",
    "uri",
    "remoteurl",
    "remoteuri",
    "streamurl",
    "ttsengine",
    "ttsprovider",
    "cloudtts",
    "apiurl",
}

AUDIO_EXTS = {".ogg", ".wav", ".mp3", ".m4a", ".aac", ".flac"}
VISUAL_EXTS = {".webp", ".png", ".jpg", ".jpeg", ".svg", ".json"}


def load_json(path: Path) -> Any:
    with path.open("r", encoding="utf-8") as f:
        return json.load(f)


def flatten_manifest(manifest: dict[str, Any]) -> tuple[dict[str, dict[str, Any]], list[str]]:
    errors: list[str] = []
    nodes: dict[str, dict[str, Any]] = {}
    sequences = manifest.get("sequences")
    if not isinstance(sequences, list):
        return {}, ["curriculum_manifest.json: 'sequences' must be an array"]

    for seq in sequences:
        seq_id = seq.get("id", "<unknown>")
        seq_nodes = seq.get("nodes")
        if not isinstance(seq_nodes, list):
            errors.append(f"{seq_id}: nodes must be an array")
            continue

        orders: set[int] = set()
        for n in seq_nodes:
            node_id = n.get("id")
            if not isinstance(node_id, str) or not node_id:
                errors.append(f"{seq_id}: node without valid id")
                continue
            if node_id in nodes:
                errors.append(f"duplicate curriculum node id: {node_id}")
            nodes[node_id] = n

            order = n.get("order")
            if not isinstance(order, int) or order < 1:
                errors.append(f"{node_id}: order must be positive integer")
            elif order in orders:
                errors.append(f"{seq_id}: duplicate order {order}")
            else:
                orders.add(order)

            page_range = n.get("pageRange")
            source_type = n.get("sourceType")
            if source_type in {"SOURCE", "ADAPTED"}:
                if n.get("sourceCode") is None:
                    errors.append(f"{node_id}: SOURCE/ADAPTED requires sourceCode")
                if not (
                    isinstance(page_range, list)
                    and len(page_range) == 2
                    and all(isinstance(x, int) and x >= 1 for x in page_range)
                    and page_range[0] <= page_range[1]
                ):
                    errors.append(f"{node_id}: invalid pageRange")
            elif source_type == "EXTENSION":
                if not node_id.startswith("EXT-"):
                    errors.append(f"{node_id}: EXTENSION id must start with EXT-")
            else:
                errors.append(f"{node_id}: invalid sourceType {source_type!r}")

    for node_id, n in nodes.items():
        for p in n.get("prerequisites", []):
            if p not in nodes:
                errors.append(f"{node_id}: missing curriculum prerequisite {p}")

    # Cycle detection across curriculum prerequisites.
    visiting: set[str] = set()
    visited: set[str] = set()

    def visit(node_id: str, stack: list[str]) -> None:
        if node_id in visited:
            return
        if node_id in visiting:
            cycle = " -> ".join(stack + [node_id])
            errors.append(f"curriculum cycle detected: {cycle}")
            return
        visiting.add(node_id)
        for p in nodes[node_id].get("prerequisites", []):
            if p in nodes:
                visit(p, stack + [node_id])
        visiting.remove(node_id)
        visited.add(node_id)

    for node_id in nodes:
        visit(node_id, [])

    return nodes, errors


def walk_keys(value: Any, path: str = "$") -> list[str]:
    errors: list[str] = []
    if isinstance(value, dict):
        for k, v in value.items():
            if k.lower() in FORBIDDEN_KEYS:
                errors.append(f"{path}.{k}: remote/runtime key is forbidden")
            errors.extend(walk_keys(v, f"{path}.{k}"))
    elif isinstance(value, list):
        for i, item in enumerate(value):
            errors.extend(walk_keys(item, f"{path}[{i}]"))
    return errors


def collect_stems(base: Path, exts: set[str]) -> set[str]:
    if not base.exists():
        return set()
    return {
        p.stem
        for p in base.rglob("*")
        if p.is_file() and p.suffix.lower() in exts
    }


def collect_activity_files() -> list[Path]:
    files: list[Path] = []
    for sub in ("literacy", "mathematics", "mini_games"):
        d = CONTENT_DIR / sub
        if d.exists():
            files.extend(sorted(d.rglob("*.json")))
    return files


def validate_activity(
    path: Path,
    activity: dict[str, Any],
    schema_validator: Draft202012Validator,
    curriculum_nodes: dict[str, dict[str, Any]],
    all_activity_ids: set[str],
    mode: str,
    audio_stems: set[str],
    visual_stems: set[str],
) -> list[str]:
    errors: list[str] = []
    rel = path.relative_to(ROOT)

    for e in schema_validator.iter_errors(activity):
        loc = ".".join(str(x) for x in e.absolute_path) or "$"
        errors.append(f"{rel}: schema {loc}: {e.message}")

    errors.extend(f"{rel}: {msg}" for msg in walk_keys(activity))

    activity_id = activity.get("id")
    curriculum_id = activity.get("curriculumId")
    node = curriculum_nodes.get(curriculum_id)
    if node is None:
        errors.append(f"{rel}: unknown curriculumId {curriculum_id!r}")
        return errors

    target = activity.get("learningTarget", {})
    if target.get("conceptId") != curriculum_id:
        errors.append(f"{rel}: learningTarget.conceptId must equal curriculumId")

    source_type = activity.get("sourceType")
    node_type = node.get("sourceType")
    if node_type == "EXTENSION":
        if source_type != "EXTENSION":
            errors.append(f"{rel}: EXTENSION curriculum node requires EXTENSION activity")
    else:
        if source_type == "EXTENSION":
            errors.append(f"{rel}: source-backed curriculum node cannot contain EXTENSION activity")
        if node_type == "ADAPTED" and source_type != "ADAPTED":
            errors.append(f"{rel}: ADAPTED curriculum node requires ADAPTED activity")

    source_ref = activity.get("sourceRef")
    if source_type in {"SOURCE", "ADAPTED"} and isinstance(source_ref, dict):
        if source_ref.get("sourceCode") != node.get("sourceCode"):
            errors.append(f"{rel}: sourceCode does not match curriculum manifest")
        ps = source_ref.get("pageStart")
        pe = source_ref.get("pageEnd")
        if isinstance(ps, int) and isinstance(pe, int):
            if ps > pe:
                errors.append(f"{rel}: pageStart cannot be greater than pageEnd")
            page_range = node.get("pageRange")
            if isinstance(page_range, list) and len(page_range) == 2:
                if ps < page_range[0] or pe > page_range[1]:
                    errors.append(
                        f"{rel}: source pages {ps}-{pe} exceed curriculum range "
                        f"{page_range[0]}-{page_range[1]}"
                    )

    allowed = target.get("allowedLetters")
    unlocked = node.get("unlockedLetters")
    if isinstance(allowed, list) and isinstance(unlocked, list):
        extra = sorted(set(allowed) - set(unlocked))
        if extra:
            errors.append(f"{rel}: letters not yet unlocked: {', '.join(extra)}")

    nr = target.get("numberRange")
    constraints = node.get("constraints", {})
    if isinstance(nr, dict) and isinstance(constraints, dict):
        nmin = constraints.get("numberMin")
        nmax = constraints.get("numberMax")
        amin = nr.get("min")
        amax = nr.get("max")
        if isinstance(amin, int) and isinstance(amax, int):
            if amin > amax:
                errors.append(f"{rel}: numberRange.min cannot exceed max")
            if isinstance(nmin, int) and amin < nmin:
                errors.append(f"{rel}: numberRange.min {amin} is below locked minimum {nmin}")
            if isinstance(nmax, int) and amax > nmax:
                errors.append(f"{rel}: numberRange.max {amax} exceeds locked maximum {nmax}")

    interaction = activity.get("interaction", {})
    options = interaction.get("options")
    if isinstance(options, list) and options:
        if not any(o.get("correct") is True for o in options if isinstance(o, dict)):
            errors.append(f"{rel}: options are present but no option is marked correct")

    prereqs = activity.get("prerequisites", [])
    for p in prereqs:
        if p not in curriculum_nodes and p not in all_activity_ids:
            errors.append(f"{rel}: unknown prerequisite {p}")

    for u in activity.get("progression", {}).get("unlocks", []):
        if u not in curriculum_nodes and u not in all_activity_ids:
            errors.append(f"{rel}: unknown unlock target {u}")

    if mode == "release":
        if activity.get("safety", {}).get("reviewed") is not True:
            errors.append(f"{rel}: safety.reviewed must be true in release mode")

        audio_ids = {
            activity.get("instruction", {}).get("audioId"),
            activity.get("feedback", {}).get("successAudioId"),
            activity.get("feedback", {}).get("retryAudioId"),
        }
        audio_ids.update(activity.get("audio", {}).get("extraAudioIds", []))
        for aid in sorted(x for x in audio_ids if isinstance(x, str)):
            if aid not in audio_stems:
                errors.append(f"{rel}: missing offline audio asset {aid}")

        visual_ids = set(activity.get("assets", {}).get("visualIds", []))
        bg = activity.get("assets", {}).get("backgroundId")
        if isinstance(bg, str):
            visual_ids.add(bg)
        for vid in sorted(x for x in visual_ids if isinstance(x, str)):
            if vid not in visual_stems:
                errors.append(f"{rel}: missing offline visual asset {vid}")

    return errors


def main() -> int:
    parser = argparse.ArgumentParser(description="Minik Akademi content validator")
    parser.add_argument(
        "--mode",
        choices=["authoring", "release"],
        default="authoring",
        help="release mode also requires reviewed safety and all offline assets",
    )
    args = parser.parse_args()

    schema_path = CONTENT_DIR / "activity_schema.json"
    manifest_path = CONTENT_DIR / "curriculum_manifest.json"

    errors: list[str] = []

    try:
        schema = load_json(schema_path)
        Draft202012Validator.check_schema(schema)
    except Exception as exc:
        print(f"ERROR: cannot load/validate activity schema: {exc}")
        return 1

    try:
        manifest = load_json(manifest_path)
    except Exception as exc:
        print(f"ERROR: cannot load curriculum manifest: {exc}")
        return 1

    if manifest.get("manifestVersion") != "1.0":
        errors.append("curriculum_manifest.json: manifestVersion must be 1.0")
    if manifest.get("status") != "LOCKED":
        errors.append("curriculum_manifest.json: status must be LOCKED")
    if manifest.get("locale") != "tr-TR":
        errors.append("curriculum_manifest.json: locale must be tr-TR")

    curriculum_nodes, manifest_errors = flatten_manifest(manifest)
    errors.extend(manifest_errors)

    activity_files = collect_activity_files()
    parsed_activities: list[tuple[Path, dict[str, Any]]] = []
    activity_ids: set[str] = set()

    for path in activity_files:
        try:
            activity = load_json(path)
        except Exception as exc:
            errors.append(f"{path.relative_to(ROOT)}: invalid JSON: {exc}")
            continue
        if not isinstance(activity, dict):
            errors.append(f"{path.relative_to(ROOT)}: activity root must be object")
            continue
        aid = activity.get("id")
        if isinstance(aid, str):
            if aid in activity_ids:
                errors.append(f"duplicate activity id: {aid}")
            activity_ids.add(aid)
        parsed_activities.append((path, activity))

    schema_validator = Draft202012Validator(schema)
    audio_stems = collect_stems(AUDIO_DIR, AUDIO_EXTS)
    visual_stems = collect_stems(ASSETS_DIR, VISUAL_EXTS)

    for path, activity in parsed_activities:
        errors.extend(
            validate_activity(
                path,
                activity,
                schema_validator,
                curriculum_nodes,
                activity_ids,
                args.mode,
                audio_stems,
                visual_stems,
            )
        )

    if errors:
        print(f"CONTENT VALIDATION FAILED ({len(errors)} error(s))")
        for err in errors:
            print(f" - {err}")
        return 1

    print("CONTENT VALIDATION PASSED")
    print(f" mode: {args.mode}")
    print(f" curriculum nodes: {len(curriculum_nodes)}")
    print(f" activity files: {len(parsed_activities)}")
    if not parsed_activities:
        print(" note: no authored activity JSON files yet; schema/manifest validation only")
    return 0


if __name__ == "__main__":
    sys.exit(main())
