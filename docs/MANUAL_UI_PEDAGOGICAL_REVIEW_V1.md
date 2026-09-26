# Minik Akademi — Manuel Pedagojik / UI İnceleme V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 13

Bu aşama, kullanıcının bilgisayarında hiçbir işlem gerektirmeden GitHub Actions Android emülatörü üzerinde gerçek uygulama ekranlarının gözle incelenmesi için kanıt paketi üretir.

## İnceleme Yöntemi

GitHub-hosted Android emülatörü üzerinde gerçek `MainActivity` açılır. İlk kurulum ve çocuk öğrenme alanları gerçek navigasyon üzerinden geçilir. Her kritik ekran PNG olarak kaydedilir.

Workflow:

`.github/workflows/pedagogical-ui-review.yml`

Instrumentation:

`app/src/androidTest/java/com/uysal/minikakademi/app/PedagogicalUiReviewTest.kt`

## Zorunlu Görsel Kanıtlar

1. Welcome
2. Adult setup gate
3. Child profile
4. Learning level
5. Avatar selection
6. Offline voice/speech speed
7. Theme selection
8. Parent PIN
9. Setup summary
10. Child dashboard
11. Tracing home
12. Tracing activity
13. Literacy home
14. Mathematics home
15. Mathematics category
16. Mathematics activity
17. Mini-games locked state

## Pedagojik İnceleme Kriterleri

Her kanıt ekranı şu kilitli ilkeler bakımından gözle incelenir:

- tek ekranda tek ana öğrenme amacı,
- çocuk metinlerinin kısa ve olumlu olması,
- büyük ve net dokunma hedefleri,
- gereksiz puan/sayaç/rekabet bulunmaması,
- yanlış cevap için cezalandırıcı dil bulunmaması,
- çocuk alanında reklam/satın alma/dış bağlantı bulunmaması,
- seçilen avatar kimliğinin çocuk ekranlarında korunması,
- yönergeyi yeniden dinleme kontrolünün görünmesi,
- kilitli içerik dilinin “Biraz daha çalışınca açılacak.” yaklaşımını koruması,
- EXTENSION matematik bölümünün kaynak-temelli içerikten ayrılması,
- ebeveyn kurulumunun çocuk deneyiminden ayrılması,
- PIN'in çocuk alanını ebeveyn alanından ayırması,
- teknik hata kodlarının çocuk ekranında görünmemesi.

## UI İnceleme Kriterleri

- metin veya ana kontrol ekrandan taşmıyor,
- başlık ve ana eylem görsel hiyerarşisi anlaşılır,
- kartlar arasında yeterli boşluk var,
- kritik butonlar birbirine aşırı yakın değil,
- avatar/öğrenme nesnesi metni kapatmıyor,
- 1080x1920 sınıfı dikey telefon görünümünde kullanılabilir,
- sistem durum/navigasyon alanları kritik kontrolleri örtmüyor,
- çocuk dashboard'undan ana dört öğrenme alanı açıkça ayırt ediliyor,
- geri dönüş eylemi öğrenme ekranlarında görünür.

## Ses / Offline Gözlemi

Emülatör akışı ses oynatımını çağırır fakat GitHub emulator job `-noaudio` ile çalışır; amaç CI ses çıkışı değil, uygulamanın offline audio bağlantısının ekran akışını bozmadığını doğrulamaktır. Offline ses dosyası kapsamı Build Adımı 11 ve 12 validator'ları tarafından ayrıca doğrulanmıştır.

## Kabul Kapısı

Build Adımı 13 yalnızca şu koşullarda LOCKED olabilir:

1. Instrumentation akışı PASS.
2. En az 17 PNG kanıt ekranı üretilmiş olmalı.
3. Kanıt paketi GitHub artifact olarak yüklenmiş olmalı.
4. Ekranlar yukarıdaki pedagojik/UI kriterlerine göre gözle incelenmiş olmalı.
5. Kritik sorun varsa düzeltilip emülatör akışı yeniden çalıştırılmalı.
6. Son Android build ve Adım 12 otomatik test kapısı bozulmamalı.

## Doğrulama Sonucu — 2026-09-26

Final doğrulama commit'i:

