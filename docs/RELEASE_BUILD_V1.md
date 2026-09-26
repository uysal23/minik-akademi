# Minik Akademi — Release APK Build V1

**Status:** VERIFYING  
**Version:** 1.0  
**Build Step:** 15

Bu aşama Build Adımları 1–14 LOCKED durumdayken Android release varyantını üretir, imzayı doğrular ve oluşan APK'nın gerçek Android emülatörüne kurulup açılabildiğini kontrol eder.

## Değişmez Önkoşul

- Build Adımı 14 **LOCKED** olmalıdır.
- `tests/regression/locked_baseline_v1.json` içinde `lockedThroughStep = 14` kalmalıdır.
- Release öncesi Python contract testleri, content validator, audio validator ve visual validator yeniden PASS olmalıdır.
- Adım 1–14 protected runtime tree ve kilit belgeleri release hazırlığı sırasında değiştirilmez.

## Release Candidate Doğrulaması — 2026-09-26

GitHub Actions:

- Workflow: `.github/workflows/release-apk.yml`
- **Step 15 Release Candidate #2**
- Run: `36254205682`
- Sonuç: **PASS**

Doğrulanan kapılar:

1. Step 14 LOCKED gate — **PASS**
2. Release preflight contracts/validators — **PASS**
3. `:app:assembleRelease` — **PASS**
4. APK alignment — **PASS**
5. APK signing — **PASS**
6. APK signature verification — **PASS**
7. Package metadata verification — **PASS**
8. Android emulator install — **PASS**
9. `com.uysal.minikakademi.app.MainActivity` launch — **PASS**
10. Process alive / launch smoke evidence — **PASS**

## Üretilen APK

- Dosya: `MinikAkademi-0.1.0-release-ci-signed.apk`
- Package: `com.uysal.minikakademi`
- versionCode: `1`
- versionName: `0.1.0`
- minSdk: `26`
- targetSdk: `37`
- APK SHA-256: `03b9008cf248c67f4e7d427727937f9cb1d3acc7665a6e3a3fca952605c6d3a2`
- APK Signature Scheme v2: **true**
- APK Signature Scheme v3: **true**
- Signing key algorithm: RSA 4096

GitHub artifact:

- `minik-akademi-step15-release-candidate`
- Artifact ID: `10909198982`
- Smoke evidence artifact: `minik-akademi-step15-release-smoke-evidence`
- Smoke evidence artifact ID: `10909533708`

## Signing Sınırı

Bu doğrulama APK'sı GitHub Actions koşusunda oluşturulan **geçici CI signing kimliği** ile imzalanmıştır. Özel anahtar source code'a veya repoya commit edilmemiştir.

Bu paket teknik olarak imzalı, kurulabilir ve çalıştırılabilir bir release APK'dır; ancak aynı CI anahtarı korunmadığı için gelecekte aynı uygulamanın üstüne imzalı güncelleme yüklemek için kalıcı production signing kimliği gerekir.

Bu nedenle Adım 15 henüz **LOCKED** değildir. Nihai production release için güvenli ve kalıcı signing key/keystore GitHub Actions secret mekanizmasına bağlanmalı, aynı release + install smoke kapıları bu anahtarla tekrar PASS olmalıdır.

---

**VERIFYING — Release APK Build V1.0**
