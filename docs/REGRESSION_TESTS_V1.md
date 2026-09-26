# Minik Akademi — Regresyon Testleri V1

**Status:** VERIFYING  
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
