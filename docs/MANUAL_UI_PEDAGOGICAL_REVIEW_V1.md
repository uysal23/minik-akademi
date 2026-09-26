# Minik Akademi — Manuel Pedagojik / UI İnceleme V1

**Status:** VERIFYING  
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

Tüm kriterler tamamlandığında durum **LOCKED V1.0** yapılacaktır.
