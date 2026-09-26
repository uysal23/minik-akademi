# Minik Akademi v1.0.0 — Production Release

Bu belge production signing ve gerçek dağıtım aşaması içindir.

## Kaynak taban

- Build Steps 1–15: LOCKED
- Release teknik kabulü: `docs/RELEASE_BUILD_V1.md`
- Production package: `com.uysal.minikakademi`
- versionCode: `1`
- versionName: `0.1.0`
- minSdk: `26`
- targetSdk: `37`
- Offline-first; INTERNET permission yok.

## Production signing

Production signing anahtarı source control dışında tutulur. GitHub Actions yalnızca repository secrets üzerinden erişir:

- `MINIK_AKADEMI_KEYSTORE_BASE64`
- `MINIK_AKADEMI_KEYSTORE_PASSWORD`
- `MINIK_AKADEMI_KEY_ALIAS`
- `MINIK_AKADEMI_KEY_PASSWORD`

Keystore hiçbir koşulda repoya commit edilmez.

## v1.0.0 dağıtım kapısı

Production workflow şu çıktıları üretir:

- `MinikAkademi-v1.0.0.apk`
- `MinikAkademi-v1.0.0.aab`
- SHA-256 özetleri
- Production certificate bilgisi
- Production imzalı APK ile kapsamlı offline kabul kanıtları

İlk GitHub Release **draft** oluşturulur. Gerçek Android telefonda kurulum ve temel kabul kontrolü yapıldıktan sonra v1.0.0 yayımlanır.

## Gerçek cihaz son kontrolü

Yayın öncesi fiziksel cihazda:

1. APK kurulumu
2. ilk açılış
3. onboarding ve çocuk profili
4. offline ses oynatma
5. çizgi / Türkçe / matematik ekranları
6. ebeveyn PIN alanı ve ayarların kaydırılması
7. uygulamayı kapatıp yeniden açınca veri kalıcılığı
8. ekran/nav bar taşması
9. cihaz ses seviyesi ve doğal kullanım hissi

kontrol edilir.

Sonraki küçük ses ve UI düzeltmeleri v1.0 tabanı üzerine ayrı bakım sürümü olarak yapılacaktır.
