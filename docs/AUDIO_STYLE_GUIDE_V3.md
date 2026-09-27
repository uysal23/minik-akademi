# Minik Akademi — Audio Style Guide V3 Gentle Teacher

**Status:** IN REVIEW  
**Version:** 3.0  
**Scope:** Post-V2 voice identity replacement

## 1. Neden V3

V2 ve V2.1 FreyaTTS-small canonical Leyla tabanlı örnekler kullanıcı dinleme testinde reddedildi.

Red gerekçeleri:
- fazla sert / çırtlak tını,
- çocuk için ürkütücü veya itici algı,
- doğallıktan uzak okuma,
- şefkat ve zarafet eksikliği.

Bu nedenle yalnız EQ/post-processing ile aynı konuşmacı kimliğini düzeltmeye devam edilmez.

## 2. V3 Ana Hedef

Öğretmen sesi:
- çok yumuşak,
- şefkatli,
- nazik,
- sakin,
- zarif yetişkin kadın,
- çocuğun yanında konuşuyormuş gibi yakın,
- güven veren,
- emir vermeyen,
- sabırlı,
- doğal nefes ve cümle sonlarına sahip

olmalıdır.

Ses çocuk sesi taklidi yapmaz. Aşırı neşeli, reklam sesi, sunucu sesi, mekanik öğretmen tonu veya dramatik oyunculuk kullanılmaz.

## 3. Konuşma Tavrı

Yönergeler komut verir gibi değil, birlikte yapıyormuş hissiyle okunur.

Tercih:
- "Birlikte bakalım."
- "Hazır olduğunda başlayabilirsin."
- "İstersen bir kez daha deneyelim."
- "Acele etmene gerek yok."

Kaçınılır:
- keskin emir tonu,
- hızlı direktif,
- sert cümle bitişi,
- yüksek enerji ile övgü,
- baskı yaratan vurgu.

## 4. Voice Casting Kuralı

Canonical Leyla sesi V3 için kullanılmaz.

FreyaTTS-small'ın seed ile farklı konuşmacı kimlikleri üretme özelliği kullanılarak birden fazla yeni ses adayı oluşturulur.

İlk casting:
- 12 ayrı deterministik speaker seed,
- 2 şefkatli test cümlesi,
- toplam 24 örnek,
- aynı ücretsiz Apache-2.0 model,
- ücretli API yok,
- API anahtarı yok.

Bir ses kimliği seçilmeden tam 159+ asset üretimine geçilmez.

## 5. İşleme

Post-processing yalnız destek amaçlıdır; kötü bir ses kimliğini maskelemek için kullanılmaz.

Hedef:
- doğal formantları koru,
- yapay pitch shift kullanma,
- üst frekans sertliğini yumuşat,
- düşük-orta bölgede çok hafif sıcaklık,
- sakin tempo,
- yaklaşık -20 LUFS,
- true peak <= -3 dB.

## 6. Kalite Kapısı

Aday ses aşağıdaki ölçütlerin tamamını geçmelidir:
- çocuk için güvenli/yumuşak algı,
- kadınsı zarafet,
- şefkatli öğretmen tavrı,
- doğal Türkçe,
- uzun dinlemede yorucu olmama,
- fonem ve sayı netliği,
- sentetik/robotik hissin minimum olması.

Kullanıcının açık dinleme onayı olmadan mevcut V1 seslerinin üzerine yazılmaz.

---

**IN REVIEW — Audio V3 Gentle Teacher**
