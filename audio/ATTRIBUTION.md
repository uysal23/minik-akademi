# Offline Audio Attribution

Minik Akademi konuşma assetleri build sırasında açık Türkçe TTS modelleriyle sentetik olarak üretilir ve APK içine statik OGG dosyaları olarak paketlenir.

## V1 — Antalia 1

V1 build-time model:

- Model: `cloud0day3/antalia-1`
- Code: `0daycloud/antalia`
- Model weights license: Antalia OpenRAIL-M
- Code license: Apache-2.0
- Output: 24 kHz mono Turkish synthetic speech
- Runtime cloud/TTS use: **none**

Required attribution:

**Antalia 1** — Sezgin Saygili, Emre Kaplaner, Oncel Ozgul and Fikri San Koktas (Patientdesk.ai).

The application must disclose in the adult About/Open Source area that the teacher narration is AI-generated synthetic speech.

The generated clips must not be presented as recordings of the anonymous voice actor.

## V2 IN REVIEW — FreyaTTS-small

V2 kalite inceleme hattı:

- Model: `freyavoice/freya-tts`
- Code: `freyavoiceai/FreyaTTS`
- Model/code license: Apache-2.0
- Voice selection: canonical deterministic Leyla seed
- Source output: 48 kHz synthetic speech
- Packaged output: 24 kHz mono OGG/Vorbis
- Paid API use: **none**
- API key required: **none**
- Runtime cloud/TTS use: **none**

V2 yalnız build-time kalite örnekleri üretir. Kullanıcı dinleme onayı olmadan V1 konuşma assetlerinin üzerine yazılmaz.

FreyaTTS-large ticari modeldir ve Minik Akademi V2 hattında kullanılmaz.

## BigVGAN

Antalia V1 uses NVIDIA BigVGAN v2 as vocoder. The workflow pins the upstream source revision used by the Antalia quick-start recipe.

## Child-safety audio rule

Generated speech and procedural SFX are reviewed as learning assets. Runtime Android system TTS, notification sounds, alarm sounds, online speech APIs and API keys are not used.
