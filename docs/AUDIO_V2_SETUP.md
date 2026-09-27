# Audio V2 — MiniMax API Kurulumu

Audio V2 kalite örneklerini üretmek için GitHub Actions'a yalnız bir secret gerekir:

`MINIMAX_API_KEY`

Anahtar source control'a yazılmaz ve APK içine girmez. Sadece build-time ses üretiminde kullanılır.

## Akış

1. MiniMax API Platform hesabından API key oluştur.
2. `uysal23/minik-akademi` → Settings → Secrets and variables → Actions → New repository secret.
3. Name: `MINIMAX_API_KEY`
4. Secret: oluşturduğun API key.
5. Secret eklendikten sonra `audio/AUDIO_V2.trigger` dosyası `mode=GENERATE_SAMPLES` yapılır.
6. Workflow 10 adet kalite örneği üretir.
7. Örnekler kullanıcı tarafından dinlenir.
8. Onaydan sonra tam 159+ konuşma asseti V2 ile yeniden üretilir.

Production API key uygulamaya paketlenmez.
