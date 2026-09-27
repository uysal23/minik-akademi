# Minik Akademi — Spoken Text QA V4

**Status:** REVIEWED FOR V4 STAGING  
**Scope:** Uygulamadaki seslendirilecek 159 konuşma metni

## Sonuç

Ses üretiminden önce 159 metin tarandı. Müfredat sırası, activity ID'leri, öğrenme hedefleri ve kilitli davranışlar değiştirilmedi.

20 yönerge dilbilgisi, açıklık veya TTS güvenliği açısından düzeltildi:
- 9 harf tanıtım cümlesinde `A - a` benzeri tireli ifade kaldırıldı ve daha doğal iki cümleli anlatım kullanıldı.
- Konum, ölçme, sayı/rakam, onluk-birlik, eşleme, kütle ve çokluk yönergelerinde 10 anlatım düzeltmesi yapıldı.
- “5 fark” yönergesi doğal Türkçeye çevrildi.

## TTS için ayrı spokenText kuralı

Ekrandaki pedagojik sayı gösterimleri korunur. Ses üretiminde rakamlar doğal Türkçe okunuşa çevrilir.

Örnekler:
- `20'den` → `yirmiden`
- `3 grupta 2'şer` → `üç grupta ikişer`
- `19 sayısı` → `on dokuz sayısı`
- `1 onluk ve 5 birlik` → `bir onluk ve beş birlik`

Bu ayrım sayesinde ekrandaki sayı öğretimi bozulmaz, fakat TTS rakam/apostrof işaretini yanlış okumaz.

## Seçilen ses

V4 için kullanıcı tarafından **C seçeneği** onaylandı:
- Chatterbox Multilingual V3
- Turkish
- seed: 618034
- exaggeration: 0.25
- cfg weight: 0.20
- temperature: 0.60
- ücretli API: yok
- API anahtarı: yok

## Fonem kalite kapısı

`a, n, e, t, i, l, o, k, u` tek fonem dosyaları tam üretimde staging'e alınır ancak otomatik final kabul edilmez. Harf adı yerine gerçek ses değerinin duyulduğu ayrıca kontrol edilir.

Mevcut canlı V1 konuşma assetleri V4 staging tamamlanıp kalite kontrolü geçmeden değiştirilmez.
