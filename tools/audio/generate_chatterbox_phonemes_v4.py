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

VOICE = {
    "seed": 618034,
    "exaggeration": 0.25,
    "cfg_weight": 0.20,
    "temperature": 0.60,
}

# Symmetric/simple Turkish carrier words make the target phone easier to isolate.
SOURCES = {
    "a": {"text": "ana", "kind": "initial_vowel", "duration": 0.34},
    "e": {"text": "ele", "kind": "initial_vowel", "duration": 0.34},
    "i": {"text": "iki", "kind": "initial_vowel", "duration": 0.34},
    "o": {"text": "ona", "kind": "initial_vowel", "duration": 0.34},
    "u": {"text": "ulu", "kind": "initial_vowel", "duration": 0.34},
    "n": {"text": "ana", "kind": "middle_consonant", "duration": 0.18},
    "l": {"text": "ala", "kind": "middle_consonant", "duration": 0.18},
    "k": {"text": "aka", "kind": "middle_stop", "duration": 0.16},
    "t": {"text": "ata", "kind": "middle_stop", "duration": 0.16},
}

def set_seed(seed: int) -> None:
    torch.manual_seed(seed)
    random.seed(seed)
    np.random.seed(seed)

def speech_bounds(x: np.ndarray, sr: int) -> tuple[int, int]:
    # Envelope-based activity bounds, robust to Chatterbox head/tail silence.
    mono = np.abs(x)
    win = max(1, int(sr * 0.01))
    env = np.convolve(mono, np.ones(win) / win, mode="same")
    threshold = max(float(env.max()) * 0.08, 1e-4)
    active = np.flatnonzero(env >= threshold)
    if active.size == 0:
        return 0, len(x)
    pad = int(sr * 0.025)
    return max(0, int(active[0]) - pad), min(len(x), int(active[-1]) + pad)

def extract_phone(x: np.ndarray, sr: int, kind: str, duration: float) -> np.ndarray:
    start, end = speech_bounds(x, sr)
    active = x[start:end]
    if active.size == 0:
        raise RuntimeError("empty active speech")

    n = max(1, int(duration * sr))
    if kind == "initial_vowel":
        # Skip a tiny onset transient; capture only the first stable vowel.
        s = min(len(active) - 1, int(0.045 * sr))
        e = min(len(active), s + n)
        clip = active[s:e]
    else:
        # VCV carrier: target consonant lies near the acoustic midpoint.
        center = len(active) // 2
        if kind == "middle_stop":
            # Search around the midpoint for the lowest short-time energy
            # (closure), then include its release burst.
            radius = int(0.18 * sr)
            lo = max(0, center - radius)
            hi = min(len(active), center + radius)
            seg = active[lo:hi]
            w = max(1, int(0.012 * sr))
            sq = seg * seg
            energy = np.convolve(sq, np.ones(w) / w, mode="same")
            center = lo + int(np.argmin(energy))
            pre = int(0.055 * sr)
            post = n - pre
            s = max(0, center - pre)
            e = min(len(active), center + post)
        else:
            s = max(0, center - n // 2)
            e = min(len(active), s + n)
        clip = active[s:e]

    if clip.size < int(0.08 * sr):
        raise RuntimeError("extracted phone too short")

    # Short fades avoid clicks without adding a vowel.
    fade = min(int(0.008 * sr), clip.size // 4)
    if fade > 0:
        clip = clip.copy()
        clip[:fade] *= np.linspace(0.0, 1.0, fade, endpoint=False)
        clip[-fade:] *= np.linspace(1.0, 0.0, fade, endpoint=False)
    return clip

def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--output-dir", default="audio/v4/phoneme_qa")
    ap.add_argument("--report", default="audio/v4/phoneme_qa_report.json")
    args = ap.parse_args()

    out = Path(args.output_dir)
    out.mkdir(parents=True, exist_ok=True)

    print("Loading selected V4 C voice for isolated phoneme generation.")
    model = ChatterboxMultilingualTTS.from_pretrained("cpu", t3_model="v3")
    sr = model.sr
    results = []

    for phone, spec in SOURCES.items():
        set_seed(VOICE["seed"])
        wav = model.generate(
            spec["text"],
            language_id="tr",
            exaggeration=VOICE["exaggeration"],
            cfg_weight=VOICE["cfg_weight"],
            temperature=VOICE["temperature"],
        )
        x = wav.detach().cpu().numpy().reshape(-1).astype(np.float32)
        clip = extract_phone(x, sr, spec["kind"], spec["duration"])

        raw = out / f"aud_phoneme_{phone}.raw.wav"
        ta.save(str(raw), torch.from_numpy(clip).unsqueeze(0), sr)

        ogg = out / f"aud_phoneme_{phone}.ogg"
        subprocess.run([
            "ffmpeg","-y","-loglevel","error",
            "-i",str(raw),
            "-ac","1","-ar","24000",
            "-af","loudnorm=I=-20:LRA=7:TP=-3",
            "-c:a","libvorbis","-q:a","5",
            str(ogg),
        ], check=True)
        raw.unlink(missing_ok=True)

        probe = subprocess.check_output([
            "ffprobe","-v","error","-show_entries","format=duration",
            "-of","default=nw=1:nk=1",str(ogg)
        ], text=True).strip()
        duration = float(probe)
        if duration > 0.55:
            raise RuntimeError(f"{phone}: isolated phoneme too long ({duration:.3f}s)")

        results.append({
            "phoneme": phone,
            "carrier": spec["text"],
            "method": spec["kind"],
            "durationSeconds": round(duration, 3),
            "file": str(ogg),
        })
        print(f"{phone}: {duration:.3f}s from {spec['text']}")

    report = {
        "status": "PENDING_LISTENING_QA",
        "engine": "ResembleAI Chatterbox Multilingual V3",
        "selectedVoice": "C",
        "seed": VOICE["seed"],
        "paidApiUsed": False,
        "apiKeyRequired": False,
        "method": "carrier-word acoustic isolation; no letter-name synthesis",
        "results": results,
    }
    rp = Path(args.report)
    rp.parent.mkdir(parents=True, exist_ok=True)
    rp.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

if __name__ == "__main__":
    main()
