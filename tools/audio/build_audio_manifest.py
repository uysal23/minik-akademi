#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
from pathlib import Path

STATIC_PROMPTS = [
    {"audioId": "aud_common_success_01", "text": "Harika!", "voiceProfile": "TEACHER_ENCOURAGE"},
    {"audioId": "aud_common_retry_01", "text": "Bir daha bakalım.", "voiceProfile": "TEACHER_ENCOURAGE"},
    {"audioId": "aud_common_welcome_01", "text": "Minik Akademi'ye hoş geldin.", "voiceProfile": "TEACHER_WARM"},
    {"audioId": "aud_common_ready_01", "text": "Hazırsan başlayalım.", "voiceProfile": "TEACHER_WARM"},
]

for letter in ["a", "n", "e", "t", "i", "l", "o", "k", "u"]:
    STATIC_PROMPTS.append({
        "audioId": f"aud_phoneme_{letter}",
        "text": letter,
        "voiceProfile": "TEACHER_PHONICS",
    })

NUMBER_WORDS = {
    0: "sıfır", 1: "bir", 2: "iki", 3: "üç", 4: "dört", 5: "beş",
    6: "altı", 7: "yedi", 8: "sekiz", 9: "dokuz", 10: "on",
    11: "on bir", 12: "on iki", 13: "on üç", 14: "on dört", 15: "on beş",
    16: "on altı", 17: "on yedi", 18: "on sekiz", 19: "on dokuz", 20: "yirmi",
}
for number, word in NUMBER_WORDS.items():
    STATIC_PROMPTS.append({
        "audioId": f"aud_number_{number:02d}",
        "text": word,
        "voiceProfile": "TEACHER_MATH",
    })

PROFILE_PRESETS = {
    "TEACHER_WARM": {"preset": "warm_voice_agent", "durationScale": 1.04},
    "TEACHER_PHONICS": {"preset": "phonetic_normalization", "durationScale": 1.16},
    "TEACHER_MATH": {"preset": "numeric_normalization", "durationScale": 1.06},
    "TEACHER_STORY": {"preset": "explanations_long_form", "durationScale": 1.05},
    "TEACHER_ENCOURAGE": {"preset": "restrained_emotion", "durationScale": 0.98},
}


def collect_instruction_prompts(content_root: Path) -> list[dict]:
    prompts: list[dict] = []
    for path in sorted(content_root.rglob("*.json")):
        if path.name in {"activity_schema.json", "curriculum_manifest.json"}:
            continue
        try:
            payload = json.loads(path.read_text(encoding="utf-8"))
        except Exception:
            continue
        instruction = payload.get("instruction")
        if not isinstance(instruction, dict):
            continue
        audio_id = instruction.get("audioId")
        text = instruction.get("text")
        profile = instruction.get("voiceProfile")
        if not (audio_id and text and profile):
            continue
        prompts.append({
            "audioId": str(audio_id),
            "text": str(text).strip(),
            "voiceProfile": str(profile),
            "source": str(path.as_posix()),
        })
    return prompts


def dedupe(prompts: list[dict]) -> list[dict]:
    by_id: dict[str, dict] = {}
    for prompt in prompts:
        audio_id = prompt["audioId"]
        profile = prompt["voiceProfile"]
        if profile not in PROFILE_PRESETS:
            raise SystemExit(f"Unknown voice profile {profile!r} for {audio_id}")
        existing = by_id.get(audio_id)
        if existing is not None:
            if existing["text"] != prompt["text"] or existing["voiceProfile"] != profile:
                raise SystemExit(
                    f"Conflicting prompt for {audio_id}: "
                    f"{existing['text']!r}/{existing['voiceProfile']} vs "
                    f"{prompt['text']!r}/{profile}"
                )
            continue
        preset = PROFILE_PRESETS[profile]
        by_id[audio_id] = {
            **prompt,
            "preset": preset["preset"],
            "durationScale": preset["durationScale"],
            "output": f"audio/generated/speech/{audio_id}.ogg",
        }
    return [by_id[key] for key in sorted(by_id)]


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--content-root", default="content")
    parser.add_argument("--output", default="audio/manifest/audio_manifest.json")
    args = parser.parse_args()

    content_root = Path(args.content_root)
    prompts = collect_instruction_prompts(content_root) + STATIC_PROMPTS
    manifest = {
        "schemaVersion": "1.0",
        "provider": "Antalia 1",
        "model": "cloud0day3/antalia-1",
        "runtimeMode": "OFFLINE_PACKAGED_AUDIO",
        "syntheticDisclosureRequired": True,
        "profiles": PROFILE_PRESETS,
        "items": dedupe(prompts),
    }

    output = Path(args.output)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(
        json.dumps(manifest, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    print(f"Wrote {len(manifest['items'])} speech prompts to {output}")


if __name__ == "__main__":
    main()
