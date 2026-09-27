# Minik Akademi — Audio V4 C Staging Status

**Status:** STAGING GENERATED — PHONEME CANDIDATES READY / PENDING USER LISTENING QA  
**Live V1 audio replaced:** NO

## Verified gates

- Spoken text inventory: 159/159
- Content Validation: PASS
- Spoken Text QA: PASS
- Selected voice: C
- Engine: ResembleAI Chatterbox Multilingual V3
- Seed: 618034
- Paid API: none
- API key: none
- Generation chunks: 16/16 SUCCESS
- Generated speech files: 159/159 across chunk artifacts

## Text review before synthesis

Before V4 synthesis, all 159 spoken items were reviewed.
20 application instruction texts were corrected for grammar, clarity, natural Turkish, or child-friendly phrasing.
A separate `spokenText` layer converts display digits into natural Turkish words for TTS without changing pedagogical on-screen number representation.

## Manual phoneme gate

The locked phoneme QA gate remains open for:
- a
- n
- e
- t
- i
- l
- o
- k
- u

Generated locations:
- chunk 140: a, e, i, k, l
- chunk 150: n, o, t, u

These clips were generated successfully. They must be listened to and confirmed to represent the intended sound value rather than an unwanted letter-name pronunciation before promotion to live app assets.

## Packaging note

All 16 audio chunk artifacts were successfully generated and uploaded.
A separate final combine job failed before runner steps started; this is a packaging/runner issue and did not invalidate any generated audio chunk.
Live V1 assets remain unchanged until phoneme QA and promotion are complete.


## Phoneme candidate revision 2

The original single-character TTS phoneme files were rejected by technical QA because several were too long and carried letter-name/vowel-tail risk.

A new local QA candidate set was prepared from the selected C voice by isolating only the target acoustic onset/sound segment:
- vowels a/e/i/o/u: 0.28 s clean vowel segment,
- continuants n/l: 0.11 s consonant onset,
- stops k/t: 0.065 s release/onset segment.

Technical checks passed for duration and acoustic isolation. Locked manual listening QA is still required before these nine files and the 159-file V4 staging set are promoted to live app assets.
