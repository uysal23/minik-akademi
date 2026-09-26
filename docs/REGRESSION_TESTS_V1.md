# Minik Akademi — Regresyon Testleri V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 14

Bu aşama, Build Adımları 1–13 arasında LOCKED duruma alınmış mimari, içerik, runtime davranışı, offline varlıklar, kalıcı ayarlar ve çocuk UI akışlarının birlikte bozulmadığını doğrular.

## Değişmezlik Tabanı

`tests/regression/locked_baseline_v1.json` aşağıdaki LOCKED runtime ağaçlarının Git tree SHA değerlerini sabitler:

- `app/src/main`
- `core`
- `feature`
- `content`
- `audio`
- `assets`

Ayrıca Build Adımları 1–13'ün kilit belgelerinin Git blob SHA değerleri sabitlenmiştir. Bu alanlardan biri proje sahibinin onayladığı sürümlü bir unlock olmadan değişirse regresyon kapısı FAIL olur.

## Regresyon Katmanları

### 1. Kilitli sözleşmeler

`tests/regression/test_locked_regression_contracts.py`

Kontroller:

- LOCKED runtime ağaçları değişmemiş olmalı.
- Build Adımları 1–13 kilit belgeleri değişmemiş olmalı.
- `LOCK_INDEX.md` Adım 1–13 kayıtlarını korumalı.
- Build sırası Adım 14 regresyon → Adım 15 release şeklinde kalmalı.
- Adım 13 LOCKED ve 17/17 kanıtlı kalmalı.
- Adım 14 workflow'u release APK üretmemeli.
- Adım 12 validator/test katmanları ve Adım 13 UI akışı tekrar çalıştırılmalı.

### 2. Tam otomatik tekrar

Adım 14 workflow'u:

- tüm Python contract/content testlerini,
- content authoring validator'ını,
- offline audio validator'ını,
- offline visual validator'ını,
- core:model Kotlin/JUnit testlerini,
- core:navigation Kotlin/JUnit testlerini,
- Gradle configuration check'i,
- debug APK build'ini

yeniden çalıştırır.

### 3. Kalıcılık regresyonu

`RegressionPersistenceTest` gerçek Android emülatörde şunların DataStore'da yeni repository örneğinden tekrar okunabildiğini doğrular:

- setupComplete,
- çocuk adı,
- öğrenme seviyesi,
- avatar,
- tema,
- konuşma hızı,
- narration/SFX/music,
- reduced motion,
- large UI,
- high contrast,
- left-handed mode,
- günlük hedef clamp,
- çizgi/Türkçe/matematik/mini oyun ilerlemesi,
- doğru ebeveyn PIN doğrulaması,
- yanlış PIN reddi.

### 4. Tam çocuk UI regresyonu

Adım 13'te kilitlenen gerçek navigasyon testi tekrar çalıştırılır ve tam 17 PNG kanıtının her biri yeniden üretilir.

## Release Sınırı

Bu aşama yalnızca **debug regresyon build** üretir. `assembleRelease`, `bundleRelease`, signing veya release APK üretimi yasaktır. Release APK yalnızca Build Adımı 15'te ve proje sahibinin açık onayıyla alınır.

## Kabul Kapısı

Adım 14 yalnızca şu koşullarda LOCKED olabilir:

1. Locked contracts job PASS.
2. Tüm Python testleri ve validator'lar PASS.
3. Kotlin/JUnit testleri PASS.
4. Debug regression build PASS.
5. Persistence instrumentation PASS.
6. Pedagogical UI instrumentation PASS.
7. 17/17 UI kanıtı mevcut.
8. Adım 1–13 runtime ağaçlarında veya kilit belgelerinde izinsiz değişiklik yok.
9. Release build başlatılmamış olmalı.

Tüm koşullar doğrulandıktan sonra belge **LOCKED V1.0** yapılacaktır.

## Doğrulama Sonucu — 2026-09-26

Final kabul koşusu:

- **Regression Tests #11** — run `36251160389` — **PASS**
- Locked contracts and validators — **PASS**
- Kotlin tests and debug regression build — **PASS**
- Persistence and full UI regression — **PASS**
- 17 / 17 UI kanıt ekranı — **PASS**
- UI evidence artifact: `minik-akademi-step14-regression-ui-evidence`
- UI evidence artifact ID: `10909565688`
- UI evidence SHA-256: `a6520af58163a90b16b3714f9e60254dd0a3c0d616df0a2110d3bf016fd8e57f`
- Instrumentation reports artifact ID: `10909231153`
- Debug regression artifact ID: `10908977135`
- Release APK / bundle / signing işlemi — **BAŞLATILMADI**

### Adım 14 sırasında giderilen eksiklikler

1. İlk teknik PASS koşusunda Android emülatörüne ait **Pixel Launcher isn't responding** sistem diyaloğunun 17 kanıt ekranını örttüğü tespit edildi. Bu koşu kabul edilmedi.
2. Sistem diyaloğu gizleme / daha temiz emulator hedefi eklendi.
3. Odak penceresini doğrulamak için eklenen ilk guard'ın Android `executeShellCommand` davranışı nedeniyle güvenilir olmadığı tespit edildi; bu guard kaldırıldı.
4. Son temiz koşuda persistence testi, full UI regression ve exact 17-screen evidence validation birlikte PASS oldu.
5. Final 17 ekran gözle incelendi; launcher/ANR/system dialog örtüşmesi görülmedi.
6. Seçili avatar sürekliliği, çocuk güvenliği dili, offline davranış, Türkçe/matematik/çizgi/mini oyun navigasyonu ve EXTENSION ayrımı korunmuş durumda.
7. Build Adımları 1–13'ün protected runtime tree SHA'ları ve kilit belge SHA'ları değişmedi.

## Nihai Karar

Kabul kapısındaki 9 koşulun tamamı karşılandı. Build Adımı 14 tamamlandı.

---

**LOCKED — Regresyon Testleri V1.0**
