# Minik Akademi — Activity Quality Audit V1

**Status:** OWNER-APPROVED MAINTENANCE  
**Date:** 2026-09-28  
**Scope:** Uygulamadaki 125 authored etkinliğin içerik, pedagojik davranış, cevap doğruluğu, görsel eşleşme ve çocuk güvenliği denetimi.

## Kapsam

Denetim toplam **125 / 125** etkinliği kapsar:

- 52 okuryazarlık etkinliği,
- 5 yazmaya hazırlık / çizgi etkinliği,
- 63 matematik etkinliği,
- 5 mini oyun.

## Kontrol edilenler

1. Öğrenme hedefi, yönerge ve doğru cevap uyumu.
2. Sayma, ritmik sayma, toplama, çıkarma ve gruplama sonuçlarının matematiksel doğruluğu.
3. Konum, karşılaştırma, örüntü, onluk-birlik ve ölçme etkinliklerinin hedefle uyumu.
4. Harf bulma seçeneklerinin gerçekten hedef harf olması.
5. Ses bulma seçeneklerinin hedef sesi gerçekten içerip içermemesi.
6. Ses bulma etkinliğinde kullanılan her nesnenin uygulamada gerçek bir özel görsel karşılığının bulunması.
7. Hece ve kelime hedeflerinin o aşamada açılmış harflerden oluşması.
8. Rakam, harf ve çizgi izleme motorlarında başlangıç, sıra, yön ve gerçek bitiş noktasının zorunlu olması.
9. Çocuk görünür metinlerinde harici URL, reklam, satın alma, cezalandırıcı dil ve yaşa uygunsuz içerik bulunmaması.
10. Güvenlik metadata alanlarının gözden geçirilmiş ve temiz olması.
11. Beş mini oyunun tamamının açık runtime rotasına sahip olması.
12. Etkinliklerin hiçbirinin uygulamada “henüz yapılmamış/placeholder” yola düşmemesi.

## Denetimde giderilen önemli sorunlar

### Harf izleme

Eski harf izleme motoru rakamlarda daha önce tespit edilen eski davranışı taşıyordu:
noktaların yaklaşık %70'ine herhangi bir sırada dokunmak başarı için yeterli olabiliyordu.

Yeni sistem:

- her harfi bir veya daha fazla sıralı hamle olarak tanımlar,
- yalnız aktif hamlenin başlangıç noktasından başlamaya izin verir,
- ilerlemeyi yalnız ileri yönde kabul eder,
- en az %90 ilerleme ve gerçek bitiş noktasına ulaşmayı zorunlu kılar,
- bütün hamleler tamamlanmadan başarı vermez,
- aktif hamlenin yön okunu gösterir,
- üst / kesikli orta / alt yazı kılavuzunu gösterir,
- örnek küçük harfi izleme yoluyla aynı vektör geometrisinden çizer.

### Harf sunumu ve ses kullanımı

Güncel MEB 1. sınıf yaklaşımına uygun olarak:

- önce küçük harf, sonra büyük harf gösterilir,
- ünlülerde ses dinleme kullanılabilir,
- ünsüzler tek başına sesletilmek yerine kelime/görsel bağlamında fark ettirilir.

### Yazmaya hazırlık çizgileri

Eski çizgi motorunda bir yolun farklı noktalarına sıradan bağımsız dokunarak ilerleme biriktirmek mümkündü.

Yeni sistem:

- yalnız büyük başlangıç noktasından başlar,
- ileri sıra penceresi kullanır,
- yolun en az %90'ını ve gerçek bitiş noktasını ister,
- yanlış başlangıç/bitişte yumuşak tekrar dönütü verir.

### Fark bulma

Yanlış şekle dokunulduğunda daha önce hiçbir dönüt oluşmuyordu.
Yanlış seçim artık cezalandırıcı olmayan tekrar dönütünü tetikler.

## Kalıcı QA

`tools/content-validator/audit_activity_quality.py` ve
`tests/automated/test_activity_quality_audit.py` bu denetimi her otomatik test ve
regresyon koşusunda tekrarlar.

## Referans

Güncel MEB 1. sınıf Türkçe programı; harflerin temel formuna ve yazım yönlerine göre
yazılmasını, önce küçük formun ele alınmasını, ses-harf ilişkisinin bağlam içinde
kurulmasını ve kesik çizgilerin yazım yönünde takip edilmesini ister.

## Release

Bu bakım değişikliği yeni release APK build başlatmaz.
Yeni release yalnızca ayrıca owner onayı verildiğinde çalıştırılacaktır.
