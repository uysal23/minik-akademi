# Minik Akademi — Audio V4 C Status

**Status:** ACTIVE / USER APPROVED  
**Audio revision:** V4 C  
**Voice:** C  
**Engine:** ResembleAI Chatterbox Multilingual V3  
**Seed:** 618034  
**Paid API:** none  
**API key:** none

## Final audio state

- 159 / 159 spoken assets were generated with the approved C voice.
- Spoken-text QA: 159 / 159 PASS.
- Content validation: PASS.
- The nine phonemes `a, n, e, t, i, l, o, k, u` use the exact user-approved QA set.
- Speech format: OGG/Vorbis, mono, 24 kHz.
- The seven locked SFX remain unchanged.
- V1 Antalia speech is no longer an active APK asset source and is archived under `audio/legacy/v1/speech`.
- The Android app packages speech only from `audio/runtime/speech`.
- `tools/audio/prepare_v4_runtime_audio.py` assembles the 16 checksum-pinned approved C chunks, overlays the approved phonemes, verifies the full 159-ID inventory, and copies the unchanged SFX before build.
- Runtime internet/TTS remains absent; all audio is packaged into the APK at build time.

## Owner approval

The owner explicitly selected **C** and approved the final nine-phoneme QA set, then requested that all sounds be updated to C without per-file confirmation.
