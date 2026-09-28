# Minik Akademi — Rakam Yazımı Denetimi V1

**Status:** OWNER-APPROVED MAINTENANCE  
**Date:** 2026-09-28  
**Scope:** Matematik / 0–9 rakam yazımı ve izleme motoru

## Amaç

0–9 rakamlarının ekrandaki örnek formunu, başlangıç noktasını, hareket sırasını,
yazım yönünü ve çocuğun parmakla izleme davranışını ayrıntılı olarak denetlemek.

Kaynak standardı olarak güncel MEB/Türkiye Yüzyılı Maarif Modeli'ndeki temel rakam
formları ve yazım yönleri esas alınmıştır. Rakamların yalnızca tanınması değil,
temel forma ve yazım yönlerine uygun yazılması korunmuştur.

## Denetimde Bulunan Sorunlar

- Eski motor bütün rakamı tek bir düz nokta listesi olarak değerlendiriyordu.
- Yalnızca ilk nokta başlangıç noktası olarak gösteriliyordu.
- Noktaların yaklaşık %68'ine herhangi bir sırada dokunmak etkinliği tamamlayabiliyordu.
- Ters yönde veya parçalı gezinme, doğru yazım yönü gibi kabul edilebiliyordu.
- Çok hareketli rakamlarda her hareketin ayrı başlangıcı gösterilmiyordu.
- Üstteki büyük örnek rakam Android sistem fontundan geldiği için izleme geometrisiyle
  birebir aynı değildi.
- Özellikle 2, 3, 5, 6, 8 ve 9 geometrilerinde biçim/birleşim sorunları vardı.
- 0'ın yönü ve 1, 4, 5 gibi çok hareketli rakamların hareket sırası yeterince
  öğretici değildi.

## V1 Düzeltmeleri

### 0

Saat 2 yönüne yakın başlangıç noktasından saat yönünün tersine ilerleyen tek oval hareket.

### 1

Tek kesintisiz hareket: kısa eğik çıkıştan yukarıdan aşağı dik çizgiye devam edilir.

### 2

Tek kesintisiz hareket:
üst kavis → sağdan aşağı/sola iniş → tabanda soldan sağa yatay bitiş.

### 3

Tek kesintisiz hareket; üst ve alt sağ kavis orta birleşimde kontrollü biçimde daralır.

### 4

İki hareket:
1. eğik iniş + yatay çizgi,
2. ayrı yukarıdan aşağı dik çizgi.

### 5

İki hareket:
1. üst yatay çizgi,
2. sol üstten aşağı iniş + alt kavis.

### 6

Tek kesintisiz hareket; üstten sola kıvrılarak aşağı iner ve alt halkayı tamamlar.
Eski kopuk diyagonal parça kaldırılmıştır.

### 7

İki hareket: önce üst yatay çizgi, ardından sağ üstten sol alta eğik iniş.

### 8

İki ayrı kapalı halka yerine saat 2 civarındaki üst-sağ başlangıçtan ilerleyen tek kesintisiz sekiz hareketi kullanılır.

### 9

Üst halka saat 2 civarından başlayıp saat yönünün tersine tamamlanır; aşağı inen kuyruk aynı geometrik akışta birleştirilmiştir.

## İzleme Motoru Kuralları

- Her rakam artık bir veya daha fazla **sıralı hareket** olarak tanımlanır.
- Çocuk yalnızca aktif hareketin büyük başlangıç noktasından başlayabilir.
- Hareket ilerlemesi yalnızca ileri yönde kabul edilir.
- Parmak izi, mevcut ilerlemenin önündeki sınırlı bir nokta penceresine eşleştirilir;
  geriye atlama veya rastgele nokta toplama başarı sağlamaz.
- Bir hareketin en az %90'ı doğru yönde tamamlanmalı ve parmak bitiş noktasına ulaşmalıdır; aksi durumda sonraki harekete geçilmez.
- Bütün hareketler tamamlanmadan etkinlik başarı sayılmaz.
- Her aktif hareket için yön oku gösterilir.
- Yazım alanında üst çizgi, kesikli orta çizgi ve alt çizgi bulunur.
- Ekranın üstündeki örnek rakam sistem fontu yerine **aynı digitGuide geometrisi**
  kullanılarak çizilir. Örnek ile izlenecek biçim böylece birebir aynıdır.

## Otomatik QA

`tests/automated/test_digit_writing_guides.py` şunları korur:

1. 0–9'un tamamında açık rakam geometrisi bulunması,
2. eski hatalı 2/3/6/8 geometrilerinin geri gelmemesi,
3. başlangıç noktası ve ileri yön kontrolü,
4. hareket başına %90 + bitiş noktası tamamlama eşiği,
5. bütün hareketlerin tamamlanma zorunluluğu,
6. örnek rakamın izleme geometrisiyle aynı kaynaktan çizilmesi,
7. 10 rakam etkinliğinin içerik hedefleriyle eşleşmesi.

## Release Durumu

Bu bakım değişikliği için yeni **release APK build başlatılmamıştır**.
Yeni rakam düzeltmelerini içeren release build, ayrıca owner onayı verildiğinde başlatılacaktır.