`9935f261079c586c513de204086c7b69841b012f`

GitHub Actions sonuçları:

- **Pedagogical UI Review #25** — run `36242603661` — **PASS**
- **Android Build #123** — run `36242603631` — **PASS**
- **Automated Tests #23** — run `36242603745` — **PASS**
- Görsel kanıt sayısı: **17 / 17**
- Kanıt artifact: `minik-akademi-step13-ui-review-evidence`
- Artifact ID: `10905809486`
- Artifact SHA-256 digest: `9a0902c3c3399b151c77eb48ed2838cab5817c18ad4e760af329f8c85b4b19df`
- Instrumentation artifact: `minik-akademi-step13-instrumentation-reports`
- Instrumentation artifact ID: `10906447727`

### Görsel İnceleme Kaydı

| Kanıt | Sonuç | Gözlem |
|---|---|---|
| 01 Welcome | PASS | Yetişkin kurulumu net; dış bağlantı/reklam yok. |
| 02 Adult setup gate | PASS | Çocuk deneyiminden ayrılmış yetişkin kapısı görünür. |
| 03 Child profile | PASS | Kısa yönerge, büyük giriş ve kontrollü devam eylemi. |
| 04 Learning level | PASS | Tek seçim amacı ve net seviye hiyerarşisi. |
| 05 Avatar selection | PASS | 12 insan çocuk avatarı görünür; seçili avatar net; “Bu Benim” eylemi erişilebilir. |
| 06 Offline voice speed | PASS | 0.8x / 0.9x / 1.0x seçenekleri ve offline örnek dinleme görünür. |
| 07 Theme | PASS | Pastel/Doğa/Gökyüzü/Yüksek Kontrast seçenekleri net. |
| 08 Parent PIN | PASS | Ebeveyn alanını çocuk alanından ayıran PIN kurulumu görünür. |
| 09 Setup summary | PASS | Çocuk, seviye, avatar, tema ve konuşma hızı özeti tutarlı. |
| 10 Child dashboard | PASS | Dört ana öğrenme alanı açıkça ayrılıyor; seçilen avatar korunuyor. |
| 11 Tracing home | PASS | Olumlu yönerge, kaynak-temelli etkinlik listesi ve görünür Ana Sayfa eylemi. |
| 12 Tracing activity | PASS | Öğrenme nesnesi, “Dinle” ve Etkinlik Listesi kontrolleri görünür. |
| 13 Literacy home | PASS | Kilitli harf sırası korunuyor; olumlu açılma dili ve görünür Ana Sayfa eylemi. |
| 14 Mathematics home | PASS | Kaynak-temelli kategoriler ile EXTENSION bölümü görsel/metinsel olarak ayrılıyor. |
| 15 Mathematics category | PASS | Önkoşul/kilit dili cezalandırıcı değil; geri dönüş eylemi görünür. |
| 16 Mathematics activity | PASS | Tek matematik amacı, “Dinle”, cevaplar ve geri dönüş kontrolü net. |
| 17 Mini-games locked | PASS | Kilitli oyunlar “Biraz daha çalışınca açılacak.” diliyle gösteriliyor; rekabet/ödül baskısı yok. |

### Run #24 Sonrası Kalite Düzeltmesi

Run #24 teknik olarak PASS olmasına rağmen artifact gözle incelemesinde `05_avatar_selection.png` dosyasının bir önceki Eğitim Seviyesi karesini tekrar ettiği tespit edildi. Bu nedenle Adım 13 kilitlenmedi.

Instrumentation ekran yakalama kodu, her kanıt için fiziksel ekranın önceki kareden gerçekten değişmesini ve iki ardışık yakalamada sabitlenmesini bekleyecek şekilde güçlendirildi. Run #25 sonrasında 17 PNG yeniden gözle incelendi ve avatar karesi dahil tüm kanıtların doğru ekrana ait olduğu doğrulandı.

## Nihai Karar

Kabul kapısındaki 6 koşulun tamamı karşılandı. Kilitli pedagojik, UI, avatar, güvenlik, offline ve içerik ayrımı kurallarında kritik ihlal görülmedi.

---

**LOCKED — Manuel Pedagojik / UI İnceleme V1.0**
