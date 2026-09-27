#!/usr/bin/env python3
from __future__ import annotations

import json
import re
import sys
from pathlib import Path

EXPECTED_COUNT=159

def main()->int:
    p=Path("audio/v4/full_manifest.json")
    m=json.loads(p.read_text(encoding="utf-8"))
    items=m.get("items",[])
    errors=[]
    if len(items)!=EXPECTED_COUNT:
        errors.append(f"Expected {EXPECTED_COUNT} items, found {len(items)}")

    ids=set()
    for item in items:
        aid=item.get("audioId","")
        spoken=str(item.get("spokenText","")).strip()
        if not aid:
            errors.append("Missing audioId")
        elif aid in ids:
            errors.append(f"Duplicate audioId: {aid}")
        ids.add(aid)
        if not spoken:
            errors.append(f"{aid}: empty spokenText")
        if re.search(r"\d",spoken):
            errors.append(f"{aid}: digit remains in spokenText: {spoken}")
        if " - " in spoken:
            errors.append(f"{aid}: risky dash remains in spokenText: {spoken}")
        if spoken.endswith("yerleştir?"):
            errors.append(f"{aid}: malformed imperative/question: {spoken}")

    expected_phonemes={f"aud_phoneme_{x}" for x in ["a","n","e","t","i","l","o","k","u"]}
    flagged={x["audioId"] for x in items if x.get("manualQaRequired")}
    if flagged!=expected_phonemes:
        errors.append(f"Phoneme manual-QA set mismatch: {sorted(flagged)}")

    if errors:
        print(f"V4 SPOKEN TEXT QA FAILED ({len(errors)})")
        for e in errors:
            print(" -",e)
        return 1

    print("V4 SPOKEN TEXT QA PASSED")
    print(f" items: {len(items)}")
    print(" selected voice: C")
    print(" paid API: none")
    print(" phonemes requiring manual QA: 9")
    return 0

if __name__=="__main__":
    sys.exit(main())
