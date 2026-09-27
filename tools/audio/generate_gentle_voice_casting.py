#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

# Deterministic speaker identities for a fresh casting pass.
# Canonical Leyla seed is deliberately excluded.
SEEDS = [
    104729, 130363, 161803, 271828, 314159,
    424243, 577721, 618034, 707107, 866025,
    915965, 987653
]

PHRASES = [
    {
        "id": "gentle_instruction",
        "text": "Acele etmene hiç gerek yok. Önce şekle birlikte dikkatlice bakalım. Hazır olduğunda parmağınla yolu yavaşça takip edebilirsin."
    },
    {
        "id": "gentle_encourage",
        "text": "Çok güzel gidiyorsun. İstersen bir kez daha birlikte deneyelim."
    }
]

def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--freya-root", required=True)
    ap.add_argument("--output-dir", default="audio/v3/casting")
    ap.add_argument("--report", default="audio/v3/casting_report.json")
    ap.add_argument("--steps", type=int, default=32)
    args = ap.parse_args()

    if shutil.which("ffmpeg") is None:
        raise SystemExit("ffmpeg is required")

    freya_root = Path(args.freya_root).resolve()
    sys.path.insert(0, str(freya_root))
    from freyatts import FreyaTTS  # noqa: E402

    out_root = Path(args.output_dir)
    out_root.mkdir(parents=True, exist_ok=True)

    print("Loading FreyaTTS-small for V3 gentle-teacher voice casting.")
    print("Canonical Leyla voice is intentionally excluded.")
    tts = FreyaTTS.from_pretrained("freyavoice/freya-tts", device="cpu")

    results = []
    for seed in SEEDS:
        for phrase in PHRASES:
            audio_id = f"v3_seed_{seed}_{phrase['id']}"
            ogg = out_root / f"{audio_id}.ogg"
            with tempfile.TemporaryDirectory() as tmp:
                wav_path = Path(tmp) / f"{audio_id}.wav"
                wav = tts.synthesize(phrase["text"], steps=args.steps, seed=seed)
                tts.save_wav(wav, str(wav_path))

                # Extremely gentle finishing only; no fake pitch shifting.
                # Goal: remove edge, preserve natural formants and breath.
                af = (
                    "atempo=0.91,"
                    "highshelf=f=3600:g=-3.5:t=q:w=0.75,"
                    "equalizer=f=5200:t=q:w=1.0:g=-3.0,"
                    "equalizer=f=7600:t=q:w=0.9:g=-2.0,"
                    "equalizer=f=240:t=q:w=0.9:g=1.4,"
                    "lowpass=f=9800,"
                    "loudnorm=I=-20:LRA=13:TP=-3"
                )
                subprocess.run([
                    "ffmpeg","-y","-loglevel","error",
                    "-i",str(wav_path),
                    "-ac","1","-ar","24000",
                    "-af",af,
                    "-c:a","libvorbis","-q:a","5",
                    str(ogg)
                ], check=True, timeout=180)

            results.append({
                "seed": seed,
                "phrase": phrase["id"],
                "text": phrase["text"],
                "file": str(ogg),
                "processing": "V3_GENTLE_TEACHER"
            })
            print(f"generated {audio_id}")

    report = {
        "version": "3.0-casting",
        "goal": "very soft, compassionate, graceful adult female teacher voice",
        "model": "freyavoice/freya-tts",
        "license": "Apache-2.0",
        "paidApiUsed": False,
        "canonicalLeylaExcluded": True,
        "seedCount": len(SEEDS),
        "clipCount": len(results),
        "seeds": SEEDS,
        "results": results,
    }
    Path(args.report).parent.mkdir(parents=True, exist_ok=True)
    Path(args.report).write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

if __name__ == "__main__":
    main()
