# Minik Akademi — Android App Shell V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 6

Bu klasör Minik Akademi Android uygulamasının çalışan giriş iskeletini içerir.

## Teknoloji

- Kotlin
- Jetpack Compose
- Android Gradle Plugin 9.4.1
- Gradle 9.6 (CI)
- Java 17
- minSdk 26
- compileSdk 37
- targetSdk 37

## Modüller

### app
- MainActivity
- ana navigation graph
- uygulama composition root

### core
- model
- navigation
- design-system
- datastore

### feature
- splash
- onboarding
- child-profile
- avatar-selection
- child-home
- learning-path
- parent-gate
- parent-dashboard
- settings

## Kilitli Çalışan Akış

```text
Splash
→ Hoş Geldiniz
→ Yetişkin Kurulum Kapısı
→ Çocuk Profili
→ Eğitim Seviyesi
→ Avatar Seçimi
→ Konuşma Hızı
→ Tema
→ Ebeveyn PIN
→ Kurulum Özeti
→ Çocuk Dashboard
```

Çocuk dashboard:

```text
DEVAM ET
├─ Çiziyorum
├─ Harfleri Öğreniyorum
├─ Matematik Öğreniyorum
└─ Oyun Zamanı
```

Ebeveyn erişimi:

```text
Dashboard
→ ebeveyn ikonuna yaklaşık 3 sn basılı tut
→ PIN
→ Ebeveyn Dashboard
→ Ayarlar
→ Çocuk Moduna Dön
```

## Yerel Durum

Preferences DataStore ile cihazda saklanır:

- kurulum tamamlandı mı,
- çocuk adı,
- seviye,
- avatar id,
- tema,
- konuşma hızı,
- anlatıcı/SFX/müzik tercihleri,
- azaltılmış hareket,
- büyük UI,
- yüksek kontrast,
- solak mod,
- günlük hedef,
- ebeveyn PIN hash'i.

PIN, ebeveyn alanına yanlışlıkla erişimi önleyen yerel bir ürün kapısıdır; yüksek güvenlikli kimlik doğrulama mekanizması değildir.

## Offline Kuralı

V1 shell:
- INTERNET izni istemez,
- runtime TTS kullanmaz,
- cloud servis çağrısı yapmaz,
- cleartext trafiğe izin vermez.

Gerçek kadın öğretmen sesleri ve SFX dosyaları Build Adımı 11'de offline asset olarak bağlanacaktır.

## Geçici Shell Öğeleri

Build Adımı 6'nın kapsamı gereği:
- gerçek avatar çizimleri henüz yoktur; avatar placeholder kullanılır,
- gerçek ders düğümleri henüz bağlanmamıştır,
- ses örneği butonu gerçek audio asset gelene kadar pasiftir,
- ilerleme metrikleri ders modülleri bağlandıktan sonra gerçek veri kullanacaktır.

Bunlar eksik Build Adımı 6 davranışı değil; sonraki kilitli build aşamalarının kapsamıdır.

## Build Doğrulaması

GitHub Actions Android Build:
- Java 17: PASS
- Android SDK 37: PASS
- Gradle configuration: PASS
- `:app:assembleDebug`: **PASS**

Doğrulama workflow run: **36167430659**

---

**LOCKED — Android App Shell V1.0**
