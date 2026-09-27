# Audio V4 C — Active Application Audio

This directory is the active audio source for current Minik Akademi builds.

- Voice: **C**
- Engine: ResembleAI Chatterbox Multilingual V3
- Speech: **159 / 159**
- Phoneme QA: **USER APPROVED**
- Format: OGG/Vorbis, mono, 24 kHz
- Paid API: none
- Runtime TTS: none
- Runtime network: none

Before an Android build, `tools/audio/prepare_v4_runtime_audio.py` prepares `audio_v4/runtime/` from the checksum-pinned approved C artifacts and overlays the exact approved phoneme pack.

The original locked V1 `audio/` tree is retained only as a regression baseline and is not an Android asset source.
