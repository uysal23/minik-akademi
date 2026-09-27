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

def set_seed(seed:int):
    torch.manual_seed(seed); random.seed(seed); np.random.seed(seed)

def main():
    ap=argparse.ArgumentParser()
    ap.add_argument("--manifest",default="audio/v4/full_manifest.json")
    ap.add_argument("--output-dir",default="audio/v4/staging")
    ap.add_argument("--report",default="audio/v4/full_report.json")
    ap.add_argument("--start",type=int,default=0)
    ap.add_argument("--limit",type=int,default=0)
    args=ap.parse_args()

    manifest=json.loads(Path(args.manifest).read_text(encoding="utf-8"))
    items=manifest["items"]
    if args.start:
        items=items[args.start:]
    if args.limit:
        items=items[:args.limit]

    out=Path(args.output_dir); out.mkdir(parents=True,exist_ok=True)
    params=manifest["voiceParameters"]
    print("Loading selected C voice with free Chatterbox Multilingual V3 on CPU.")
    model=ChatterboxMultilingualTTS.from_pretrained("cpu",t3_model="v3")
    results=[]; failures=[]

    for idx,item in enumerate(items,1):
        aid=item["audioId"]; text=item["spokenText"]; speed=float(item["speed"])
        ogg=out/f"{aid}.ogg"
        print(f"[{idx}/{len(items)}] {aid}: {text}")
        try:
            set_seed(int(params["seed"]))
            wav=model.generate(
                text,
                language_id="tr",
                exaggeration=float(params["exaggeration"]),
                cfg_weight=float(params["cfgWeight"]),
                temperature=float(params["temperature"]),
            )
            raw=out/f"{aid}.raw.wav"
            ta.save(str(raw),wav,model.sr)
            af=(
                f"atempo={speed},"
                "highshelf=f=4200:g=-2.5:t=q:w=0.8,"
                "equalizer=f=6500:t=q:w=0.9:g=-2.0,"
                "equalizer=f=260:t=q:w=0.9:g=1.0,"
                "loudnorm=I=-20:LRA=13:TP=-3"
            )
            subprocess.run([
                "ffmpeg","-y","-loglevel","error","-i",str(raw),
                "-ac","1","-ar","24000","-af",af,
                "-c:a","libvorbis","-q:a","5",str(ogg)
            ],check=True,timeout=180)
            raw.unlink(missing_ok=True)
            results.append({"audioId":aid,"status":"generated","manualQaRequired":item.get("manualQaRequired",False)})
        except Exception as exc:
            failures.append({"audioId":aid,"error":str(exc)})

    report={
        "engine":manifest["engine"],
        "selectedVoice":"C",
        "paidApiUsed":False,
        "requested":len(items),
        "generated":len(results),
        "failed":len(failures),
        "manualQaRequired":[x["audioId"] for x in items if x.get("manualQaRequired")],
        "results":results,
        "failures":failures,
    }
    rp=Path(args.report); rp.parent.mkdir(parents=True,exist_ok=True)
    rp.write_text(json.dumps(report,ensure_ascii=False,indent=2)+"\n",encoding="utf-8")
    if failures:
        raise SystemExit(2)

if __name__=="__main__":
    main()
