# Minik Akademi — UI Flow V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 2

Bu belge bütün ana ekranları ve ekranlar arası geçişleri tanımlar. Görsel ölçüler, renk paleti ve ayrıntılı component stilleri Build Adımı 4'te kilitlenecektir.

## 1. Ana Navigasyon Haritası

```text
APP START
│
├─ İlk kurulum yok
│  └─ Splash
│     └─ Welcome
│        └─ Parent Start Gate
│           └─ Create Child Profile
│              └─ Select Learning Level
│                 └─ Choose Avatar
│                    └─ Sound & Speech Speed
│                       └─ Choose Theme
│                          └─ Create Parent PIN
│                             └─ Setup Summary
│                                └─ Child Dashboard
│
└─ Kurulum tamam
   ├─ Birden fazla profil → Profile Picker
   │  └─ Child Dashboard
   └─ Tek profil → Child Dashboard
```

## 2. Splash

Amaç: uygulamanın açıldığını sakin biçimde göstermek.

İçerik:

- Minik Akademi logosu,
- düz/pastel zemin,
- maksimum yaklaşık 1–1.5 saniyelik geçiş.

Olmaz:

- reklam,
- uzun intro,
- yüksek ses,
- zorunlu "atla" butonu.

## 3. Welcome

Başlık:
**Minik Akademi'ye Hoş Geldiniz**

Alt metin:
Kısa, yetişkine yönelik tek cümle.

Ana buton:
**Kuruluma Başla**

İlk açılışta bu ekran çocuğa değil yetişkine yöneliktir.

## 4. Parent Start Gate

Amaç: ilk kurulumu yetişkinin yaptığını doğrulamak.

Basit yetişkin kapısı kullanılır.

V1 davranışı:

- "Bu bölümü bir yetişkin tamamlamalıdır."
- devam butonu,
- gerektiğinde basit yetişkin doğrulaması.

PIN henüz oluşturulmadığı için ilk kurulum kapısında PIN istenmez.

## 5. Create Child Profile

Alanlar:

- Çocuğun ekranda görünen adı veya takma adı.

Zorunlu olmayan alanlar gösterilmez.

Buton:
**Devam Et**

Validasyon:

- boş bırakılamaz,
- uygunsuz/çok uzun metin kabul edilmez,
- hata mesajı yetişkin dilinde sade gösterilir.

## 6. Select Learning Level

Büyük kartlar:

- Okul Öncesi Başlangıç
- Okul Öncesi İleri
- İlkokul 1

Her kartın altında en fazla bir kısa açıklama.

Seçim daha sonra ebeveyn ayarlarından değiştirilebilir.

## 7. Choose Avatar

Başlık:
**Avatarını Seç**

Görsel yapı:

- ekranda büyük avatar kartları,
- yatay veya iki sütunlu kaydırılabilir alan,
- seçilen kartta belirgin ama yumuşak vurgu.

Dokunma:

1. avatar ön izlenir,
2. karakter küçük el sallama/gülümseme animasyonu yapar,
3. altta büyük onay butonu açılır.

Buton:
**Bu Benim**

Avatar insan çocuk karakteridir.

Seçim sonraki bütün çocuk ekranlarında kullanılır.

## 8. Sound & Speech Speed

İlk kurulumda basit tutulur.

Kontroller:

- Örnek sesi dinle
- Yavaş
- Normal
- Hızlı

Varsayılan:
**Normal — 0.90x**

Seçimde örnek kısa cümle tekrar oynatılır.

Gelişmiş ses kontrolleri ebeveyn ayarlarında bulunur.

## 9. Choose Theme

Büyük tema kartları:

- Pastel
- Doğa
- Gökyüzü
- Yüksek Kontrast

Tema kartına dokunulduğunda ekranın arka planı anında ön izleme olarak değişebilir.

Buton:
**Devam Et**

## 10. Create Parent PIN

Amaç: ebeveyn ve çocuk alanlarını ayırmak.

Akış:

- 4 haneli PIN oluştur,
- tekrar gir,
- eşleşirse devam.

PIN çocuk ekranında gösterilmez.

## 11. Setup Summary

Yetişkin özet ekranı.

Gösterilir:

- çocuk adı,
- avatar,
- eğitim seviyesi,
- tema,
- konuşma hızı.

Buton:
**Çocuk Modunu Başlat**

Bu butondan sonra yetişkin kurulum akışı kapanır.

## 12. Profile Picker

Yalnız birden fazla çocuk profili varsa görünür.

Her profil kartında:

