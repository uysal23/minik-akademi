# Minik Akademi — Otomatik Testler V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 12

Bu aşama, Build Adımları 1–11'de kilitlenen davranışların GitHub Actions üzerinde otomatik olarak doğrulanmasını sağlar. Kullanıcının bilgisayarında Android Studio, Gradle, Python veya Codex çalıştırılması gerekmez.

## Test Katmanları

### 1. Python proje sözleşmesi testleri

`tests/automated/test_project_contracts.py`

Doğrulanan başlıklar:

- toplam activity sayısı ve modül dağılımı,
- benzersiz activity ID'leri,
- curriculumId eşleşmesi,
- çocuk güvenliği bayrakları,
- EXTENSION ayrımı,
- INTERNET/location/microphone izinlerinin bulunmaması,
- cleartext trafiğin kapalı olması,
- runtime TTS ve network client yasağı,
- kilitli navigation route bağlantıları,
- dört öğrenme alanının DataStore ilerleme kayıtları,
- ebeveyn PIN SHA-256 saklama kuralı,
- günlük hedefin 5–30 dakika sınırı,
- offline audio ve avatar runtime hook'ları.

### 2. Content validator negatif/pozitif testleri

`tests/content/test_content_validator_contract.py`

Otomatik olarak kontrol edilir:

- geçerli etkinliğin geçmesi,
- bilinmeyen curriculumId'nin reddedilmesi,
- punitive feedback'in reddedilmesi,
- external URL bayrağının reddedilmesi,
- kilitli sayı aralığı ihlalinin reddedilmesi,
- kilitli harf ihlalinin reddedilmesi,
- release modunda safety review zorunluluğu,
- eksik offline audio'nun reddedilmesi,
- eksik offline visual'ın reddedilmesi,
- duplicate activity ID bulunmaması,
- malformed JSON'un reddedilmesi.

### 3. Kotlin/JUnit testleri

`core:model`:
- 12 sabit avatar kimliği,
- avatar fallback davranışı,
- 0.80x / 0.90x / 1.00x konuşma hızları,
- güvenli varsayılan uygulama ayarları.

`core:navigation`:
- oluşturulan learning route'larının pattern'larla uyumu,
- çocuk ve ebeveyn route köklerinin ayrılığı.

### 4. Mevcut validator'ların tekrar çalıştırılması

Automated Tests workflow ayrıca:

- Content Validation — authoring,
- Audio Validation,
- Visual Validation

testlerini aynı otomatik kapıda tekrar çalıştırır.

### 5. Android build kapısı

Kotlin testlerinden sonra:

- Gradle configuration check,
- `:core:model:testDebugUnitTest`,
- `:core:navigation:testDebugUnitTest`,
- `:app:assembleDebug`,
- JUnit report artifact upload,
- test edilmiş debug APK artifact upload

çalıştırılır.

## CI

Workflow:

`.github/workflows/automated-tests.yml`

Hem `main` push'larında hem pull request'lerde ilgili kod/içerik değişikliklerinde otomatik çalışır.

## Aşama Sınırı

Bu aşama otomatik test kapsamıdır. Gerçek cihaz/emülatör üzerinde pedagojik gözlem ve manuel UI kontrolü **Build Adımı 13** kapsamındadır.

## Doğrulama Sonucu

GitHub Actions **Automated Tests run 36224514555 — PASS**.

Sonuçlar:

- Python automated tests: **20 / 20 PASS**
- Content Validation: **PASS**
- Audio Validation: **PASS**
- Visual Validation: **PASS**
- Kotlin/JUnit model tests: **PASS**
- Kotlin/JUnit navigation tests: **PASS**
- Gradle configuration: **PASS**
- `:app:assembleDebug`: **PASS**
- JUnit report artifact: **automated-test-reports**
- Test edilmiş debug APK artifact: **minik-akademi-step12-tested-debug**

Kotlin tarafında toplam **6 JUnit testi** bulunur: 4 model testi + 2 navigation testi.

---

**LOCKED — Otomatik Testler V1.0**
