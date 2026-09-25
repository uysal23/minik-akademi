# Minik Akademi — Product Spec V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 2  
**Repository:** uysal23/minik-akademi

Bu belge uygulamanın ürün davranışını tanımlar. Müfredatın ayrıntılı konu sırası Build Adımı 3'te kilitlenecektir. Görsel, ses, avatar, ebeveyn, yönetim ve erişilebilirlik ayrıntıları Build Adımı 4'te kendi belgelerinde derinleştirilecektir. Bu belgede tanımlanan temel kullanıcı davranışı ise V1.0 olarak kilitlidir.

## 1. Ürün Tanımı

Minik Akademi; okul öncesi ve ilkokul başlangıç düzeyindeki çocukların:

- çizgi/ön-yazı becerilerini,
- Türkçe okuma-yazmaya hazırlık ve ilk okuma-yazma becerilerini,
- sayı, sayma ve temel matematik kavramlarını,
- ilerleyen seviyelerde toplama, çıkarma ve çarpma mantığını

çocuk dostu, sade, çizgi-film estetiğinde, dokunmatik ve sesli etkinliklerle çalışmasını sağlayan Android uygulamasıdır.

Çekirdek pedagojik yaklaşım yüklenen Türkçe ve Matematik materyallerine dayanır. Uygulama, materyallerdeki çalışma kâğıdı mantığını doğrudan kopyalamak yerine etkinlikleri etkileşimli dijital deneyime dönüştürür.

## 2. Ürün İlkeleri

1. **Tek ekranda tek öğrenme hedefi.**
2. **Az metin, büyük ve anlaşılır görseller.**
3. **Somuttan soyuta, kolaydan zora ilerleme.**
4. **Dokun, sürükle, izle, dinle, tekrar et.**
5. **Yanlış cevapta ceza, korkutma veya utandırma yok.**
6. **Başarı geri bildirimi kısa ve sakin.**
7. **Çocuk ekranında reklam, satın alma veya dış bağlantı yok.**
8. **Gereksiz kişisel veri toplanmaz.**
9. **Ebeveyn ve çocuk alanları birbirinden ayrılır.**
10. **Seçilen çocuk avatarı bütün çocuk deneyiminde aynı kimlikle kullanılır.**
11. **İçerik ve pedagojik sıra koddan ayrıdır.**
12. **Uygulama mümkün olduğunca çevrimdışı çalışır.**

## 3. Kullanıcı Rolleri

### 3.1 Çocuk

Çocuk:

- kendi profilini seçer,
- kendine insan çocuk avatarı seçebilir,
- ana dashboard üzerinden devam eder,
- öğrenme yolunu ve açık etkinlikleri görür,
- etkinlikleri oynar,
- tamamladığı içerikleri tekrar ziyaret eder,
- ödül/başarı ekranlarını görür.

Çocuk:

- ebeveyn ayarlarına doğrudan giremez,
- geliştirici/yönetim paneline erişemez,
- harici web bağlantısı açamaz,
- satın alma işlemi başlatamaz.

### 3.2 Ebeveyn / Yetişkin

Ebeveyn:

- ilk kurulumu yapar,
- çocuk profili oluşturur,
- eğitim seviyesini seçer,
- avatar seçimini başlatabilir veya değiştirebilir,
- günlük çalışma hedefini belirler,
- ses/konuşma hızı ve tema ayarlarını yapar,
- ilerlemeyi görüntüler,
- erişilebilirlik seçeneklerini yönetir,
- gerektiğinde öğrenme seviyesini düzenler.

### 3.3 Geliştirici / Yönetici

Normal kullanıcıdan gizli yönetim alanıdır.

Burada ileride:

- içerik paketlerinin durumu,
- eksik görsel/ses,
- içerik doğrulama,
- kaynak eşlemesi,
- sürüm/veritabanı bilgisi

görülebilir.

Bu alan çocuk ve ebeveyn ürün akışının parçası değildir.

## 4. İlk Çalıştırma

Uygulama ilk açıldığında çocuk doğrudan etkinliğe bırakılmaz.

Kilitli sıra:

```text
Splash
→ Hoş Geldiniz
→ Ebeveyn Başlangıç Kapısı
→ Çocuk Profili Oluştur
→ Eğitim Seviyesi
→ Avatar Seç
→ Temel Ses/Tempo Ayarı
→ Tema
→ Ebeveyn PIN'i
→ Kurulum Özeti
→ Çocuk Dashboard
```

Kurulum tamamlandıktan sonra sonraki açılışlarda doğrudan profil seçimi veya tek profil varsa çocuk dashboard'u açılır.

## 5. Çocuk Profili

