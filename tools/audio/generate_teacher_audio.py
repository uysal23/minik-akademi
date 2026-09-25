#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
import os
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path


def deterministic_seed(audio_id: str) -> int:
    digest = hashlib.sha256(audio_id.encode("utf-8")).digest()
    return 100000 + int.from_bytes(digest[:4], "big") % 800000000


def run_one(
    item: dict,
    antalia_root: Path,
    prosody_presets: Path,
    output_root: Path,
    steps: int,
    force: bool,
) -> dict:
    audio_id = item["audioId"]
    output = output_root / f"{audio_id}.ogg"
    output.parent.mkdir(parents=True, exist_ok=True)

    if output.exists() and output.stat().st_size > 1024 and not force:
        return {"audioId": audio_id, "status": "skipped", "output": str(output)}

    preset = item["preset"]
    duration_scale = str(item.get("durationScale", 1.0))
    text = item["text"]
    seed = str(deterministic_seed(audio_id))

    with tempfile.TemporaryDirectory() as tmp:
        wav = Path(tmp) / f"{audio_id}.wav"
        cmd = [
            sys.executable,
            str(antalia_root / "scripts" / "synthesize-crossflow.py"),
            "--checkpoint", "cloud0day3/antalia-1",
            "--vocoder", "nvidia/bigvgan_v2_24khz_100band_256x",
            "--device", "cpu",
            "--speaker", "voicedata-candidate-b",
            "--prosody-presets", str(prosody_presets),
            "--preset", preset,
            "--preset-strength", "1.0",
            "--text-guidance", "4.0",
            "--speaker-guidance", "1.0",
            "--guidance-rescale", "0.5",
            "--sway", "-0.8",
            "--steps", str(steps),
            "--mel-clamp", "5.0",
            "--duration-scale", duration_scale,
            "--min-seconds-per-char", "0.085",
            "--chunk-chars", "120",
            "--chunk-pause-ms", "160",
            "--seed", seed,
            "--text", text,
            "--output", str(wav),
        ]

        env = os.environ.copy()
        bigvgan_root = env.get("BIGVGAN_ROOT", "/opt/bigvgan")
        env["PYTHONPATH"] = bigvgan_root + os.pathsep + env.get("PYTHONPATH", "")

        subprocess.run(
            cmd,
            cwd=antalia_root,
            env=env,
            check=True,
            timeout=1200,
        )

        if not wav.exists() or wav.stat().st_size < 1024:
            raise RuntimeError(f"Antalia did not create a valid WAV for {audio_id}")

        subprocess.run(
            [
                "ffmpeg", "-y", "-loglevel", "error",
                "-i", str(wav),
                "-ac", "1",
                "-ar", "24000",
                "-af", "loudnorm=I=-18:LRA=7:TP=-2",
                "-c:a", "libvorbis",
                "-q:a", "4",
                str(output),
            ],
            check=True,
            timeout=180,
        )

    return {
        "audioId": audio_id,
        "status": "generated",
        "output": str(output),
        "profile": item["voiceProfile"],
        "preset": preset,
        "seed": int(seed),
    }


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--manifest", default="audio/manifest/audio_manifest.json")
    parser.add_argument("--antalia-root", required=True)
    parser.add_argument("--prosody-presets", required=True)
    parser.add_argument("--output-dir", default="audio/generated/speech")
    parser.add_argument("--report", default="audio/manifest/generation_report.json")
    parser.add_argument("--steps", type=int, default=16)
    parser.add_argument("--max-items", type=int, default=0)
    parser.add_argument("--profile", action="append", default=[])
    parser.add_argument("--force", action="store_true")
    parser.add_argument("--allow-partial", action="store_true")
    args = parser.parse_args()

    if shutil.which("ffmpeg") is None:
        raise SystemExit("ffmpeg is required")

    manifest = json.loads(Path(args.manifest).read_text(encoding="utf-8"))
    items = manifest["items"]
    if args.profile:
        wanted = set(args.profile)
        items = [item for item in items if item["voiceProfile"] in wanted]
    if args.max_items > 0:
        items = items[: args.max_items]

    antalia_root = Path(args.antalia_root).resolve()
    presets = Path(args.prosody_presets).resolve()
    output_root = Path(args.output_dir).resolve()

    results: list[dict] = []
    failures: list[dict] = []

    print(f"Generating {len(items)} speech assets with Antalia 1 on CPU.")
    print("The APK will use only the generated OGG files; no runtime TTS is used.")

    for index, item in enumerate(items, start=1):
        audio_id = item["audioId"]
        print(f"[{index}/{len(items)}] {audio_id}: {item['text']}")
        try:
            results.append(
                run_one(
                    item=item,
                    antalia_root=antalia_root,
                    prosody_presets=presets,
                    output_root=output_root,
                    steps=args.steps,
                    force=args.force,
                )
            )
        except Exception as exc:
            print(f"FAILED {audio_id}: {exc}", file=sys.stderr)
            failures.append({"audioId": audio_id, "error": str(exc)})

    report = {
        "provider": "Antalia 1",
        "model": "cloud0day3/antalia-1",
        "steps": args.steps,
        "requested": len(items),
        "generated": sum(1 for x in results if x["status"] == "generated"),
        "skipped": sum(1 for x in results if x["status"] == "skipped"),
        "failed": len(failures),
        "results": results,
        "failures": failures,
    }
    report_path = Path(args.report)
    report_path.parent.mkdir(parents=True, exist_ok=True)
    report_path.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(json.dumps({k: report[k] for k in ("requested", "generated", "skipped", "failed")}, ensure_ascii=False))

    if failures and not args.allow_partial:
        raise SystemExit(2)
    if len(items) > 0 and not results:
        raise SystemExit(3)


if __name__ == "__main__":
    main()
