# Minik Akademi — Audio Style Guide V2

**Status:** IN REVIEW  
**Version:** 2.0  
**Scope:** Maintenance / post-v1 technical acceptance  
**Supersedes after approval:** AUDIO_STYLE_GUIDE.md V1.0

## 1. Değişiklik Gerekçesi

Gerçek cihaz dinleme kontrolünde V1 Antalia 1 öğretmen sesi soğuk, mekanik ve yeterince samimi bulunmadı.
Bu değerlendirme kullanıcı tarafından açıkça reddedildiği için V1 konuşma assetleri final kalite standardı olarak kabul edilmez.

V2'nin ana hedefi:

- doğal Türkçe,
- sıcak ve güven veren kadın öğretmen kimliği,
- çocuğa yukarıdan değil yanında konuşan ton,
- kısa yönergelerde samimi ve canlı ama abartısız ifade,
- uzun cümlelerde doğal nefes/durak hissi,
- matematik ve fonemlerde netlik,
- çizgi-film/çocuk taklidi yapmayan yetişkin kadın sesi.

## 2. V2 Ses Motoru

Birincil aday:

- Motor: FreyaTTS-small
- Model: `freyavoice/freya-tts`
- Lisans: Apache-2.0
- Çalışma biçimi: tamamen self-hosted build-time üretim
- Ses kimliği: modelin sabit canonical Leyla seed'i
- Kaynak üretim: 48 kHz WAV
- Uygulama paketi: 24 kHz mono OGG/Vorbis
- Ücretli API: YOK
- API anahtarı: YOK
- Runtime TTS: YOK

Model ve üretim kodu yalnız geliştirme/GitHub Actions aşamasında çalışır. Üretilen OGG dosyaları APK içine statik asset olarak paketlenir.

FreyaTTS-large veya başka ticari/ücretli TTS hizmetleri bu V2 hattında kullanılmaz.

## 3. Öğretmen Profilleri

### TEACHER_WARM
- playback/prosody tempo hedefi: 0.94
- pitch: doğal ses korunur
- kullanım: karşılama, genel yönerge, tracing
- hedef: sıcak, sakin, hafif gülümseyen doğal konuşma

### TEACHER_PHONICS
- tempo hedefi: 0.86
- pitch: doğal ses korunur
- kullanım: harf/ses/hece/kelime
- hedef: çok net artikülasyon, kısa temiz duraklar
- tek harf/fonemler ayrı dinleme kalite kapısından geçer

### TEACHER_MATH
- tempo hedefi: 0.92
- pitch: doğal ses korunur
- kullanım: sayı, sayma, matematik yönergeleri
- hedef: net, ritmik ama mekanik olmayan tempo

### TEACHER_STORY
- tempo hedefi: 0.94
- pitch: doğal ses korunur
- kullanım: anlatım/hikâye
- hedef: doğal ifade, yumuşak cümle sonları

### TEACHER_ENCOURAGE
- tempo hedefi: 0.98
- pitch: doğal ses korunur
- kullanım: başarı ve tekrar-dene ifadeleri
- hedef: kısa, içten, olumlu; bağırmayan ve aşırı neşeli olmayan ton

## 4. Yasaklar

V2'de kullanılmaz:
- ücretli TTS/API servisi,
- API anahtarı gerektiren ses üretimi,
- robotik/metronom gibi tempo,
- yapay pitch yükseltme,
- çocuk sesi taklidi,
- gereksiz dramatik duygu,
- cümle sonlarında sert düşüş,
- aşırı kompresyon,
- tiz/sivri EQ,
- runtime internet/TTS.

## 5. Kalite Kapısı

Tam üretimden önce 10 temsilî örnek dinlenir:

1. sıcak karşılama
2. tracing yönergesi
3. tek fonem
4. fonem yönergesi
5. matematik yönergesi
6. tek sayı
7. kısa hikâye/anlatım
8. başarı geri bildirimi
9. tekrar-dene geri bildirimi
10. uzun doğal öğretmen cümlesi

Kullanıcı dinleme onayı olmadan V2 konuşmalar uygulamadaki V1 assetlerinin üzerine yazılmaz.

## 6. Post-processing

- mono
- 24 kHz
- hedef loudness yaklaşık -18 LUFS
- true peak <= -2 dB
- OGG/Vorbis q5
- pitch değiştirmeden profile uygun tempo
- baş/son sessizlik doğal konuşmayı kesmeyecek şekilde korunur

## 7. Fonem Özel Kuralı

`a, n, e, t, i, l, o, k, u` tek tek kontrol edilir.
Model harf adını okursa veya fonem değerini yanlış verirse klip kabul edilmez.
Bu klipler gerekirse ayrı ücretsiz/offline yöntemle hazırlanır; otomatik toplu üretim sonucu körlemesine kullanılmaz.

## 8. Versiyonlama

V1 assetleri kalite geri dönüşü için Git geçmişinde korunur.
V2 onaylanınca:
- tüm öğretmen konuşmaları yeniden üretilir,
- audio manifest provider/model bilgisi V2'ye geçirilir,
- validator/regression çalıştırılır,
- yeni APK ayrı bakım sürümü olarak üretilir.

---

**IN REVIEW — Audio V2**
