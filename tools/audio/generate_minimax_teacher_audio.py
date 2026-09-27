#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import os
import subprocess
import tempfile
import time
from pathlib import Path

import requests

API_KEY = os.environ.get("MINIMAX_API_KEY", "").strip()
API_BASE = os.environ.get("MINIMAX_API_BASE", "https://api.minimax.io/v1").rstrip("/")

PROFILE_SETTINGS = {
    "TEACHER_WARM": {"speed": 0.94, "pitch": 0},
    "TEACHER_PHONICS": {"speed": 0.86, "pitch": 0},
    "TEACHER_MATH": {"speed": 0.92, "pitch": 0},
    "TEACHER_STORY": {"speed": 0.94, "pitch": 0},
    "TEACHER_ENCOURAGE": {"speed": 0.98, "pitch": 0},
}


def request_wav(text: str, profile: str, model: str, voice: str, language_boost: str) -> bytes:
    if not API_KEY:
        raise RuntimeError("MINIMAX_API_KEY is not set")

    settings = PROFILE_SETTINGS[profile]
    payload = {
        "model": model,
        "text": text,
        "stream": False,
        "voice_setting": {
            "voice_id": voice,
            "speed": settings["speed"],
            "vol": 1.0,
            "pitch": settings["pitch"],
        },
        "audio_setting": {
            "sample_rate": 32000,
            "bitrate": 128000,
            "format": "wav",
            "channel": 1,
        },
        "language_boost": language_boost,
        "output_format": "hex",
    }

    last_error = None
    for attempt in range(1, 5):
        try:
            response = requests.post(
                f"{API_BASE}/t2a_v2",
                headers={
                    "Authorization": f"Bearer {API_KEY}",
                    "Content-Type": "application/json",
                },
                json=payload,
                timeout=180,
            )
            response.raise_for_status()
            data = response.json()
            base_resp = data.get("base_resp", {})
            if base_resp.get("status_code", 0) != 0:
                raise RuntimeError(
                    f"MiniMax API error {base_resp.get('status_code')}: "
                    f"{base_resp.get('status_msg')}"
                )
            audio_hex = data.get("data", {}).get("audio", "")
            if not audio_hex:
                raise RuntimeError("MiniMax response did not contain audio")
            return bytes.fromhex(audio_hex)
        except Exception as exc:
            last_error = exc
            if attempt == 4:
                break
            time.sleep(2.0 * attempt)
    raise RuntimeError(f"MiniMax request failed after retries: {last_error}")


def encode_ogg(wav_bytes: bytes, output: Path) -> None:
    output.parent.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory() as tmp:
        source = Path(tmp) / "source.wav"
        source.write_bytes(wav_bytes)
        subprocess.run(
            [
                "ffmpeg", "-y", "-loglevel", "error",
                "-i", str(source),
                "-ac", "1",
                "-ar", "24000",
                "-af",
                "silenceremove=start_periods=1:start_duration=0.03:start_threshold=-48dB:"
                "stop_periods=1:stop_duration=0.05:stop_threshold=-48dB,"
                "apad=pad_dur=0.10,loudnorm=I=-18:LRA=8:TP=-2",
                "-c:a", "libvorbis",
                "-q:a", "5",
                str(output),
            ],
            check=True,
            timeout=120,
        )
    if not output.exists() or output.stat().st_size < 1024:
        raise RuntimeError(f"Invalid output file: {output}")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--manifest", required=True)
    parser.add_argument("--output-dir", required=True)
    parser.add_argument("--report", required=True)
    parser.add_argument("--force", action="store_true")
    args = parser.parse_args()

    if not API_KEY:
        raise SystemExit("MINIMAX_API_KEY is required")

    manifest = json.loads(Path(args.manifest).read_text(encoding="utf-8"))
    model = manifest.get("model", "speech-2.8-hd")
    voice = manifest.get("voice", "Turkish_CalmWoman")
    language_boost = manifest.get("languageBoost", "Turkish")
    out_root = Path(args.output_dir)

    results = []
    failures = []

    for index, item in enumerate(manifest["items"], start=1):
        audio_id = item["audioId"]
        profile = item["voiceProfile"]
        if profile not in PROFILE_SETTINGS:
            failures.append({"audioId": audio_id, "error": f"Unknown profile {profile}"})
            continue
        output = out_root / f"{audio_id}.ogg"
        print(f"[{index}/{len(manifest['items'])}] {audio_id} [{profile}]")
        try:
            if output.exists() and output.stat().st_size > 1024 and not args.force:
                results.append({"audioId": audio_id, "status": "skipped", "output": str(output)})
                continue
            wav = request_wav(item["text"], profile, model, voice, language_boost)
            encode_ogg(wav, output)
            results.append({
                "audioId": audio_id,
                "status": "generated",
                "output": str(output),
                "bytes": output.stat().st_size,
                "profile": profile,
            })
        except Exception as exc:
            print(f"FAILED {audio_id}: {exc}")
            failures.append({"audioId": audio_id, "error": str(exc)})

    report = {
        "provider": "MiniMax",
        "model": model,
        "voice": voice,
        "requested": len(manifest["items"]),
        "generated": sum(x["status"] == "generated" for x in results),
        "skipped": sum(x["status"] == "skipped" for x in results),
        "failed": len(failures),
        "results": results,
        "failures": failures,
    }
    report_path = Path(args.report)
    report_path.parent.mkdir(parents=True, exist_ok=True)
    report_path.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

    if failures:
        raise SystemExit(2)


if __name__ == "__main__":
    main()
