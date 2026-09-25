# Minik Akademi — Pre-Writing / Tracing V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 7

Bu modül, kaynak-temelli çizgi/ön-yazı etkinliklerini çalışan Jetpack Compose etkileşimlerine dönüştürür.

## Kilitli Etkinlikler

1. **PRE-TRACE-PATH-001** — hedefe dalgalı yol üzerinden ulaşma
2. **PRE-TRACE-SHAPE-001** — spiral/görsel tamamlama
3. **PRE-TRACE-DIRECTION-001** — zikzak ve yönlü çizgi
4. **PRE-TRACE-COPY-001** — eğri çizgiyi tekrar etme
5. **PRE-VIS-DIFFERENCE-001** — 5 farkı bulma

İçerik dosyaları:
`content/literacy/preparation/`

## Etkileşim Motoru

Tracing motoru:
- noktalı kılavuz noktaları oluşturur,
- parmak hareketini gerçek zamanlı çizer,
- kılavuz noktalarına yakın geçişleri işaretler,
- yaklaşık %72 kılavuz kapsaması sonrasında etkinliği tamamlar,
- yanlış çizimde ceza üretmez,
- yeniden denemeye izin verir.

Desteklenen V1 kılavuzları:
- WAVE_PATH
- SPIRAL
- ZIGZAG
- CURVE_COPY

## Görsel Dikkat

Fark bulma etkinliği:
- iki sade paneli karşılaştırır,
- 5 farklı öğeyi tek tek buldurur,
- yanlış dokunmada ceza/puan kaybı uygulamaz.

## Avatar Sürekliliği

Seçilen çocuk avatarı:
- tracing ana ekranında,
- her tracing etkinliğinde

aynı `avatarId` ile gösterilir.

Gerçek avatar illüstrasyonları Build Adımı 11'de placeholder'ın yerini alacaktır.

## Offline İçerik

Kök `content/` klasörü Android app assets içine paketlenir.
Tracing ekranı etkinlik başlığı, yönergesi ve activity type bilgisini runtime'da yerel JSON dosyasından okur.

İnternet veya runtime TTS kullanılmaz.

## İlerleme

Tamamlanan tracing etkinlikleri Preferences DataStore içinde yerel olarak saklanır.

Etkinlik listesinde:
**✓ Tamamlandı**

durumu gösterilir.

## Kaynak ve Validation

Beş etkinlik `ADAPTED` olarak TR-01 kaynağına ve kilitli curriculum node'larına bağlıdır.

Content Validation run **36168713215**: **PASS**

## Android Build

Android Build run **36169159298**:
- Gradle configuration: PASS
- `:app:assembleDebug`: PASS
- debug APK artifact upload: PASS

---

**LOCKED — Pre-Writing / Tracing V1.0**
