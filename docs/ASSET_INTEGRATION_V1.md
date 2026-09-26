# Minik Akademi — Görsel ve Ses Varlıkları V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 11

Bu belge Build Adımı 11 kapsamında üretilen, doğrulanan ve uygulamaya bağlanan görsel ve ses varlıklarını tanımlar.

## 1. Offline Konuşma Paketi

GitHub Actions üzerinde build-time TTS ile **159 / 159** Türkçe konuşma klibi üretildi.

- Format: OGG/Vorbis
- Örnekleme: 24 kHz mono
- Runtime internet: yok
- Android sistem TTS: yok
- Bulut TTS runtime çağrısı: yok
- API anahtarı: yok
- Üretim raporu: 159 requested / 159 generated / 0 failed

Ses profilleri:

- TEACHER_WARM
- TEACHER_PHONICS
- TEACHER_MATH
- TEACHER_STORY
- TEACHER_ENCOURAGE

Üretim pipeline'ı:

`content JSON → audio manifest → Antalia 1 build-time synthesis → normalize → OGG → APK assets`

## 2. Konuşma Bağlantıları

Etkinlik JSON'larındaki `instruction.audioId` değerleri doğrudan offline OGG dosyalarına bağlanmıştır.

Bağlanan modüller:

- Çizgi / ön-yazı
- Türkçe okuma-yazma
- Matematik
- Mini oyunlar
- Onboarding ses ön izlemesi

Davranış:

- etkinlik açılışında yönerge sesi,
- ekrandaki **🔊 Dinle** ile yönergeyi yeniden oynatma,
- doğru cevapta `successAudioId`,
- yanlış denemede `retryAudioId`,
- ebeveyn anlatım ayarı kapalıysa konuşma oynatılmaz,
- konuşma hızı 0.80x / 0.90x / 1.00x ayarına uyar.

Ek bağlantılar:

- a, n, e, t, i, l, o, k, u fonem klipleri harf tanıtımına,
- 0–20 sayı klipleri sayı tanıtımına,
- karşılama sesi Welcome ekranına,
- örnek öğretmen sesi konuşma hızı ekranına

bağlanmıştır.

## 3. Animasyon / Etkileşim SFX

Toplam **7 özgün prosedürel SFX** üretildi:

- `sfx_soft_pop`
- `sfx_gentle_tap`
- `sfx_success_chime`
- `sfx_star_sparkle`
- `sfx_slide_soft`
- `sfx_retry_soft`
- `sfx_complete`

Bağlantı örnekleri:

- ekran/öğe girişi → soft pop / slide soft
- dokunma ve ses tekrar butonu → gentle tap
- doğru cevap → success chime
- başarı animasyonu → star sparkle
- yanlış seçim → retry soft
- mini oyun tamamlanması → complete

SFX kapalıysa bu efektler oynatılmaz.

## 4. Runtime Audio Motoru

`core:audio` modülü eklendi.

`OfflineAudioPlayer`:

- speech ve SFX'i bağımsız kanallarda oynatır,
- APK assetlerinden okur,
- konuşma hızını pitch bozmadan değiştirir,
- dosya yoksa runtime TTS fallback başlatmaz,
- ekran kapanınca MediaPlayer kaynaklarını serbest bırakır.

## 5. Avatar Görselleri

Önceki A01/A02 tarzı placeholder görünüm kaldırıldı.

V1 avatar sistemi:

- **12 özgün insan çocuk kimliği**
- farklı ten tonları,
- farklı saç biçimleri,
- farklı kıyafet renkleri,
- bazı avatarlar için gözlük,
- aynı avatarId ile bütün ekranlarda kimlik sürekliliği

sağlar.

Avatar renderer, kilitli 16 pozu destekler:

- idle
- smile
- wave
- listen
- point_left
- point_right
- point_up
- point_down
- thinking
- walk_left
- walk_right
- hold_object
- celebrate
- clap
- sit
- surprised_soft

Görseller tamamen offline Compose vector çizimleridir; kaynak PDF illüstrasyonları kopyalanmaz.

## 6. Öğrenme Nesnesi Görselleri

`LearningObjectArt` final V1 pictogram sistemi eklendi.

Türkçe ses farkındalığı etkinliklerinde kullanılan nesneler için özgün 2D pictogramlar bağlandı. Bunlara örnek:

- armut, elma, limon, incir,
- araba, tren, kayık, uçak, uçurtma,
- masa, telefon, balon, kalem, silgi,
- inek, aslan, tavşan, koyun, kelebek,
- okul, orman

ve kaynak-müfredat etkinliklerinde kullanılan diğer nesneler.

Aynı vector görsel sistemi:

- Türkçe ses etkinliklerinde,
- Matematik sayma/işlem görsellerinde,
- Mini oyunlarda

kullanılır.

## 7. Asset Packaging

Android uygulaması:

- `content/`
- `audio/generated/`

klasörlerini APK assetlerine paketler.

Görsel vector renderer uygulama koduyla birlikte APK içinde bulunur.

## 8. Otomatik Doğrulama

### Ses üretimi

GitHub Actions:

- Generate Offline Audio run **36198942500**
- sonuç: **PASS**
- speech: **159 / 159**
- failed: **0**
- SFX: **7**

### Audio Validation

Run **36223818177**

- sonuç: **PASS**
- activity JSON: **125**
- gerekli unique speech ID: **127**
- mevcut speech OGG: **159**
- gerekli SFX seti: **7**
- mevcut SFX OGG: **7**
- generation report: **159 generated / 0 failed**

### Visual Validation

Run **36223854933**

- sonuç: **PASS**
- avatar identities: **12**
- avatar poses: **16**
- literacy object pictograms: **25**
- visual hooks: literacy / mathematics / mini-games

## 9. Kilit Kuralı

Bu aşama kilitlendikten sonra:

- mevcut audioId bağlantıları sessizce değiştirilemez,
- runtime TTS eklenemez,
- seçili avatar kimliği değiştirilemez,
- çocuk güvenliği SFX kuralları gevşetilemez,
- kaynak görseller birebir kopyalanamaz.

## 10. Android Build Doğrulaması

Run **36223801498**

- sonuç: **PASS**
- Gradle configuration: **PASS**
- `:app:assembleDebug`: **PASS**
- debug APK artifact upload: **PASS**

---

**LOCKED — Görsel ve Ses Varlıkları V1.0**
