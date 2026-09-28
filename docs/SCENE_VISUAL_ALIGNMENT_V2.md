# Minik Akademi — Scene Visual Alignment Maintenance V2

**Status:** OWNER-APPROVED MAINTENANCE  
**Date:** 2026-09-28  
**Scope:** Build Steps 11–14 maintenance; curriculum/activity IDs are unchanged.  
**Branch:** visual-alignment-v2

## Amaç

125 authored sahnenin yönerge/hedef/seçenekleri ile runtime görsel anlatımını aynı pedagojik anlama getirmek. Önceki denetimde işaretlenen 24 uyumsuz ve 10 kısmi uyumlu sahne aşağıdaki sahne-özel tasarımlarla düzeltilir. Diğer 91 sahnenin öğretim hedefi ve davranışı değiştirilmez.

## Görsel standardı

- Tamamen offline ve özgün.
- Kaynak illüstrasyon kopyası yok.
- Sıcak 2.5D / CGI-inspired vector görünüm: zemin gölgesi, kart yüksekliği, katmanlı nesne yüzeyleri ve yumuşak highlight.
- Bir ekran = bir ana öğrenme hedefi.
- Görsel, cevabı çelişkili biçimde öğretmez; yönergeyi doğrudan destekler.
- Safe-area ve mevcut erişilebilirlik davranışları korunur.

## 34 sahnelik tek tek uygulama planı

