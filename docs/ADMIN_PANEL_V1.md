# Minik Akademi — Admin Panel V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 4

## Amaç

Normal kullanıcıdan gizli geliştirici/yönetim alanıdır.

## Erişim

- release çocuk akışında görünmez,
- debug veya özel yönetici kapısı ile açılır,
- ebeveyn PIN'inden ayrı tutulur.

## Ana Kartlar

- Content Packs
- Curriculum Validation
- Source Mapping
- Missing Images
- Missing Audio
- Audio QA
- JSON Validation
- App Version
- Content Version
- Database Version
- Regression Status

## Audio QA

Her konuşma asset'i için:
- contentId,
- voiceProfile,
- text,
- dosya var mı,
- süre,
- loudness kontrolü,
- phoneme QA sonucu,
- lisans/attribution kaynağı

görülebilir.

Eksik çekirdek ses:
**release blocker**.

## Avatar QA

- tüm avatarlar gerekli pozlara sahip mi,
- pose eksikleri,
- asset boyutları,
- kimlik tutarlılığı.

## İçerik QA

SOURCE / ADAPTED / EXTENSION sınıfı eksik olan etkinlik release'e alınmaz.

## Çocuk Alanı Sınırı

Admin paneli çocuk navigasyonundan açılamaz.

---

**LOCKED — Admin Panel V1.0**
