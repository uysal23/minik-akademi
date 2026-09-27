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

Birincil sağlayıcı:

- Provider: MiniMax
- Model: `speech-2.8-hd`
- Voice: `Turkish_CalmWoman`
- Language boost: Turkish
- Kaynak üretim: 32 kHz mono WAV
- Uygulama paketi: 24 kHz mono OGG/Vorbis
- Runtime TTS: YOK

Sesler yalnız build/geliştirme aşamasında üretilir ve uygulamaya statik asset olarak paketlenir.

## 3. Öğretmen Profilleri

### TEACHER_WARM
- speed: 0.94
- pitch: 0
- kullanım: karşılama, genel yönerge, tracing
- hedef: sıcak, sakin, hafif gülümseyen doğal konuşma

### TEACHER_PHONICS
- speed: 0.86
- pitch: 0
- kullanım: harf/ses/hece/kelime
- hedef: çok net artikülasyon, kısa temiz duraklar
- tek harf/fonemler ayrı dinleme kalite kapısından geçer

### TEACHER_MATH
- speed: 0.92
- pitch: 0
- kullanım: sayı, sayma, matematik yönergeleri
- hedef: net, ritmik ama mekanik olmayan tempo

### TEACHER_STORY
- speed: 0.94
- pitch: 0
- kullanım: anlatım/hikâye
- hedef: doğal ifade, yumuşak cümle sonları

### TEACHER_ENCOURAGE
- speed: 0.98
- pitch: 0
- kullanım: başarı ve tekrar-dene ifadeleri
- hedef: kısa, içten, olumlu; bağırmayan ve aşırı neşeli olmayan ton

## 4. Yasaklar

V2'de kullanılmaz:
- robotik/metronom gibi tempo,
- yapay pitch yükseltme,
- çocuk sesi taklidi,
- gereksiz dramatik duygu,
- cümle sonlarında sert düşüş,
- aşırı kompresyon,
- tiz/sivri EQ,
- runtime internet/TTS.

## 5. Kalite Kapısı

Tam üretimden önce temsilî örnekler dinlenir:

1. sıcak karşılama
2. tracing yönergesi
3. fonem/hece
4. matematik yönergesi
5. sayı
6. başarı geri bildirimi
7. tekrar-dene geri bildirimi
8. uzun doğal öğretmen cümlesi

Kullanıcı dinleme onayı olmadan V2 konuşmalar uygulamadaki V1 assetlerinin üzerine yazılmaz.

## 6. Post-processing

- DC/sessizlik temizliği
- mono
- 24 kHz
- hedef loudness yaklaşık -18 LUFS
- true peak <= -2 dB
- OGG/Vorbis q5
- baş/son sessizlik doğal konuşmayı kesmeyecek kadar korunur

## 7. Fonem Özel Kuralı

`a, n, e, t, i, l, o, k, u` tek tek kontrol edilir.
Model harf adını okursa veya fonem değerini yanlış verirse klip kabul edilmez.
Bu klipler gerekirse ayrı yöntemle hazırlanır; otomatik toplu üretim sonucu körlemesine kullanılmaz.

## 8. Versiyonlama

V1 assetleri kalite geri dönüşü için Git geçmişinde korunur.
V2 onaylanınca:
- tüm öğretmen konuşmaları yeniden üretilir,
- audio manifest provider/model bilgisi V2'ye geçirilir,
- validator/regression çalıştırılır,
- yeni APK ayrı bakım sürümü olarak üretilir.

---

**IN REVIEW — Audio V2**
