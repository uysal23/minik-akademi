# Minik Akademi — Audio Style Guide V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 4

## 1. Ana Karar

Uygulama **telefon/tabletin sistem TTS sesini veya sistem bildirim seslerini kullanmayacaktır.**

Çekirdek sesler:
- proje için önceden üretilir,
- kalite kontrolünden geçirilir,
- APK içine paketlenir,
- internet bağlantısı olmadan oynatılır.

Runtime sırasında bulut TTS çağrısı yapılmaz.

## 2. Öğretmen / Anlatıcı

Bütün çekirdek öğretim ve anlatım sesleri **yumuşak kadın öğretmen sesi** olacaktır.

Tek bir ses kimliği korunur; farklı görevler için farklı **ses profilleri/prosodi profilleri** uygulanır.

### TEACHER_WARM

Kullanım:
- genel yönerge,
- dashboard karşılama,
- ders girişi.

Karakter:
- sıcak,
- sakin,
- güven verici,
- doğal.

Hız hedefi: yaklaşık **0.90x**.

### TEACHER_PHONICS

Kullanım:
- harf,
- ses/fonem,
- hece,
- kelime okuma.

Karakter:
- çok net artikülasyon,
- daha yavaş,
- gereksiz duygusal vurgu yok,
- fonem öncesi/sonrası temiz sessizlik.

Hız hedefi: yaklaşık **0.80x**.

### TEACHER_MATH

Kullanım:
- sayma,
- sayı,
- matematik yönergeleri.

Karakter:
- ritmik ama şarkı söyler gibi değil,
- sayıların birbirine karışmaması için net kısa duraklar.

Hız hedefi: yaklaşık **0.88–0.90x**.

### TEACHER_STORY

Kullanım:
- dinleme metni,
- kısa hikâye,
- sahne tanıtımı.

Karakter:
- sıcak,
- hafif ifade zenginliği,
- kısa cümle sonlarında doğal durak.

Hız hedefi: yaklaşık **0.88x**.

### TEACHER_ENCOURAGE

Kullanım:
- “Harika!”
- “Doğru!”
- “Tekrar deneyelim.”
- “Bir daha bakalım.”

Karakter:
- kısa,
- neşeli fakat yüksek enerjili değil,
- çocukta baskı yaratmayan.

Hız hedefi: yaklaşık **0.95x**.

## 3. Ücretsiz / Açık Ses Üretim Stratejisi

### Birincil aday: Antalia 1 — build-time üretim

Antalia 1:
- Türkçe tek konuşmacılı bir TTS modelidir,
- yayımlanan ses kimliği profesyonel bir Türk kadın seslendirme sanatçısının izinli kayıtlarına dayanır,
- ses ve model hak zinciri açık biçimde belgelenmiştir,
- model cihazda çalıştırılmayacaktır; yalnız geliştirme/build aşamasında ses dosyası üretmek için kullanılır.

Üretilen tüm sesler sonradan APK içine statik dosya olarak eklenir.

Lisans/etik koşul:
- yapay ses olduğu uygulamanın yetişkin “Hakkında / Açık Kaynak ve Ses Kaynakları” bölümünde belirtilir,
- gerekli Antalia 1 atfı korunur,
- gerçek kişi taklidi veya yanıltıcı kullanım yapılmaz.

### İkincil değerlendirme adayı: FreyaTTS-small

FreyaTTS-small:
- Türkçe,
- self-hosted,
- Apache-2.0,
- offline build-time üretime uygundur.

Ancak kadın öğretmen sesi şartı nedeniyle üretim hattına ancak dinleme testiyle ses kimliği uygun bulunursa alınır.

### Kullanılmayacak üretim seçeneği: Piper tr_TR-dfki

Teknik olarak offline Türkçe TTS'dir; fakat mevcut Türkçe dfki model kartında eğitim verisi CC BY-NC-SA 4.0 olarak belirtilmektedir. Bu nedenle V1 üretim hattında kullanılmaz.

## 4. Çoklu Ses Profili Nasıl Oluşturulacak?

Farklı “öğretmen” profilleri farklı kişileri taklit etmeyecek.

Aynı izinli/sentetik kadın ses kimliği:
- hız,
- cümle arası durak,
- enerji,
- hafif pitch/prosodi,
- vurgu

parametreleriyle farklı görev profillerine dönüştürülür.

Aşırı pitch değişimi, çocuk sesi taklidi veya yapay “çizgi film sesi” kullanılmaz.

## 5. Fonem Kalite Kapısı

Harf/ses öğretimi için:
- a, e, i, ı, o, ö, u, ü ve ünsüz hedefleri ayrı kalite kontrol edilir,
- harfin adı ile ses değeri karıştırılmaz,
- kısa tek-heceli üretimler otomatik kabul edilmez,
- Türkçe ana dili olan yetişkin dinleme kontrolünden geçmeden asset olarak kilitlenmez.

