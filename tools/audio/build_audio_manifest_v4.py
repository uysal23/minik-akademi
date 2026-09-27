#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

STATIC_PROMPTS = [
    {"audioId":"aud_common_success_01","text":"Çok güzel, harika gidiyorsun.","voiceProfile":"TEACHER_ENCOURAGE"},
    {"audioId":"aud_common_retry_01","text":"İstersen bir daha birlikte bakalım.","voiceProfile":"TEACHER_ENCOURAGE"},
    {"audioId":"aud_common_welcome_01","text":"Minik Akademi'ye hoş geldin.","voiceProfile":"TEACHER_WARM"},
    {"audioId":"aud_common_ready_01","text":"Hazırsan birlikte başlayalım.","voiceProfile":"TEACHER_WARM"},
]
for letter in ["a","n","e","t","i","l","o","k","u"]:
    STATIC_PROMPTS.append({
        "audioId":f"aud_phoneme_{letter}",
        "text":letter,
        "spokenText":letter,
        "voiceProfile":"TEACHER_PHONICS",
        "manualQaRequired":True,
    })

NUMBER_WORDS={
    0:"sıfır",1:"bir",2:"iki",3:"üç",4:"dört",5:"beş",6:"altı",7:"yedi",8:"sekiz",9:"dokuz",
    10:"on",11:"on bir",12:"on iki",13:"on üç",14:"on dört",15:"on beş",16:"on altı",17:"on yedi",
    18:"on sekiz",19:"on dokuz",20:"yirmi",
}
for n,w in NUMBER_WORDS.items():
    STATIC_PROMPTS.append({
        "audioId":f"aud_number_{n:02d}",
        "text":w,
        "spokenText":w,
        "voiceProfile":"TEACHER_MATH",
    })

SPOKEN_OVERRIDES={
    "aud_act_ext_add_01_instruction":"Üç nesneye iki nesne daha ekle. Toplamı seç.",
    "aud_act_ext_mul_01_instruction":"Üç grupta ikişer nesne var. Toplamı seç.",
    "aud_act_ext_sub_01_instruction":"Beş nesneden iki nesneyi ayır. Kalanı seç.",
    "aud_act_mat_back_01_instruction":"Yirmiden geriye birer saymayı tamamla.",
    "aud_act_mat_back_02_instruction":"Yirmiden geriye ikişer saymayı tamamla.",
    "aud_act_mat_digit_0_instruction":"Sıfır rakamını yazılış yönüne uygun olarak parmağınla izle.",
    "aud_act_mat_digit_1_instruction":"Bir rakamını yazılış yönüne uygun olarak parmağınla izle.",
    "aud_act_mat_digit_2_instruction":"İki rakamını yazılış yönüne uygun olarak parmağınla izle.",
    "aud_act_mat_digit_3_instruction":"Üç rakamını yazılış yönüne uygun olarak parmağınla izle.",
    "aud_act_mat_digit_4_instruction":"Dört rakamını yazılış yönüne uygun olarak parmağınla izle.",
    "aud_act_mat_digit_5_instruction":"Beş rakamını yazılış yönüne uygun olarak parmağınla izle.",
    "aud_act_mat_digit_6_instruction":"Altı rakamını yazılış yönüne uygun olarak parmağınla izle.",
    "aud_act_mat_digit_7_instruction":"Yedi rakamını yazılış yönüne uygun olarak parmağınla izle.",
    "aud_act_mat_digit_8_instruction":"Sekiz rakamını yazılış yönüne uygun olarak parmağınla izle.",
    "aud_act_mat_digit_9_instruction":"Dokuz rakamını yazılış yönüne uygun olarak parmağınla izle.",
    "aud_act_mat_len_04_instruction":"Masa beş karış, kalemlik iki karış. Hangisi daha uzundur?",
    "aud_act_mat_len_05_instruction":"Tahmin altı karış, ölçüm beş karış. Fark kaçtır?",
    "aud_act_mat_num_01_instruction":"On dokuz sayısının bir ve dokuz rakamlarıyla yazıldığını incele.",
    "aud_act_mat_num_03_instruction":"On iki tane nesneyi say ve doğru sayıyı seç.",
    "aud_act_mat_rev_01_instruction":"Ondan büyük olan sayıyı seç.",
    "aud_act_mat_tens_01_instruction":"On birlik kaç onluk eder?",
    "aud_act_mat_tens_02_instruction":"On sekiz sayısını onluk ve birliklerine ayır.",
    "aud_act_mat_tens_03_instruction":"Bir onluk ve beş birlik hangi sayıyı oluşturur?",
    "aud_act_mat_tens_04_instruction":"On yedi sayısını onluk ve birlik olarak göster.",
    "aud_act_mat_tens_05_instruction":"Bir onluk ve üç birlik hangi sayıyı oluşturur?",
    "aud_act_mat_tens_06_instruction":"On altı sayısını seç.",
    "aud_game_math_group_001_instruction":"On nesnenin tamamını seç ve bir onluk oluştur.",
}

PROFILE_SPEED={
    "TEACHER_WARM":0.94,
    "TEACHER_PHONICS":0.88,
    "TEACHER_MATH":0.92,
    "TEACHER_STORY":0.94,
    "TEACHER_ENCOURAGE":0.96,
}

def collect():
    items=[]
    for path in sorted(Path("content").rglob("*.json")):
        if path.name in {"activity_schema.json","curriculum_manifest.json"}:
            continue
        try:
            data=json.loads(path.read_text(encoding="utf-8"))
        except Exception:
            continue
        ins=data.get("instruction")
        if not isinstance(ins,dict):
            continue
        aid=ins.get("audioId"); text=ins.get("text"); profile=ins.get("voiceProfile")
        if aid and text and profile:
            items.append({
                "audioId":str(aid),
                "text":str(text).strip(),
                "spokenText":SPOKEN_OVERRIDES.get(str(aid),str(text).strip()),
                "voiceProfile":str(profile),
                "source":path.as_posix(),
                "manualQaRequired":False,
            })
    return items + STATIC_PROMPTS

def main():
    by_id={}
    for item in collect():
        p=item["voiceProfile"]
        if p not in PROFILE_SPEED:
            raise SystemExit(f"Unknown voice profile: {p}")
        item["speed"]=PROFILE_SPEED[p]
        old=by_id.get(item["audioId"])
        if old and (old["text"]!=item["text"] or old["voiceProfile"]!=p):
            raise SystemExit(f"Conflicting prompt: {item['audioId']}")
        by_id[item["audioId"]]=item

    manifest={
        "schemaVersion":"4.0",
        "status":"STAGING",
        "provider":"self-hosted open source",
        "engine":"ResembleAI Chatterbox Multilingual V3",
        "license":"MIT",
        "language":"tr",
        "selectedVoice":"C",
        "voiceParameters":{
            "seed":618034,
            "exaggeration":0.25,
            "cfgWeight":0.20,
            "temperature":0.60,
        },
        "paidApiUsed":False,
        "apiKeyRequired":False,
        "runtimeMode":"OFFLINE_PACKAGED_AUDIO",
        "items":[by_id[k] for k in sorted(by_id)],
    }
    out=Path("audio/v4/full_manifest.json")
    out.parent.mkdir(parents=True,exist_ok=True)
    out.write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+"\n",encoding="utf-8")
    print(f"Wrote {len(manifest['items'])} V4 prompts")

if __name__=="__main__":
    main()