Her çocuk profilinde en az şu bilgiler tutulur:

- profileId
- görünen ad veya takma ad
- seçilen eğitim seviyesi
- avatarId
- tema tercihi
- ses/konuşma hızı
- erişilebilirlik tercihleri
- öğrenme ilerlemesi
- tekrar geçmişi
- tamamlanan etkinlikler
- son açık öğrenme düğümü

Doğum tarihi zorunlu değildir.

## 6. Avatar Sistemi — Ürün Davranışı

Avatarlar insan çocuk karakterleri olacaktır.

İlk sürümde:

- birden fazla hazır avatar,
- farklı ten tonları,
- farklı saç biçimleri,
- farklı saç renkleri,
- farklı yüz görünümleri,
- çocuk yaşına uygun günlük kıyafetler

sunulacaktır.

Avatar seçimi:

1. Çocuk kartlara dokunarak avatarları ön izler.
2. Seçilen avatar kısa, sakin bir animasyon verir.
3. "Bu benim" benzeri tek büyük onay butonu ile seçim kaydedilir.
4. Ebeveyn ayarlarından sonradan değiştirilebilir.

Seçilen avatar:

- çocuk dashboard'unda,
- öğrenme yolu ekranında,
- ders girişinde,
- çizgi çalışmalarında,
- Türkçe etkinliklerinde,
- matematik etkinliklerinde,
- mini oyunlarda,
- başarı/ödül ekranlarında,
- yönlendirme sahnelerinde

aynı kimlikle kullanılır.

Avatar yalnız profil fotoğrafı değildir; bazı etkinliklerde pedagojik nesne/karakter olarak da kullanılabilir.

## 7. Çocuk Dashboard

Dashboard çocuk için ana merkezdir.

Üst alan:

- çocuk adı,
- seçilen avatar,
- küçük, sade ilerleme göstergesi.

Ana eylem:

**DEVAM ET**

Bu buton:

1. son ilerlemeyi kontrol eder,
2. gerekiyorsa kısa tekrar önerir,
3. sıradaki açık etkinliği seçer,
4. öğrenme oturumunu başlatır.

Alt ana alanlar:

- Çiziyorum
- Harfleri Öğreniyorum
- Matematik Öğreniyorum
- Oyun Zamanı

İleri içerikler kilitli olabilir. Kilit dili cezalandırıcı olmayacaktır.

Örnek:
"Biraz daha çalışınca açılacak."

## 8. Öğrenme Yolu

Çocuk serbest ve karmaşık menülere bırakılmaz.

Ana eğitim deneyimi görsel bir öğrenme yolu üzerinden ilerler.

Her düğüm şu durumlardan birindedir:

- tamamlandı,
- sıradaki,
- açık,
- henüz açılmadı.

Tamamlanan etkinlik yeniden oynanabilir.

Yeni içerik açılması müfredat kurallarına göre belirlenir; ayrıntılı sıra Build Adımı 3'te kilitlenecektir.

## 9. Standart Öğrenme Döngüsü

Her ders mümkün olduğunca aynı zihinsel modeli kullanır:

```text
TANIŞ
→ GÖR
→ DİNLE
→ DENE
→ OYNA
→ TEKRAR ET
→ TAMAMLA
```

Bu yapı konuya göre kısaltılabilir ancak çocuk yeni bir ekran mantığı öğrenmek zorunda kalmamalıdır.

## 10. Standart Etkinlik Ekranı

Her etkinlik ekranında:

- üstte çok küçük ilerleme göstergesi,
- ana öğrenme alanı,
- büyük dokunma hedefleri,
- kısa sesli yönerge,
- gerektiğinde yönergeyi tekrar dinleme butonu,
- çıkış/geri butonu

bulunur.

Ekranda:

- reklam,
- gereksiz sayaç,
- puan baskısı,
- sosyal özellik,
- karmaşık menü

bulunmaz.

## 11. Etkinlik Etkileşim Tipleri

Ürün, ileride içerik motoruyla şu etkileşimleri destekleyecektir:

- dokunarak seçme,
- çoklu doğru seçme,
- sürükle-bırak,
- eşleştirme,
- çizgi/parmak izi takip etme,
- harf/rakam üzerinden geçme,
- nesne sayma,
- nesne gruplama,
- sıralama,
- yön/konum seçme,
- görsel-kelime eşleştirme,
- harf/hece/kelime oluşturma,
- basit işlem görselleştirme.

Ayrıntılı JSON türleri Build Adımı 5'te kilitlenecektir.

## 12. Hareketli Materyal İlkesi

Tek bir nesneye bağlı kalınmaz.

Etkinliğe göre çocukların ilgisini çekebilecek:

