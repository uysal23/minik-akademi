# Minik Akademi — Matematik Modülü V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 9

Bu modül, kilitli matematik müfredatını çalışan Android etkinliklerine dönüştürür.

## İçerik Kapsamı

Toplam **63 matematik etkinliği**:

- Uzamsal ilişkiler ve eş nesneler: 13
- Sayılar ve nicelikler: 36
- Ölçme ve tartma: 11
- Kaynak dışı genişletmeler: 3

## Kaynak-Temelli Bölümler

- altında / üstünde ve diğer uzamsal ilişkiler
- eş nesneler ve eş şekiller
- 1–9 ve 0 rakamları
- nesne sayma ve sayı-miktar ilişkisi
- onluk / birlik
- sıra bildiren sayılar
- az / çok / eşit karşılaştırmaları
- birer, ikişer, beşer ve onar ritmik sayma
- geriye ritmik sayma
- örüntüler
- uzun / kısa
- standart olmayan ölçme
- ağır / hafif / eşit ağırlık

## EXTENSION Bölümü

Mevcut PDF setinde bağımsız sistematik öğretim dizisi bulunmadığı için aşağıdakiler kaynak-temelli bölüm gibi gösterilmez:

- toplama
- çıkarma
- çarpma / eş gruplama

Bu üç bölüm `EXTENSION` olarak açıkça ayrılmıştır.

## Etkileşim Motorları

V1 içinde:

- POSITION_SELECT
- MATCH_PAIR
- TRACE_NUMBER
- NUMBER_INTRO
- COUNT_OBJECTS
- TAP_CHOICE
- ORDER_ITEMS
- COMPARE_QUANTITY
- RHYTHMIC_COUNT
- PATTERN_COMPLETE
- COMPARE_LENGTH
- MEASURE_NONSTANDARD
- COMPARE_MASS
- ADD_OBJECTS
- SUBTRACT_OBJECTS
- GROUP_OBJECTS

## Rakam Yazma

0–9 için noktalı parmak izleme motoru bulunur. Çocuk:
- büyük başlangıç noktasından başlar,
- noktalı yolu takip eder,
- yanlış çizimde ceza almaz,
- etkinliği tekrar deneyebilir.

## İlerleme

Tamamlanan matematik etkinlikleri cihaz içinde Preferences DataStore ile saklanır ve ebeveyn paneline aktarılır.

## Offline

Matematik içerikleri APK içindeki `content/mathematics/` assetlerinden okunur.
Runtime internet veya cihaz TTS'si kullanılmaz.

## Asset Aşaması

Gerçek nesne çizimleri, avatar çizimleri, kadın öğretmen sesleri ve SFX Build Adımı 11'de mevcut activity/audio ID'lerine bağlanacaktır.

---

## Doğrulama

- Matematik activity JSON: **63**
- Content Validation run: **36190586885 — PASS**
- Android Build run: **36190716305 — PASS**
- Gradle configuration: **PASS**
- `:app:assembleDebug`: **PASS**
- Debug APK artifact upload: **PASS**

---

**LOCKED — Matematik Modülü V1.0**