| # | Sahne ID | Önceki sorun | V2 runtime görseli | Durum |
|---:|---|---|---|---|
| 1 | ACT-MAT-CMP-01 | 3↔5 gösterip eşitlik soruyordu | 3 elma = 3 armut, birebir eşit nicelik | IMPLEMENTED |
| 2 | ACT-MAT-CMP-02 | Görsel sağ grubu daha çok gösteriyordu | 6 elma > 4 armut | IMPLEMENTED |
| 3 | ACT-MAT-SP-06 | Büyük/küçük algısı yükseklik yerine geçiyordu | Eş boyutlu pembe balon üst sırada, yeşil balon alt sırada | IMPLEMENTED |
| 4 | ACT-MAT-TENS-01 | Onluk/birlik modeli yoktu | 10 birlik bloğu → 1 onluk çubuğu | IMPLEMENTED |
| 5 | ACT-MAT-TENS-02 | 18’in ayrışımı görsel değildi | 1 onluk + 8 birlik = 18 | IMPLEMENTED |
| 6 | ACT-MAT-TENS-03 | Abaküs yoktu | 1 onluk ve 5 birlik boncuklu abaküs | IMPLEMENTED |
| 7 | ACT-MAT-TENS-04 | Abaküs yoktu | 1 onluk ve 7 birlik boncuklu abaküs | IMPLEMENTED |
| 8 | ACT-MAT-TENS-05 | Birim küp modeli yoktu | 1 onluk küp çubuğu + 3 birlik | IMPLEMENTED |
| 9 | ACT-MAT-TENS-06 | Onluk-birlik görseli yoktu | 1 onluk küp çubuğu + 6 birlik = 16 | IMPLEMENTED |
| 10 | ACT-MAT-MASS-02 | Terazi yerine ilgisiz nesneler vardı | İki kefesi aynı seviyede dengeli terazi | IMPLEMENTED |
| 11 | ACT-MAT-LEN-02 | Sınıf ölçümü yerine kalem/silgi vardı | Okul/sınıf bağlamı + adım izleri | IMPLEMENTED |
| 12 | ACT-MAT-LEN-04 | Masa/kalemlik yerine kalem/silgi vardı | Masa 5 karış > kalemlik 2 karış | IMPLEMENTED |
| 13 | ACT-MAT-LEN-05 | Tahmin/ölçüm farkı görsel değildi | 6 karış tahmin satırı + 5 karış ölçüm satırı + fark 1 | IMPLEMENTED |
| 14 | ACT-MAT-MASS-03 | Karpuz/elma/çilek yoktu | Karpuz > elma > çilek boyut/kütle karşılaştırması | IMPLEMENTED |
| 15 | ACT-MAT-MASS-04 | Ağırdan hafife sıralama görünmüyordu | Karpuz → elma → çilek | IMPLEMENTED |
| 16 | ACT-MAT-MEASURE-ADAPT | Masa ölçümü yerine genel nesne vardı | Masa + 5 karış göstergesi | IMPLEMENTED |
| 17 | ACT-MAT-PAT-01 | Yalnız AAB metni vardı | Sarı-sarı-mor-sarı-? gerçek renk örüntüsü | IMPLEMENTED |
| 18 | ACT-MAT-PAT-02 | Yalnız AB metni vardı | Üçgen-daire-üçgen-daire-? | IMPLEMENTED |
| 19 | ACT-MAT-PAT-03 | Şekil örüntüsü görünmüyordu | Kare-kare-daire-kare-? | IMPLEMENTED |
| 20 | ACT-MAT-PAT-04 | Yol örüntüsü görünmüyordu | Yıldız-daire-yıldız-daire-? | IMPLEMENTED |
| 21 | ACT-MAT-EQ-01 | 3 elma↔5 armut ortak şablonu | Aynı araba = aynı araba | IMPLEMENTED |
| 22 | ACT-MAT-EQ-02 | Eş şekil gösterilmiyordu | Üçgen = üçgen | IMPLEMENTED |
| 23 | ACT-MAT-EQ-03 | Eş nesne gösterilmiyordu | Kalem = aynı kalem | IMPLEMENTED |
| 24 | ACT-MAT-EQ-04 | Fazla nesne senaryosu yoktu | Kitap+kalem+top → top çıkar → kitap+kalem | IMPLEMENTED |
| 25 | ACT-EXT-SUB-01 | Ayrılan iki nesne görünmüyordu | 5 balon → 2 ayrılır → 3 balon kalır | IMPLEMENTED |
| 26 | ACT-MAT-LEN-03 | Ölçme aracı görsel değildi | Silgi + parmak | IMPLEMENTED |
| 27 | ACT-MAT-MASS-05 | Kitap/tüy/pamuk yerine kitap/silgi | Kitap, tüy, pamuk aynı sahnede | IMPLEMENTED |
| 28 | ACT-MAT-NUM-01 | 19’un rakam yapısı ayrışmıyordu | 1 rakamı + 9 rakamı → 19 sayısı | IMPLEMENTED |
| 29 | ACT-MAT-ORD-01 | Sıra kavramı görsel değildi | 1.–5. kartlar; 3. kart yükseltilmiş | IMPLEMENTED |
| 30 | ACT-MAT-REV-01 | 12/8 karşılaştırma görsel değildi | 12 nesne > 8 nesne | IMPLEMENTED |
| 31 | ACT-MAT-REV-02 | Soldan üçüncü görsel değildi | 5 kartlık sıra; soldan 3. vurgulu | IMPLEMENTED |
| 32 | ACT-MAT-SP-02 | Tek ağaç yerine orman kullanılıyordu | Tek ağaç ve çevresinde kuşlar | IMPLEMENTED |
| 33 | ACT-MAT-SP-04 | Ön/arka ilişkisi belirsizdi | Büyük/önde araba; daha küçük/arkada kamyon + etiket | IMPLEMENTED |
| 34 | ACT-MAT-SP-07 | İçinde/üstünde ayrımı zayıftı | Muz yükseltilmiş tabak yüzeyinin merkezinde, “içinde” etiketi | IMPLEMENTED |

## Ek düzeltmeler

- ACT-MAT-LEN-01 için gerçek uzun/kısa iki çizgi kullanılır.
- ACT-MAT-MASS-01 için kitap/silgi gerçek nesne görselleri kullanılır.
- LearningObjectArt V2 nesneleri: kitap, tüy, pamuk, karpuz, çilek, kalemlik, sandalye, ağaç, muz, kamyon; ayrıca pembe/yeşil balon varyantları.
- Öğrenme nesnelerine zemin gölgesi ve yumuşak highlight eklenerek ortak 2.5D derinlik dili uygulanır.

## QA hedefi

1. Authored scene count = 125.
2. Flagged remediation set = 34/34 covered.
3. Önceden uygun 91 sahnenin içerik/hedef IDs'i değişmez.
4. Visual validator PASS.
5. Automated tests PASS.
6. Debug Android build PASS.
7. Regression gate PASS.
8. Release APK build bu bakım kapsamında başlatılmaz; proje sahibinin ayrı açık onayı gerekir.
