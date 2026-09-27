# Audio V2 — Ücretsiz Self-Hosted Kurulum

Audio V2 kalite örnekleri için ücretli servis veya repository secret gerekmez.

Kullanılan motor:
- FreyaTTS-small
- Model: `freyavoice/freya-tts`
- Lisans: Apache-2.0
- Çalışma: GitHub Actions build-time / self-hosted
- Runtime internet/TTS: yok

## Akış

1. GitHub Actions ücretsiz/self-hosted FreyaTTS-small kodunu ve açık model ağırlıklarını indirir.
2. 10 kalite örneği yerel olarak üretir.
3. Çıktıları 24 kHz mono OGG/Vorbis biçimine dönüştürür.
4. Örnekler workflow artifact olarak yüklenir.
5. Kullanıcı örnekleri dinler.
6. Kullanıcı açıkça onaylamadan mevcut V1 konuşma assetleri değiştirilmez.
7. Onaydan sonra aynı ücretsiz üretim hattı tam konuşma manifestine uygulanır.
8. Audio validation ve regression testleri çalıştırılır.

## Secret Kuralı

Audio V2 ses üretimi için:
- `MINIMAX_API_KEY` gerekmez.
- Başka ücretli TTS/API anahtarı gerekmez.
- API anahtarı APK içine veya source control'a eklenmez.

## Kalite Önceliği

Amaç yalnızca anlaşılır TTS üretmek değildir. Ses:
- doğal,
- sıcak,
- samimi,
- sakin,
- çocuğa yanında konuşuyormuş gibi,
- yetişkin kadın öğretmen kimliğinde,
- abartısız ve güven verici

olmalıdır.

Kalite kapısını geçmeyen sesler toplu üretime alınmaz.
