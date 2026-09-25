# Minik Akademi — Build Process

**Status:** LOCKED  
**Version:** 1.0  
**Repository:** uysal23/minik-akademi

Bu dosya projenin resmi build sırasını ve değişiklik kontrol kurallarını tanımlar.

## Kilitli Build Sırası

1. GitHub klasör/dosya mimarisini ve proje kalıbını kilitle.
2. Tam ürün senaryosu ve UI akışını kilitle.
3. Müfredat/içerik sırasını ve kaynak eşlemesini kilitle.
4. Avatar sistemi, görsel stil, animasyon, ses, ebeveyn paneli, yönetim paneli, tema, erişilebilirlik ve çocuk güvenliği kurallarını kilitle.
5. İçerik şemaları ve doğrulama kurallarını oluştur.
6. Android uygulama iskeleti ve navigasyonu geliştir.
7. Çizgi/ön-yazı çalışmaları modülünü geliştir.
8. Türkçe okuma-yazma modülünü geliştir.
9. Matematik modülünü geliştir.
10. Mini oyunları geliştir.
11. Görsel ve ses varlıklarını üret, doğrula ve bağla.
12. Otomatik testleri çalıştır.
13. Manuel pedagojik/UI testlerini çalıştır.
14. Regresyon testlerini tamamla.
15. Release APK build al.

## Aşama Kapısı

- Bir aşama tamamlanıp **LOCKED** durumuna alınmadan sonraki aşamaya geçilmez.
- Kilitli mimari, müfredat, UI akışı veya davranış; proje sahibinin açık onayı olmadan değiştirilmez.
- Düzeltme yapılırken daha önce kilitlenmiş bir özellik sessizce kaldırılmaz veya davranışı değiştirilmez.

## İçerik Kaynak Kuralı

Yüklenen Türkçe ve Matematik materyalleri çekirdek pedagojik kaynaktır.

Her etkinlik şu sınıflardan biri ile işaretlenir:

- **SOURCE:** Kaynaktaki etkinliğin doğrudan dijital uyarlaması.
- **ADAPTED:** Kaynaktaki pedagojik yöntemin yeni ve özgün nesne/örneklerle uyarlanması.
- **EXTENSION:** Kaynaklarda doğrudan bulunmayan fakat proje kapsamına eklenen içerik.

SOURCE ve ADAPTED içeriklerde kaynak izi korunur.

## Çocuk Güvenliği Kuralı

Çocuklara gösterilen tüm içerik:

- yaşa uygun,
- pedagojik,
- şiddetsiz,
- korkutmayan,
- utandırmayan,
- manipülatif ödül döngülerinden uzak,
- reklam ve dış bağlantı içermeyen,
- gereksiz kişisel veri toplamayan

bir yapıda olmak zorundadır.

## Avatar Kuralı

- Çocuk, insan çocuk avatarları arasından kendi avatarını seçer.
- Seçilen avatar çocuk profiline kaydedilir.
- Aynı avatar dashboard, dersler, çizgi çalışmaları, Türkçe, matematik, mini oyunlar, ödül/başarı ve yönlendirme sahnelerinde kullanılır.
- Avatar sistemi proje sahibinin açık onayı olmadan kaldırılmaz veya temel davranışı değiştirilmez.

## Değişiklik Yönetimi

Kilitli bir dokümanda değişiklik gerekiyorsa:

1. Değişiklik gerekçesi yazılır.
2. Etkilenen modüller belirtilir.
3. Sürüm numarası artırılır.
4. Proje sahibinden açık onay alınır.
5. Değişiklik uygulanır.
6. Regresyon kontrolü yapılır.

## Sürümleme

- Büyük mimari değişiklik: **MAJOR**
- Uyumlu özellik/akış genişletmesi: **MINOR**
- Hata düzeltmesi veya açıklama: **PATCH**

Örnek: `1.0.0 → 1.1.0 → 1.1.1`

---

**LOCKED — Build Process V1.0**
