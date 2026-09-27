#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

STATIC_PROMPTS = [
    {"audioId": "aud_common_success_01", "text": "Harika!", "voiceProfile": "TEACHER_ENCOURAGE"},
    {"audioId": "aud_common_retry_01", "text": "Bir daha bakalım.", "voiceProfile": "TEACHER_ENCOURAGE"},
    {"audioId": "aud_common_welcome_01", "text": "Minik Akademi'ye hoş geldin.", "voiceProfile": "TEACHER_WARM"},
    {"audioId": "aud_common_ready_01", "text": "Hazırsan başlayalım.", "voiceProfile": "TEACHER_WARM"},
]
for letter in ["a", "n", "e", "t", "i", "l", "o", "k", "u"]:
    STATIC_PROMPTS.append({"audioId": f"aud_phoneme_{letter}", "text": letter, "voiceProfile": "TEACHER_PHONICS"})

NUMBER_WORDS = {
    0:"sıfır",1:"bir",2:"iki",3:"üç",4:"dört",5:"beş",6:"altı",7:"yedi",8:"sekiz",9:"dokuz",
    10:"on",11:"on bir",12:"on iki",13:"on üç",14:"on dört",15:"on beş",16:"on altı",17:"on yedi",
    18:"on sekiz",19:"on dokuz",20:"yirmi",
}
for number, word in NUMBER_WORDS.items():
    STATIC_PROMPTS.append({"audioId": f"aud_number_{number:02d}", "text": word, "voiceProfile": "TEACHER_MATH"})

PROFILES = {
    "TEACHER_WARM": {"speed": 0.94, "pitch": 0},
    "TEACHER_PHONICS": {"speed": 0.86, "pitch": 0},
    "TEACHER_MATH": {"speed": 0.92, "pitch": 0},
    "TEACHER_STORY": {"speed": 0.94, "pitch": 0},
    "TEACHER_ENCOURAGE": {"speed": 0.98, "pitch": 0},
}

def collect() -> list[dict]:
    items = []
    for path in sorted(Path("content").rglob("*.json")):
        if path.name in {"activity_schema.json", "curriculum_manifest.json"}:
            continue
        try:
            data = json.loads(path.read_text(encoding="utf-8"))
        except Exception:
            continue
        instruction = data.get("instruction")
        if not isinstance(instruction, dict):
            continue
        audio_id = instruction.get("audioId")
        text = instruction.get("text")
        profile = instruction.get("voiceProfile")
        if audio_id and text and profile:
            items.append({
                "audioId": str(audio_id),
                "text": str(text).strip(),
                "voiceProfile": str(profile),
                "source": path.as_posix(),
            })
    return items + STATIC_PROMPTS

def main() -> None:
    by_id = {}
    for item in collect():
        profile = item["voiceProfile"]
        if profile not in PROFILES:
            raise SystemExit(f"Unknown profile: {profile}")
        old = by_id.get(item["audioId"])
        if old and (old["text"] != item["text"] or old["voiceProfile"] != profile):
            raise SystemExit(f"Conflicting prompt: {item['audioId']}")
        by_id[item["audioId"]] = item

    manifest = {
        "schemaVersion": "2.0",
        "provider": "MiniMax",
        "model": "speech-2.8-hd",
        "voice": "Turkish_CalmWoman",
        "languageBoost": "Turkish",
        "runtimeMode": "OFFLINE_PACKAGED_AUDIO",
        "profiles": PROFILES,
        "items": [by_id[k] for k in sorted(by_id)],
    }
    out = Path("audio/v2/full_manifest.json")
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Wrote {len(manifest['items'])} Audio V2 prompts")


if __name__ == "__main__":
    main()
