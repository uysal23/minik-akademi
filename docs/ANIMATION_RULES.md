# Minik Akademi — Animation Rules V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 4

## Amaç

Animasyon öğrenme nesnesini görünür hâle getirir; dikkat dağıtıcı sürekli hareket oluşturmaz.

## Standart Girişler

- SLIDE_LEFT
- SLIDE_RIGHT
- SLIDE_UP
- SOFT_DROP
- POP_IN
- FADE_IN

Genel giriş süresi: **450–750 ms**.

## Öğrenme Nesnesi Hareketi

Harf, rakam veya sayılacak nesne:
1. ekrana girer,
2. tamamen durur,
3. öğretmen sesi başlar,
4. çocuk etkileşimi açılır.

Konuşma sürerken gereksiz sürekli hareket yapılmaz.

## Sayma

Nesneler tek tek giriyorsa her giriş arasında yeterli algılama aralığı bırakılır.

Örnek:
```
nesne 1 girer → “bir”
nesne 2 girer → “iki”
nesne 3 girer → “üç”
```

## Doğru Cevap

- BOUNCE_ONCE
- SOFT_GLOW
- SMALL_STARS

Toplam kutlama süresi çoğunlukla **0.5–1.5 s**.

## Yanlış Cevap

- SOFT_SHAKE
- kısa hedef vurgusu
- gerekiyorsa yönerge tekrarı

Yasak:
- sert titreşim,
- kırmızı flaş,
- ekran sarsılması,
- yüksek hızda yanıp sönme.

## Avatar

Avatar:
- küçük el sallama,
- yürüme,
- işaret etme,
- alkışlama

gibi kısa hareketler yapabilir.

Avatar animasyonu öğrenme nesnesiyle eşzamanlı dikkat rekabeti oluşturmaz.

## Reduced Motion

“Azaltılmış Hareket” açıksa:
- slide yerine fade tercih edilir,
- bounce genliği düşer,
- yıldız parçacıkları azaltılır,
- dekor animasyonu tamamen kapanır.

## Loop Kuralı

Çocuk ekranında süresiz loop yalnız:
- çok hafif idle nefes/göz kırpma

için kullanılabilir.

Parlayan, zıplayan veya dönen öğeler sürekli loop yapmaz.

---

**LOCKED — Animation Rules V1.0**
