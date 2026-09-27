#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import random
import subprocess
from pathlib import Path

import numpy as np
import torch
import torchaudio as ta
from chatterbox.mtl_tts import ChatterboxMultilingualTTS

TEXT = (
    "Acele etmene hiç gerek yok. Önce birlikte dikkatlice bakalım. "
    "Hazır olduğunda, parmağınla yolu yavaşça takip edebilirsin."
)

PRESETS = [
    {"id": "gentle_a", "exaggeration": 0.25, "cfg_weight": 0.25, "temperature": 0.65, "seed": 271828},
    {"id": "gentle_b", "exaggeration": 0.30, "cfg_weight": 0.30, "temperature": 0.70, "seed": 314159},
    {"id": "gentle_c", "exaggeration": 0.25, "cfg_weight": 0.20, "temperature": 0.60, "seed": 618034},
]

def set_seed(seed: int) -> None:
    torch.manual_seed(seed)
    random.seed(seed)
    np.random.seed(seed)

def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--output-dir", default="audio/v4/chatterbox_samples")
    ap.add_argument("--report", default="audio/v4/chatterbox_report.json")
    args = ap.parse_args()

    out = Path(args.output_dir)
    out.mkdir(parents=True, exist_ok=True)

    print("Loading free Chatterbox Multilingual V3 on CPU.")
    print("No paid API or API key is used.")
    model = ChatterboxMultilingualTTS.from_pretrained("cpu", t3_model="v3")

    results = []
    for preset in PRESETS:
        set_seed(preset["seed"])
        wav = model.generate(
            TEXT,
            language_id="tr",
            exaggeration=preset["exaggeration"],
            cfg_weight=preset["cfg_weight"],
            temperature=preset["temperature"],
        )
        raw = out / f"{preset['id']}_raw.wav"
        ta.save(str(raw), wav, model.sr)

        ogg = out / f"{preset['id']}.ogg"
        # Gentle finish: no pitch shifting. Remove edge without making it muffled.
        af = (
            "atempo=0.94,"
            "highshelf=f=4200:g=-2.5:t=q:w=0.8,"
            "equalizer=f=6500:t=q:w=0.9:g=-2.0,"
            "equalizer=f=260:t=q:w=0.9:g=1.0,"
            "loudnorm=I=-20:LRA=13:TP=-3"
        )
        subprocess.run([
            "ffmpeg","-y","-loglevel","error",
            "-i",str(raw),"-ac","1","-ar","24000",
            "-af",af,
            "-c:a","libvorbis","-q:a","5",str(ogg)
        ], check=True)
        raw.unlink(missing_ok=True)

        results.append({
            **preset,
            "text": TEXT,
            "file": str(ogg),
            "engine": "Chatterbox Multilingual V3",
            "language": "tr",
        })

    report = {
        "version": "4.0-naturalness-test",
        "engine": "ResembleAI Chatterbox Multilingual V3",
        "license": "MIT",
        "paidApiUsed": False,
        "apiKeyRequired": False,
        "goal": "very soft, compassionate, graceful natural adult female teacher",
        "results": results,
    }
    rp = Path(args.report)
    rp.parent.mkdir(parents=True, exist_ok=True)
    rp.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

if __name__ == "__main__":
    main()