- hayvanlar,
- meyveler,
- sebzeler,
- oyuncaklar,
- okul eşyaları,
- ulaşım araçları,
- çiçekler,
- yapraklar,
- yıldızlar,
- balonlar,
- bloklar,
- deniz canlıları,
- ev eşyaları,
- doğa nesneleri

kullanılabilir.

Nesneler:

- kayarak,
- yumuşakça düşerek,
- küçük pop animasyonu ile

ekrana girebilir.

Ancak arka plan hareketi minimum tutulur.

## 13. Geri Bildirim

### Doğru cevap

Örnek davranış:

- nesne bir kez zıplar,
- hafif yıldız efekti,
- kısa ses: "Harika!", "Doğru!", "Evet!"

Aşırı kutlama yapılmaz.

### Yanlış cevap

- büyük kırmızı X kullanılmaz,
- yüksek alarm sesi kullanılmaz,
- puan düşürülmez,
- karakter üzülmez veya çocuğu suçlamaz.

Örnek:

- hafif sağ-sol hareket,
- "Bir daha bakalım.",
- "Tekrar deneyelim.",
- gerektiğinde ipucu.

Tekrarlanan hata durumunda daha basit varyasyon veya aynı kazanımın tekrar etkinliği açılabilir.

## 14. Öğrenme Oturumu

Varsayılan çocuk oturumu yaklaşık 8–12 dakika hedeflenir.

Örnek yapı:

- kısa ısınma,
- ana konu,
- etkileşimli oyun,
- tekrar,
- mini kontrol,
- kapanış.

Çocuğa geri sayan stresli bir zamanlayıcı gösterilmez.

Ebeveyn günlük hedefi değiştirebilir.

## 15. Mini Değerlendirme

Değerlendirme:

- sınav görünümünde olmamalı,
- kısa olmalı,
- aynı kazanımı farklı nesnelerle ölçmeli,
- yanlışlarda öğretim döngüsüne geri bağlanmalıdır.

Sonuç çocuk için:

- "Tamamladın",
- "Bir kez daha deneyelim"

gibi sade dille gösterilir.

Yüzde/puan gibi ayrıntılı sonuçlar çocuk ekranında zorunlu değildir.

## 16. Ödül Sistemi

Manipülatif para/coin ekonomisi kullanılmaz.

Kullanılabilecek ödüller:

- yıldız,
- çıkartma,
- tamamlandı rozeti,
- koleksiyon kitabında yeni görsel.

Ödül öğrenmenin önüne geçmez.

## 17. Ebeveyn Giriş Kapısı

Çocuk dashboard'unda yetişkin alanına giden küçük bir ikon bulunabilir.

Giriş:

1. yetişkin ikonuna uzun basma,
2. ebeveyn PIN'i,
3. ebeveyn dashboard.

Çocuk yanlışlıkla tek dokunuşla ebeveyn alanına geçemez.

## 18. Ebeveyn Dashboard

Ebeveyn şu bilgileri görebilir:

- bugün çalışma süresi,
- bu hafta çalışma günleri,
- tamamlanan etkinlikler,
- Türkçe ilerlemesi,
- Matematik ilerlemesi,
- son çalışılan konu,
- tekrar önerileri,
- konu bazlı güçlü/zorlanan alanlar.

Ebeveyn dashboard'unda çocuklar arası sıralama veya rekabet yoktur.

## 19. Ebeveyn Ayarları

### Profil

- görünen ad,
- avatar değiştir,
- eğitim seviyesi,
- profil değiştir/sil.

### Öğrenme

- günlük hedef süre,
- otomatik ilerleme,
- tekrar sıklığı,
- Türkçe öğrenme seviyesi,
- Matematik öğrenme seviyesi.

### Ses

- anlatıcı açık/kapalı,
- efekt sesi,
- arka plan müziği,
- genel ses seviyesi,
- konuşma hızı.

Temel konuşma hızları:

- Yavaş: 0.80x
- Normal: 0.90x — varsayılan
- Hızlı: 1.00x

### Tema

En az:

- Pastel
- Doğa
- Gökyüzü
- Yüksek Kontrast

Tema öğrenme içeriğini değiştirmez.

### Erişilebilirlik

- azaltılmış hareket,
- titreşim kapatma,
- büyük UI,
- yüksek kontrast,
- solak kullanım modu.

## 20. Konuşma ve Ses İlkesi

Çekirdek harf/ses öğretiminde tutarlı, önceden hazırlanmış offline ses dosyaları tercih edilir.

TTS:

- çekirdek fonemlerin tek kaynağı olmayacaktır,
- yardımcı yönergelerde gerekirse yedek olarak kullanılabilir.

