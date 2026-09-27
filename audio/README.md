# audio

Aktif konuşma sesi: **Audio V4 C**.

- APK'ya paketlenen aktif asset kökü: `audio/runtime/`
- C konuşma sesleri build öncesinde `tools/audio/prepare_v4_runtime_audio.py` ile hazırlanır.
- Onaylı 9 fonem paketi: `audio/v4/approved_phonemes/`
- Eski V1/Antalia konuşmaları yalnız arşivdir: `audio/legacy/v1/speech/`
- 7 SFX değişmeden `audio/generated/sfx/` altında tutulur ve runtime alanına kopyalanır.
- Runtime TTS, internet veya ücretli API kullanılmaz.
