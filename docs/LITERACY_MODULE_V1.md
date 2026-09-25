# Minik Akademi — Türkçe Okuma-Yazma Modülü V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 8

Bu belge, Build Adımı 8 kapsamında çalışan Türkçe okuma-yazma modülünü kilitler.

## 1. Kaynak Sırası

Kaynaklara bağlı harf sırası değiştirilmemiştir:

```text
a → n → e → t → i → l → o → k → u
```

İlk grup:
- a
- n
- e
- t

İkinci grup:
- i
- l
- o
- k
- u

## 2. Gerçek İçerik Sayısı

Toplam **52 JSON etkinliği** oluşturulmuştur.

### a
4 etkinlik:
- harfi tanı
- hedef harfi bul
- hedef sesi içeren varlığı bul
- harfi parmakla yaz

### n, e, t, i, l, o, k, u
Her harf için 6 etkinlik:
- harfi tanı
- hedef harfi bul
- hedef sesi içeren varlığı bul
- harfi parmakla yaz
- kaynakta geçen bir heceyi oluştur
- kaynakta geçen bir kelimeyi oluştur

## 3. Hece / Kelime Örnekleri

Kaynak sırasına uygun seçilen örnekler:

| Harf | Hece | Kelime |
|---|---|---|
| n | an | ana |
| e | en | anne |
| t | at | tane |
| i | in | nine |
| l | al | lale |
| o | on | nota |
| k | ak | kekik |
| u | un | okul |

Bu etkinliklerde yalnız ilgili noktaya kadar açılmış harfler kullanılır.

## 4. Çalışan Etkinlik Motorları

V1'de çalışan Türkçe activity type'ları:

- LETTER_INTRO
- FIND_LETTER
- FIND_SOUND_OBJECT
- TRACE_LETTER
- BUILD_SYLLABLE
- BUILD_WORD

## 5. Harf Tanıma

LETTER_INTRO ekranı:
- büyük harfi,
- küçük harfi,
- seçilen çocuk avatarını,
- kısa yönergeyi

gösterir.

Gerçek kadın öğretmen sesleri Build Adımı 11'de mevcut `audioId` alanlarına offline olarak bağlanacaktır.

## 6. Harf Bulma

FIND_LETTER:
- hedef büyük/küçük harfleri birden fazla kart arasından seçtirir,
- doğru seçimleri işaretli bırakır,
- yanlış seçimde yalnız “Bir daha bakalım.” geri bildirimi verir,
- ceza/puan kaybı üretmez.

## 7. Ses Farkındalığı

FIND_SOUND_OBJECT:
- kaynaklardaki isim örnekleriyle hedef sesi ayırt ettirir,
- birden fazla doğru cevap destekler.

Build Adımı 8 sırasında gerçek resim assetleri henüz üretilmediğinden seçenekler geçici metin kartlarıdır.

Build Adımı 11'de:
- çocuk dostu özgün nesne görselleri,
- ilgili offline kelime/ses dosyaları

aynı JSON activity ID'lerine bağlanacaktır.

Bu durum müfredatı veya etkinlik sırasını değiştirmez.

## 8. Harf Yazma

TRACE_LETTER motoru:
- a, n, e, t, i, l, o, k, u için ayrı noktalı kılavuz oluşturur,
- parmak hareketini gerçek zamanlı çizer,
- kılavuz noktalarına yakın geçen bölümleri kaydeder,
- birden fazla parmak darbesini destekler,
- yaklaşık %70 kılavuz kapsamasında etkinliği tamamlar,
- başlangıç noktasını daha büyük noktayla gösterir,
- yanlış çizimde ceza uygulamaz,
- “Tekrar Çiz” ile yeniden başlanabilir.

## 9. Hece ve Kelime Oluşturma

BUILD_SYLLABLE / BUILD_WORD:
- hedef hece veya kelimenin harflerini kartlara ayırır,
- çocuk harfleri doğru sırayla seçer,
- tekrar eden harfler ayrı kart kimliği taşır,
- yanlış seçimde ilerleme kaybedilmez,
- doğru sıralama tamamlandığında etkinlik başarıyla kaydedilir.

## 10. Kilit / İlerleme

Harfler kilitli curriculum prerequisite sırasına göre açılır.

Örnek:

```text
a tamamlanmadan n açılmaz
n tamamlanmadan e açılmaz
...
t tamamlanmadan i açılmaz
...
k tamamlanmadan u açılmaz
```

Bir harf içindeki etkinlikler de sırayla açılır.

Tamamlanan etkinlik:
- yeniden oynanabilir,
- cihaz içinde yerel olarak saklanır,
- listede ✓ Tamamlandı olarak görünür.

## 11. Avatar Sürekliliği

Seçilen avatar:
- Türkçe ana ekranında,
- harf ekranında,
- etkinlik ekranında

aynı `avatarId` ile kullanılır.

Gerçek avatar çizimleri Build Adımı 11'de placeholder'ın yerini alacaktır.

## 12. Offline Mimari

Türkçe modülü:
- `content/literacy/group_01_anet/`
- `content/literacy/group_02_iloku/`

altındaki JSON'ları APK assetlerinden okur.

Runtime:
- internet kullanmaz,
- Android sistem TTS kullanmaz,
- bulut TTS çağrısı yapmaz.

## 13. Yerel İlerleme

`completedLiteracyActivities` Preferences DataStore'da cihaz içinde tutulur.

Ebeveyn paneli tamamlanan Türkçe etkinliği sayısını gösterir.

## 14. Build Adımı 11'e Bilinçli Olarak Bırakılan Asset Bağlantıları

Build Adımı 8 içerik ve etkileşim motorunu tamamlar.

Kilitli build sürecine göre aşağıdakiler Build Adımı 11'de bağlanacaktır:
- gerçek kadın öğretmen/anlatıcı ses dosyaları,
- gerçek nesne görselleri,
- gerçek insan avatar çizimleri,
- final SFX,
- sesli dinleme etkinliklerinin audio assetleri,
- görsel-kelime eşleştirme için final image assetleri.

Bu varlıkların daha sonra bağlanması Build Adımı 8'in müfredat sırasını veya activity ID'lerini değiştiremez.

## 15. Doğrulama

Final content set:
- **52 literacy activity JSON**
- Content Validation run **36171063019**
- sonuç: **PASS**

Android entegrasyonu:
- Android Build run **36171643893**
- Gradle configuration: **PASS**
- `:app:assembleDebug`: **PASS**
- debug APK artifact upload: **PASS**

## 16. Build Step 8 Kabul Kriteri

Aşağıdakiler tamamlanmıştır:
- a/n/e/t/i/l/o/k/u sırası,
- JSON tabanlı Türkçe içerik,
- harf tanıma,
- harf bulma,
- ses farkındalığı,
- harf yazma,
- hece oluşturma,
- kelime oluşturma,
- curriculum gating,
- avatar sürekliliği,
- offline ilerleme,
- ebeveyn ilerleme göstergesi,
- content validation,
- Android debug build.

---

**LOCKED — Türkçe Okuma-Yazma Modülü V1.0**