- avatar,
- görünen ad.

Çocuk kartına dokununca dashboard açılır.

Ebeveyn yönetim simgesi küçük ve ayrı yerde bulunur.

## 13. Child Dashboard

### Üst Alan

Sol/orta:

- avatar,
- "Merhaba, [Ad]"

Sağ üst:

- küçük ebeveyn ikonu.

### Ana Alan

En büyük kart/buton:

**DEVAM ET**

Alt açıklama:

- "Sıradaki etkinliğin hazır."

### Alt Kartlar

- Çiziyorum
- Harfleri Öğreniyorum
- Matematik Öğreniyorum
- Oyun Zamanı

### Alt/yan küçük alan

- günlük küçük yıldız/başarı göstergesi,
- tamamlanan etkinlik sayısı gibi sade veri.

Çocuk dashboard'unda detaylı yüzde tabloları gösterilmez.

## 14. Continue Action

**DEVAM ET** basıldığında:

```text
Progress Engine
↓
Tekrar gerekli mi?
├─ Evet → kısa tekrar etkinliği
└─ Hayır → sıradaki açık düğüm
↓
Lesson Intro
```

Bu seçim arka planda yapılır; çocuk teknik karar ekranı görmez.

## 15. Category Screen

Çocuk dashboard'dan kategoriye girerse:

- kategori başlığı,
- avatar,
- görsel öğrenme yolu,
- tamamlanan düğümler,
- sıradaki düğüm,
- açık tekrar düğümleri.

Kategori ekranı uzun metin listesi değildir.

## 16. Learning Path

Düğümler görsel yol üzerinde sıralanır.

Durumlar:

- **Completed:** işaret + yumuşak renk.
- **Next:** hafif animasyon/vurgu.
- **Open:** seçilebilir.
- **Locked:** pasif ama korkutucu kilit dili yok.

Kilit metni:
**Biraz daha çalışınca açılacak.**

## 17. Lesson Intro

Amaç: konuya kısa giriş.

Örnek düzen:

- avatar solda/alt köşede,
- ortada ana nesne/harf/sayı,
- tek kısa sesli yönerge,
- büyük **Başla** butonu.

Giriş 5–10 saniyeyi aşmamalıdır.

## 18. Generic Activity Screen

Temel şablon:

```text
┌──────────────────────────┐
│  Geri     ●●○○     🔊    │
│                          │
│       ANA ETKİNLİK       │
│                          │
│    [büyük nesneler]      │
│                          │
│          Avatar          │
└──────────────────────────┘
```

### Üst Bar

- güvenli geri,
- minimal ilerleme noktaları,
- yönergeyi tekrar dinle.

### Ana Alan

Sadece ilgili görevin nesneleri.

### Avatar

Etkinliğe göre:

- köşede rehber,
- sahnenin parçası,
- hedef karakter

olarak kullanılabilir.

## 19. Tap Choice Activity

Yönerge:
Sesli + gerekirse tek satır.

Nesneler:

- büyük kartlar,
- minimum karmaşa,
- net dokunma alanı.

Doğru:
- seçili nesne küçük bounce,
- kısa olumlu ses,
- sonraki adım.

Yanlış:
- kısa soft shake,
- yönerge tekrar veya ipucu.

## 20. Multi-Select Activity

Örnek:
"Hedef sesi olanları seç."

Doğru seçilen nesne:

- seçili görünür,
- yerinde kalabilir veya yumuşakça vurgulanır.

Yanlış seçim:

- cezalandırılmaz,
- hafif geri bildirim.

Tamamlandığında:
**Kontrol Et** butonu yalnız gerekli etkinlik türlerinde görünür.

## 21. Drag & Drop Activity

Sürüklenebilir nesneler büyük tutulur.

Hedef alan:

- belirgin,
- aşırı küçük değil.

Doğru hedef:
- snap-in animasyonu.

Yanlış hedef:
- nesne başlangıç yerine yumuşakça döner.

## 22. Tracing Activity

Alan:

- büyük noktalı yol/harf/rakam,
- başlangıç noktası,
- yön oku,
- parmak izi geri bildirimi.

Aşamalar:

1. güçlü kılavuz,
2. daha silik kılavuz,
3. gerekirse serbest deneme.

Solak modu etkinse el/yardım görselinin konumu buna göre değiştirilebilir.

## 23. Counting Activity

Ekran başlangıçta sade olabilir.

Nesneler birer birer:

- sağdan/ soldan kayar,
- yumuşakça düşer,
- pop-in olur.

