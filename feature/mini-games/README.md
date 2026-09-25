# Minik Akademi — Mini Oyunlar V1

**Status:** VERIFYING  
**Version:** 1.0  
**Build Step:** 10

Build Adımı 10, kilitli UI akışındaki **Oyun Zamanı** bölümünü uygular.

## Oyunlar

V1'de beş pedagojik mini oyun bulunur:

1. **Harf Avı**
   - öğrenilmiş harfleri kısa tekrar oyununa dönüştürür,
   - hedef harfleri buldurur,
   - yanlış seçimde ceza vermez.

2. **Say ve Seç**
   - nesne sayma ve doğru niceliği seçme çalışmasıdır,
   - matematik sayma içeriğinden türetilmiştir.

3. **Eşini Bul**
   - eş nesne / eş şekil kavramını tekrar eder,
   - iki aynı şekli seçme mantığıyla çalışır.

4. **Yol Bulma**
   - çizgi/yön takibi çalışmalarından türetilmiştir,
   - doğru yolu seçerek avatarı hedefe ulaştırır.

5. **Gruplama**
   - 10 nesneyi bir araya getirerek bir onluk oluşturmayı tekrar eder,
   - onluk/birlik içeriğine bağlıdır.

## Açılma Kuralı

Mini oyunlar müfredattan bağımsız başlangıç içeriği değildir.

- Harf Avı: Türkçe çalışması başladıktan sonra açılır.
- Yol Bulma: çizgi/ön-yazı çalışması başladıktan sonra açılır.
- Eşini Bul: ilgili matematik eşlik çalışmasından sonra açılır.
- Say ve Seç: ilgili sayma çalışmasından sonra açılır.
- Gruplama: onluk çalışmasından sonra açılır.

Kilitli metin:

**Biraz daha çalışınca açılacak.**

## Güvenlik / Ödül Kuralı

Mini oyunlarda:

- geri sayım baskısı yoktur,
- can sistemi yoktur,
- kaybetme/ceza yoktur,
- para/coin yoktur,
- loot box veya rastgele ödül yoktur,
- liderlik tablosu yoktur,
- çevrimiçi rekabet yoktur.

Tamamlanan oyun yeniden oynanabilir.

## Avatar

Seçili çocuk avatarı oyun listesinde ve oyun ekranında aynı `avatarId` ile korunur.

## Offline

Mini oyun JSON'ları APK içindeki:

`content/mini_games/`

klasöründen okunur.

İlerleme cihaz içinde Preferences DataStore'da `completedMiniGames` alanında saklanır.

## Ses ve Görsel Assetleri

Build Adımı 11'de:

- özgün çocuk dostu oyun görselleri,
- final avatar pozları,
- kadın öğretmen yönerge sesleri,
- yumuşak SFX

mevcut mini-game ID ve audio ID'lerine bağlanacaktır.

---

Content ve Android doğrulamaları tamamlanınca durum **LOCKED V1.0** yapılacaktır.
