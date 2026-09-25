#!/usr/bin/env python3
from __future__ import annotations

import argparse
import math
import random
import shutil
import struct
import subprocess
import tempfile
import wave
from pathlib import Path

SAMPLE_RATE = 24000
PEAK = 0.32

SFX = {
    "sfx_soft_pop": "soft_pop",
    "sfx_gentle_tap": "gentle_tap",
    "sfx_success_chime": "success_chime",
    "sfx_star_sparkle": "star_sparkle",
    "sfx_slide_soft": "slide_soft",
    "sfx_retry_soft": "retry_soft",
    "sfx_complete": "complete",
}


def envelope(t: float, duration: float, attack: float = 0.015, release: float = 0.18) -> float:
    if t < attack:
        return max(0.0, min(1.0, t / max(attack, 1e-6)))
    tail_start = max(attack, duration - release)
    if t > tail_start:
        return max(0.0, (duration - t) / max(release, 1e-6))
    return 1.0


def tone(freq: float, t: float, phase: float = 0.0) -> float:
    return math.sin(2.0 * math.pi * freq * t + phase)


def render(kind: str) -> list[float]:
    if kind == "soft_pop":
        duration = 0.22
    elif kind == "gentle_tap":
        duration = 0.24
    elif kind == "success_chime":
        duration = 0.78
    elif kind == "star_sparkle":
        duration = 0.55
    elif kind == "slide_soft":
        duration = 0.42
    elif kind == "retry_soft":
        duration = 0.36
    else:
        duration = 0.95

    count = int(duration * SAMPLE_RATE)
    out: list[float] = []
    rng = random.Random(20260926)

    for i in range(count):
        t = i / SAMPLE_RATE
        x = 0.0

        if kind == "soft_pop":
            f = 220.0 + 110.0 * math.exp(-18.0 * t)
            x = 0.75 * tone(f, t) + 0.18 * tone(f * 2.0, t)
            x *= math.exp(-15.0 * t) * envelope(t, duration, 0.006, 0.08)

        elif kind == "gentle_tap":
            x = 0.70 * tone(390.0, t) + 0.22 * tone(780.0, t)
            x *= math.exp(-12.0 * t) * envelope(t, duration, 0.004, 0.11)

        elif kind == "success_chime":
            notes = [(523.25, 0.00), (659.25, 0.18), (783.99, 0.36)]
            for freq, start in notes:
                local = t - start
                if 0 <= local <= 0.38:
                    x += (tone(freq, local) + 0.20 * tone(freq * 2.0, local)) * math.exp(-5.8 * local)
            x *= envelope(t, duration, 0.008, 0.18)

        elif kind == "star_sparkle":
            notes = [(880.0, 0.00), (1174.66, 0.10), (1318.51, 0.20)]
            for freq, start in notes:
                local = t - start
                if 0 <= local <= 0.22:
                    x += 0.55 * tone(freq, local) * math.exp(-10.0 * local)
            x += (rng.random() * 2 - 1) * 0.025 * math.exp(-9.0 * t)
            x *= envelope(t, duration, 0.004, 0.14)

        elif kind == "slide_soft":
            progress = t / duration
            f = 180.0 + 150.0 * progress
            x = 0.38 * tone(f, t) + 0.10 * tone(f * 1.5, t)
            x += (rng.random() * 2 - 1) * 0.018
            x *= envelope(t, duration, 0.04, 0.12)

        elif kind == "retry_soft":
            x = 0.62 * tone(294.0, t) + 0.14 * tone(588.0, t)
            x *= math.exp(-7.5 * t) * envelope(t, duration, 0.006, 0.14)

        elif kind == "complete":
            notes = [(392.0, 0.00), (493.88, 0.18), (587.33, 0.36), (783.99, 0.54)]
            for freq, start in notes:
                local = t - start
                if 0 <= local <= 0.34:
                    x += (0.62 * tone(freq, local) + 0.12 * tone(freq * 2.0, local)) * math.exp(-5.2 * local)
            x *= envelope(t, duration, 0.008, 0.18)

        out.append(max(-1.0, min(1.0, x * PEAK)))

    return out


def write_wav(path: Path, samples: list[float]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with wave.open(str(path), "wb") as wav:
        wav.setnchannels(1)
        wav.setsampwidth(2)
        wav.setframerate(SAMPLE_RATE)
        frames = b"".join(
            struct.pack("<h", int(max(-1.0, min(1.0, s)) * 32767))
            for s in samples
        )
        wav.writeframes(frames)


def encode_ogg(wav_path: Path, ogg_path: Path) -> None:
    ogg_path.parent.mkdir(parents=True, exist_ok=True)
    if shutil.which("ffmpeg") is None:
        raise SystemExit("ffmpeg is required")
    subprocess.run(
        [
            "ffmpeg", "-y", "-loglevel", "error",
            "-i", str(wav_path),
            "-ac", "1",
            "-ar", str(SAMPLE_RATE),
            "-c:a", "libvorbis",
            "-q:a", "5",
            str(ogg_path),
        ],
        check=True,
    )


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--output-dir", default="audio/generated/sfx")
    args = parser.parse_args()
    output_dir = Path(args.output_dir)

    with tempfile.TemporaryDirectory() as temp_dir:
        temp = Path(temp_dir)
        for audio_id, kind in SFX.items():
            wav_path = temp / f"{audio_id}.wav"
            ogg_path = output_dir / f"{audio_id}.ogg"
            write_wav(wav_path, render(kind))
            encode_ogg(wav_path, ogg_path)
            print(ogg_path)


if __name__ == "__main__":
    main()