Nesne geldikçe sayı seslendirilebilir.

Sonra:
**Kaç tane var?**

Cevap seçenekleri büyük sayısal kartlar halinde çıkar.

Nesne havuzu içerik verisinden gelir; belirli bir meyve/nesneye sabit değildir.

## 24. Position / Spatial Activity

Avatar veya nesne sahnede kullanılır.

Örnek görev:

- "Avatarını sandalyenin sağına götür."
- "Top masanın altında mı üstünde mi?"

Arka plan yalnız pedagojik ilişkiyi anlatacak kadar basit tutulur.

## 25. Word / Letter Build Activity

Harf kartları ekrana kayarak gelebilir.

Çocuk:

- dokunur,
- sürükler,
- sıraya dizer.

Birleşim sırasında sesler kademeli okunabilir.

Örnek:

```text
A + N → AN
```

Ayrıntılı harf/müfredat sırası Build Adımı 3'te belirlenir.

## 26. Feedback Overlay

Doğru cevapta:

- tüm ekranı kaplamayan küçük yıldız efekti,
- "Harika!" gibi kısa ses,
- 0.5–1.5 saniyelik geçiş.

Yanlışta modal hata ekranı açılmaz.

## 27. Hint State

Aynı soruda tekrarlanan hatada:

1. yönerge tekrar edilir,
2. hedef alan hafif vurgulanır,
3. seçenek sayısı azaltılabilir veya daha kolay varyant sunulabilir.

Bu davranış pedagojik içerik kurallarına göre veriyle kontrol edilebilir.

## 28. Lesson Complete

Gösterilir:

- avatarın sevinç pozu,
- kazanılan yıldız/çıkartma,
- kısa mesaj:
  **Bugünkü çalışmayı tamamladın!**

Butonlar:

- **Devam Et**
- **Ana Sayfa**

Detaylı puan çocuğa gösterilmez.

## 29. Reward Collection

İsteğe bağlı çocuk ekranı.

Gösterilir:

- kazanılmış çıkartmalar,
- boş koleksiyon alanları.

Satın alma, kasa, rastgele ödül kutusu veya para sistemi yoktur.

## 30. Oyun Zamanı

Yalnız açılmış pedagojik mini oyunları gösterir.

Kartlar:

- Harf avı,
- Say ve seç,
- Eşini bul,
- Yol bulma,
- Gruplama

gibi etkinlik motorlarından türeyebilir.

Mini oyunlar müfredattan kopuk bağımlılık döngüsü oluşturmaz.

## 31. Parent Gate

Çocuk dashboard'daki ebeveyn ikonuna:

**yaklaşık 3 saniye basılı tutma**

ile başlanır.

Sonra PIN ekranı.

Yanlış PIN:

- teknik hata yok,
- sakin geri bildirim.

Doğru PIN:
Parent Dashboard.

## 32. Parent Dashboard

Üst:

- aktif çocuk avatarı ve adı,
- profil değiştir.

Özet kartları:

- Bugün: çalışma süresi
- Bu hafta: aktif gün
- Tamamlanan etkinlik
- Son çalışılan konu

İlerleme bölümü:

- Türkçe
- Matematik

Tekrar bölümü:

- sistemin önerdiği tekrarlar.

Alt navigasyon:

- Özet
- Öğrenme
- Ayarlar
- Profiller

## 33. Parent — Learning Detail

Konu/kategori bazlı:

- tamamlandı,
- çalışılıyor,
- tekrar öneriliyor.

Detay:

- çalışma sayısı,
- son çalışma tarihi,
- başarı eğilimi.

Çocuklar arası kıyaslama yoktur.

## 34. Parent — Profile Management

Gösterilir:

- profil listesi,
- profil ekle,
- adı değiştir,
- avatar değiştir,
- eğitim seviyesini değiştir,
- profili sil.

Profil silme:

- yetişkin onayı gerektirir,
- yanlışlıkla tek dokunuşla yapılamaz.

## 35. Parent — Learning Settings

Kontroller:

- günlük çalışma hedefi,
- otomatik ilerleme,
- tekrar sıklığı,
- Türkçe seviyesi,
- Matematik seviyesi.

Müfredat kilitlerini tamamen aşan "her şeyi aç" varsayılan davranış olmayacaktır.

## 36. Parent — Audio Settings

Kontroller:

- anlatıcı açık/kapalı,
- efekt açık/kapalı,
- müzik açık/kapalı,
- ses seviyesi,
- konuşma hızı.

Hızlar:

