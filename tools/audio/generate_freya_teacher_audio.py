#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

PROFILE_SPEED = {
    "TEACHER_WARM": 0.94,
    "TEACHER_PHONICS": 0.86,
    "TEACHER_MATH": 0.92,
    "TEACHER_STORY": 0.94,
    "TEACHER_ENCOURAGE": 0.98,
}

def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--manifest", default="audio/v2/sample_manifest.json")
    parser.add_argument("--freya-root", required=True)
    parser.add_argument("--output-dir", default="audio/v2/generated_samples")
    parser.add_argument("--report", default="audio/v2/sample_report.json")
    parser.add_argument("--steps", type=int, default=32)
    parser.add_argument("--force", action="store_true")
    args = parser.parse_args()

    if shutil.which("ffmpeg") is None:
        raise SystemExit("ffmpeg is required")

    freya_root = Path(args.freya_root).resolve()
    sys.path.insert(0, str(freya_root))

    from freyatts import FreyaTTS  # noqa: E402

    manifest = json.loads(Path(args.manifest).read_text(encoding="utf-8"))
    items = manifest["items"]
    output_root = Path(args.output_dir)
    output_root.mkdir(parents=True, exist_ok=True)

    print("Loading FreyaTTS-small (Apache-2.0) on CPU.")
    print("No paid API, API key, cloud TTS runtime, or device TTS is used.")
    tts = FreyaTTS.from_pretrained("freyavoice/freya-tts", device="cpu")

    results = []
    failures = []

    for index, item in enumerate(items, start=1):
        audio_id = item["audioId"]
        profile = item["voiceProfile"]
        text = item["text"]
        speed = PROFILE_SPEED.get(profile)
        if speed is None:
            failures.append({"audioId": audio_id, "error": f"Unknown profile: {profile}"})
            continue

        out_ogg = output_root / f"{audio_id}.ogg"
        if out_ogg.exists() and out_ogg.stat().st_size > 1024 and not args.force:
            results.append({"audioId": audio_id, "status": "skipped", "output": str(out_ogg)})
            continue

        print(f"[{index}/{len(items)}] {audio_id} [{profile}] {text}")
        try:
            with tempfile.TemporaryDirectory() as tmp:
                wav_path = Path(tmp) / f"{audio_id}.wav"
                wav = tts.synthesize(text, steps=args.steps)
                tts.save_wav(wav, str(wav_path))

                if not wav_path.exists() or wav_path.stat().st_size < 1024:
                    raise RuntimeError("FreyaTTS did not create a valid WAV")

                # Preserve pitch while applying the locked task-profile tempo,
                # normalize gently, then package as 24 kHz mono OGG for the APK.
                audio_filter = (
                    f"atempo={speed},"
                    "loudnorm=I=-18:LRA=11:TP=-2"
                )
                subprocess.run(
                    [
                        "ffmpeg", "-y", "-loglevel", "error",
                        "-i", str(wav_path),
                        "-ac", "1",
                        "-ar", "24000",
                        "-af", audio_filter,
                        "-c:a", "libvorbis",
                        "-q:a", "5",
                        str(out_ogg),
                    ],
                    check=True,
                    timeout=180,
                )

            results.append({
                "audioId": audio_id,
                "status": "generated",
                "output": str(out_ogg),
                "voiceProfile": profile,
                "speed": speed,
                "steps": args.steps,
                "voice": "FreyaTTS-small canonical Leyla seed",
            })
        except Exception as exc:
            print(f"FAILED {audio_id}: {exc}", file=sys.stderr)
            failures.append({"audioId": audio_id, "error": str(exc)})

    report = {
        "provider": "self-hosted open source",
        "model": "freyavoice/freya-tts (FreyaTTS-small)",
        "license": "Apache-2.0",
        "paidApiUsed": False,
        "apiKeyRequired": False,
        "runtimeTtsUsed": False,
        "sourceSampleRate": 48000,
        "packagedSampleRate": 24000,
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

    print(json.dumps({
        "requested": report["requested"],
        "generated": report["generated"],
        "skipped": report["skipped"],
        "failed": report["failed"],
    }, ensure_ascii=False))

    if failures:
        raise SystemExit(2)

if __name__ == "__main__":
    main()