Seslendirme:

- kısa,
- sıcak,
- sakin,
- açık telaffuzlu

olacaktır.

## 21. Tema Davranışı

Tema yalnızca:

- zemin,
- kart,
- buton,
- vurgu renkleri,
- küçük dekor öğeleri

üzerinde etkilidir.

Pedagojik nesne rengi, doğru cevabı veya içerik mantığını tema değiştiremez.

## 22. Yönetim / Geliştirici Paneli

Bu alan normal kullanıcıdan gizlidir.

Planlanan özet bilgiler:

- içerik paketi durumu,
- etkinlik sayısı,
- eksik görseller,
- eksik sesler,
- JSON validation,
- source mapping eksikleri,
- içerik sürümü,
- veritabanı sürümü.

Bu panel çocuk için görünmez ve ürünün günlük kullanımını etkilemez.

## 23. Çevrimdışı Çalışma

Çekirdek uygulama:

- içerik,
- görsel,
- ses,
- ilerleme

için internet bağlantısına bağımlı olmamalıdır.

Çocuğun günlük öğrenme akışı çevrimdışı devam edebilmelidir.

## 24. Veri ve Gizlilik

Varsayılan yaklaşım veri minimizasyonudur.

Zorunlu olmayan bilgiler istenmez.

Çocuk alanında:

- konum,
- rehber,
- mikrofon,
- kamera,
- hesap bağlantısı

gibi izinler çekirdek öğrenme için kullanılmaz.

İleride bir özellik bunlardan birini gerektirirse ayrı tasarım ve açık ebeveyn onayı gerektirir.

## 25. Ekran Yönü ve Boyut

V1 ana deneyimi Android telefon ve tabletlerde çalışacaktır.

UI:

- farklı ekran oranlarına uyarlanır,
- büyük dokunma alanları kullanır,
- kritik bilgi ekran kenarlarına sıkıştırılmaz.

Dikey kullanım varsayılan tasarım yönüdür. Etkinlik türü gerektirirse ileride yatay destek ayrıca değerlendirilir; otomatik yön değişimi V1 ürün davranışının zorunlu parçası değildir.

## 26. Navigasyon Kuralları

- Çocuk hiçbir ekranda çıkmaza girmez.
- Sistem Back tuşu etkinliği yanlışlıkla tamamen kapatmaz; gerekiyorsa güvenli geri dönüş sunar.
- Etkinlikten çıkış ilerlemeyi bozmaz.
- Tamamlanmış etkinlik yeniden oynanabilir.
- Çocuk ana dashboard'a her zaman güvenli biçimde dönebilir.
- Ebeveyn alanından çocuk alanına tek eylemle dönülebilir.

## 27. Durum Kaydı

Uygulama en az şu durumları yerel olarak saklar:

- kurulum tamamlandı mı,
- aktif çocuk profili,
- avatar,
- tema,
- ses tercihleri,
- erişilebilirlik tercihleri,
- açık/kapalı öğrenme düğümleri,
- tamamlanan etkinlikler,
- etkinlik başarı durumu,
- tekrar ihtiyacı,
- son ders/ekran.

Uygulama yeniden açıldığında çocuk kaldığı yerden devam edebilmelidir.

## 28. Hata / Boş Durumlar

Çocuğa teknik hata kodları gösterilmez.

Çocuk-facing hata mesajı örneği:

"Bir şey olmadı, tekrar deneyelim."

Ebeveyn/admin alanında ise teknik ayrıntı ve tanılama bilgisi gösterilebilir.

## 29. Ürün Dışı Kapsam — V1

V1 için çekirdek kapsamda değildir:

- reklam,
- uygulama içi satın alma,
- çocuklar arası çevrimiçi iletişim,
- sosyal medya paylaşımı,
- canlı sohbet,
- açık internet tarayıcısı,
- konum tabanlı özellik,
- herkese açık liderlik tablosu.

## 30. Build Step 2 Kabul Kriterleri

Bu ürün senaryosu tamamlanmış sayılır çünkü:

- ilk çalıştırma tanımlandı,
- çocuk rolü tanımlandı,
- ebeveyn rolü tanımlandı,
- avatar davranışı tanımlandı,
- dashboard davranışı tanımlandı,
- öğrenme yolu tanımlandı,
- etkinlik ekranı tanımlandı,
- geri bildirim tanımlandı,
- ebeveyn panelinin işlevsel sınırı tanımlandı,
- tema/ses/erişilebilirlik üst seviye davranışı tanımlandı,
- yönetim panelinin sınırı tanımlandı,
- offline ve veri minimizasyonu kuralları tanımlandı.

---

**LOCKED — Product Spec V1.0**