- 0.80x
- 0.90x
- 1.00x

Her hız için **Örneği Dinle** butonu bulunur.

## 37. Parent — Theme Settings

Kartlar:

- Pastel
- Doğa
- Gökyüzü
- Yüksek Kontrast

Seçim ön izlenir.

Tema seçimi çocuk profilinde saklanır.

## 38. Parent — Accessibility

Kontroller:

- azaltılmış hareket,
- titreşim,
- büyük UI,
- yüksek kontrast,
- solak modu.

Değişiklikler ön izleme ile gösterilebilir.

## 39. Parent — PIN Settings

- PIN değiştir,
- yeni PIN'i doğrula.

PIN unutma akışı V1'in ayrıntılı kimlik mekanizması değildir; daha sonraki teknik tasarımda güvenli cihaz-içi yöntem seçilecektir.

## 40. Parent Exit

Ebeveyn alanında belirgin buton:

**Çocuk Moduna Dön**

Basınca:

- ebeveyn oturumu kapanır,
- aktif profil dashboard'u açılır.

## 41. Hidden Admin Entry

Normal kullanıcı navigasyonunda görünmez.

Yalnız geliştirici/debug veya özel yönetici erişimi ile açılır.

## 42. Admin Dashboard

Planlanan kartlar:

- Content Packs
- Missing Assets
- Missing Audio
- Validation
- Source Mapping
- App/DB Version

Admin ekranının görsel ayrıntıları Build Adımı 4'te kilitlenecektir.

## 43. Android Back Davranışı

### Çocuk etkinliği

Back:
- küçük güvenli çıkış onayı:
  "Etkinlikten çıkmak ister misin?"
- seçenekler:
  - Devam Et
  - Ana Sayfa

### Dashboard

Back:
- uygulamayı yanlışlıkla kapatmaya karşı standart Android davranışı dikkatli uygulanır.

### Parent

Back:
- önceki ebeveyn ekranına gider,
- çocuk moduna geçiş ayrı ve belirgin eylemdir.

## 44. Offline State

Çekirdek ekranlarda "internet yok" engeli gösterilmez.

İnternet gerektirmeyen içerik normal çalışır.

İleride çevrimiçi opsiyon eklenirse yalnız o özellikte durum mesajı gösterilir.

## 45. Empty State

Henüz etkinlik yoksa çocuk ekranında teknik boş liste gösterilmez.

Örnek:
**Yeni etkinlikler hazırlanıyor.**

Ebeveyn/admin alanında daha açıklayıcı bilgi gösterilebilir.

## 46. Loading State

Çocuk ekranında kısa süreli:

- sade spinner yerine küçük karakter/şekil animasyonu kullanılabilir,
- 1–2 saniyeden uzun sürerse anlaşılır mesaj gösterilir.

Aşırı hareket yoktur.

## 47. Error State

Çocuk:

**Tekrar deneyelim.**

Buton:
**Yeniden Dene**

Ebeveyn/admin:
teknik hata ayrıntıları gerektiğinde gösterilebilir.

## 48. UI Dil Kuralları

Çocuk metinleri:

- kısa,
- olumlu,
- tek eylemli,
- mümkünse ses destekli.

Örnek iyi:
"3 balonu bul."

Örnek kötü:
"Aşağıdaki nesneleri dikkatli biçimde inceleyerek toplam üç adet balon bulunan seçeneği işaretleyiniz."

Ebeveyn metinleri daha açıklayıcı olabilir.

## 49. Dokunma Alanı Kuralları

- Çocuk için önemli butonlar büyük olur.
- Küçük icon-only kontroller minimuma indirilir.
- Kritik hedefler birbirine çok yakın yerleştirilmez.
- Yanlış dokunmayı azaltacak boşluk korunur.

Kesin dp ölçüleri Build Adımı 4 design-system belgesinde belirlenir.

## 50. UI Akışı Kabul Kriterleri

Build Step 2 UI akışı tamamlanmış sayılır çünkü:

- ilk kurulum ekranları,
- avatar seçimi,
- çocuk dashboard,
- devam et mekanizması,
- kategori ve öğrenme yolu,
- standart etkinlik şablonları,
- geri bildirim,
- ders sonu,
- ödül ekranı,
- ebeveyn kapısı,
- ebeveyn dashboard,
- öğrenme/ses/tema/erişilebilirlik ayarları,
- profil yönetimi,
- admin giriş sınırı,
- Back/offline/loading/error durumları

tanımlanmıştır.

---

**LOCKED — UI Flow V1.0**
