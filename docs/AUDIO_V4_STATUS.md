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
- The locked V1 `audio/` tree is preserved byte-for-byte for regression history only.
- The Android app **does not package speech from the V1 audio tree**.
- The only active APK audio asset root is `audio_v4/runtime/`.
- `tools/audio/prepare_v4_runtime_audio.py` assembles the 16 checksum-pinned approved C chunks, overlays the exact approved phoneme files, verifies the pinned 159-ID inventory, and copies the seven unchanged SFX before build.
- Runtime TTS and runtime internet remain absent. The prepared audio is packaged into the APK as offline assets.

## Lock compatibility

The protected `audio` tree remains at the locked baseline SHA:
`a76f8b07e160a9c16a34fb21009352368ad9cc85`.

V4 C maintenance assets live outside that protected tree under `audio_v4/`, so the original V1 baseline remains reproducible while the application build uses only the approved C voice.

## Owner approval

The owner explicitly selected **C**, approved the final nine-phoneme QA set, and requested that all application speech be switched to C without per-file confirmation.