Gerekirse fonem/harf klipleri TTS yerine özel tek tek kayıt/manuel düzenleme ile hazırlanır.

## 6. Ses Dosyası Üretimi

Önerilen pipeline:

```
content JSON
→ audio text manifest
→ build-time TTS
→ trim/silence cleanup
→ loudness normalize
→ phoneme/word QA
→ OGG encode
→ APK assets
```

Konuşma varlıkları:
- mono,
- konuşma için 24 kHz veya kaynak kalitesine uygun örnekleme,
- OGG/Vorbis,
- kısa klipler,
- dosya adı içerik ID'sine bağlı.

## 7. Runtime Offline Kuralı

Uygulama:
- Android TTS engine çağırmaz,
- cihaz üreticisinin ses paketine bağımlı olmaz,
- internet istemez,
- ses API anahtarı içermez,
- ses dosyasını bulamazsa teknik TTS fallback kullanmaz.

Eksik ses, content validation aşamasında build hatası kabul edilir.

## 8. Konuşma Hızı Ayarı

Ebeveyn ayarında:
- Yavaş 0.80x
- Normal 0.90x
- Hızlı 1.00x

Varsayılan: **0.90x**.

Playback rate değişimi pitch'i bozmayacak Android audio işleme yöntemiyle uygulanır.

Fonem öğretiminde TEACHER_PHONICS profilinin kendi temposu önceliklidir; ek hızlandırma sınırlandırılabilir.

## 9. Özgün Animasyon / UI Ses Profilleri

Sistem/telefon efektleri kullanılmaz. V1 ses efektleri proje için **prosedürel olarak üretilecek özgün kısa seslerdir** ve dış ses paketi lisansı gerektirmez.

### SFX_SOFT_POP
Kullanım:
- nesne ekrana geldi,
- kart açıldı.

Ses karakteri:
- çok kısa yumuşak “pop”,
- düşük tiz yoğunluğu.

### SFX_GENTLE_TAP
Kullanım:
- seçim,
- sürükle-bırak snap.

Karakter:
- yumuşak tahta/marimba dokunuşu.

### SFX_SUCCESS_CHIME
Kullanım:
- doğru cevap.

Karakter:
- 2–3 notalı sıcak bell/marimba,
- kısa,
- zafer fanfarı değil.

### SFX_STAR_SPARKLE
Kullanım:
- yıldız/çıkartma ödülü.

Karakter:
- hafif parlak,
- çok kısa.

### SFX_SLIDE_SOFT
Kullanım:
- kayarak giren harf/nesne.

Karakter:
- düşük seviyeli hava/whisper hareketi.

### SFX_RETRY_SOFT
Kullanım:
- yanlış seçim.

Karakter:
- tek kısa yumuşak tahta tonu,
- alarm veya hata bip'i değil.

### SFX_COMPLETE
Kullanım:
- ders tamamlandı.

Karakter:
- 3–4 notalık yumuşak kapanış.

## 10. Ses Efekti Üretim Kuralları

SFX:
- sinüs/triangle tabanlı ton,
- yumuşak envelope,
- marimba/bell benzeri kısa harmonikler,
- sert transient yok,
- yüksek frekanslı alarm yok,
- 1.5 saniyeyi aşan efekt minimumda.

Konuşma sırasında SFX seviyesi otomatik düşürülür.

## 11. Karıştırma Önceliği

Öncelik:
1. öğretmen sesi
2. gerekli öğrenme SFX'i
3. avatar hareket sesi
4. arka plan müziği

Konuşma başladığında:
- arka plan müziği duck edilir,
- gereksiz SFX susturulur.

## 12. Arka Plan Müziği

Varsayılan:
- çok düşük seviyede veya kapalı.

Harf/fonem ve sayma etkinliklerinde:
- konuşma netliği için müzik kapatılabilir.

Müzik hiçbir zaman görevi bitirmek için gerekli değildir.

## 13. Çocuk Güvenliği

Kullanılmaz:
- siren,
- alarm,
- çığlık,
- ani yüksek patlama,
- korku sesi,
- kaybetme/başarısızlık jingle'ı,
- slot/coin/casino benzeri ödül sesleri,
- uzun tekrarlayan dikkat yakalayıcı loop.

## 14. Kaynak / Lisans Kapısı

Her release öncesinde:
- TTS model lisansı,
- model veri kaynağı,
- attribution gereksinimi,
- dağıtım koşulları

yeniden kontrol edilir.

Lisans belirsizleşirse ilgili ses seti release'e girmez.

---

**LOCKED — Audio Style Guide V1.0**
