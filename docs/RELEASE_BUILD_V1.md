# Minik Akademi — Release APK Build V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 15

Bu aşama Build Adımları 1–14 LOCKED durumdayken gerçek Android release varyantını üretir; paket, offline içerik, imza, kurulum, açılış, temel kullanıcı akışları, kalıcılık, ebeveyn kapısı, crash/ANR davranışı ve minimum Android sürümü üzerinde doğrulama yapar.

## Değişmez Önkoşul

- Build Adımı 14 **LOCKED** olmalıdır.
- Release öncesi Python contract testleri, content validator, audio validator ve visual validator PASS olmalıdır.
- Release doğrulaması sırasında Adım 1–14'ün pedagojik/içerik kilitleri korunmalıdır.
- Bir test teknik olarak yeşil görünse bile kanıt veya gerçek kullanıcı davranışı hatalıysa kabul edilmez.

## Final Doğrulama — 2026-09-26

### Release build ve kapsamlı kabul

- Workflow: `.github/workflows/release-apk.yml`
- Release Candidate #12 run: `36258468774`
- Release build/sign/full acceptance job: `108449622535` — **PASS**
- Step 14 gate — **PASS**
- Release preflight contracts / validators — **PASS**
- `:app:assembleRelease` — **PASS**
- APK alignment — **PASS**
- APK signing / certificate verification — **PASS**
- Package metadata / permission check — **PASS**
- Offline payload check — **PASS**
- Android API 35 comprehensive offline release acceptance — **PASS**
- In-place reinstall (`adb install -r`) sonrası kullanıcı verisi kalıcılığı — **PASS**
- Release oturumu crash / ANR taraması — **PASS**
- 11 / 11 kapsamlı release kanıt ekranı — **PASS**

### Minimum Android sürümü

- Workflow: `.github/workflows/step15-api26.yml`
- **Step 15 API 26 Verification #2**
- Run: `36259338347`
- Sonuç: **PASS**
- Android 8.0 / API 26 emulator boot — **PASS**
- APK install — **PASS**
- `com.uysal.minikakademi.app.MainActivity` launch — **PASS**
- Welcome UI görünürlük doğrulaması — **PASS**
- Process/package health — **PASS**
- API 26 crash / ANR taraması — **PASS**
- API 26 ekran kanıtı — **PASS**

### Son birleşik kanıt kapısı

- Workflow: `.github/workflows/step15-final-gate.yml`
- **Step 15 Final Technical Gate #1**
- Run: `36259741949`
- Sonuç: **PASS**
- Release APK SHA doğrulaması — **PASS**
- Comprehensive acceptance artifact doğrulaması — **PASS**
- API 26 artifact doğrulaması — **PASS**
- Final logcat crash / ANR doğrulaması — **PASS**

### Kilit sonrası regresyon güvencesi

- **Regression Tests #23** — run `36258468796` — **PASS**
- **Automated Tests #40** — run `36258468758` — **PASS**

## Final APK

- Dosya: `MinikAkademi-0.1.0-release-ci-signed.apk`
- Package: `com.uysal.minikakademi`
- versionCode: `1`
- versionName: `0.1.0`
- minSdk: `26`
- targetSdk: `37`
- APK size: `13,964,359 bytes`
- APK SHA-256: `4c7841b86d623f74baad2590dbe061df8d4e1cf591f8c1f595a2bc2a414fcba6`
- APK Signature Scheme v2: **true**
- APK Signature Scheme v3: **true**
- Signing algorithm: RSA 4096
- INTERNET permission: **yok**
- Bundled offline OGG: **166**
- Bundled offline JSON: **127**

### Final artifact'lar

- Release candidate: `minik-akademi-step15-release-candidate`
  - Artifact ID: `10911322566`
  - Artifact digest: `sha256:fcf6eacbc099bcfedf5f1fb557ccea0ea9807be87d357bddde5a3711c3a3cf47`
- Comprehensive release evidence:
  - Artifact ID: `10910933993`
  - Artifact digest: `sha256:e0a393ab9dec22d3c4c45db276eacf930036dd85d3cf94968f3a3c6d5d6bfb9a`
- Android API 26 evidence:
  - Artifact ID: `10912036360`
  - Artifact digest: `sha256:cad278466dd101b806ddadba91ba988d842d8bdd0682358555f0b94038a838a4`

## ADIM 15 sırasında yakalanıp giderilen sorunlar

1. İlk release smoke testinde activity component applicationId altında yanlış aranıyordu. Gerçek component `com.uysal.minikakademi.app.MainActivity` olarak düzeltildi.
2. Tam kabul testi Ebeveyn Ayarları ekranının 1080x1920 telefonda aşağıdaki ayarlara erişim vermediğini ortaya çıkardı. Ekran dikey kaydırılabilir yapıldı.
3. Düzeltme sonrasında Regression Tests #21 ve sonraki Regression Tests #23 PASS ile önceki kilitli davranışların bozulmadığı doğrulandı.
4. GitHub emulator cold-start sırasında UIAutomator'ın geçici sistem/launcher yüzeyine takılabildiği görüldü. Release başlangıç kontrolüne güvenli retry eklendi; gerçek crash durumunun FAIL vermesi korunmuştur.
5. API 26 doğrulamasında emulator-runner'ın `/bin/sh` davranışı nedeniyle `pipefail` ve çok satırlı shell kontrol bloklarının taşınabilir olmadığı tespit edildi. Test sade ardışık ADB komutlarına dönüştürüldü.
6. Son API 26 koşusunda APK gerçek Android 8.0 emülatörüne başarıyla kurulup açıldı ve welcome ekranı gözle de doğrulandı.

## Signing Kimliği Sınırı

Bu final teknik APK, GitHub Actions koşusunda oluşturulan **CI signing kimliği** ile imzalanmıştır. Bu imza APK'nın bütünlüğünü, kurulabilirliğini ve runtime doğrulamasını sağlar ve mevcut APK çalışır durumdadır.

Ancak GitHub bağlantısının secrets yönetim API'sine erişimi yoktur; bu nedenle uzun vadeli production/Play Store güncellemeleri için gereken kalıcı özel signing key repoya gömülmemiştir. Gelecekte aynı kurulumun üzerine production güncellemesi yayınlanacaksa kalıcı keystore güvenli GitHub Actions secret/deployment mekanizmasına ayrıca bağlanmalıdır.

Bu sınır **runtime/build kabulünü bozmaz**, fakat production update kimliği açısından korunması gereken dağıtım gereksinimidir.

## Nihai Karar

Release APK derlenmiş, imzalanmış, offline paket açısından doğrulanmış, Android API 35 ve minimum API 26 üzerinde kurulup açılmış, kapsamlı kullanıcı akışından geçmiş, reinstall sonrası veri kalıcılığı doğrulanmış ve crash/ANR taramalarından temiz geçmiştir.

---

**LOCKED — Release APK Build / Technical Acceptance V1.0**
