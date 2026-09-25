# Minik Akademi — Project Structure V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 1

Bu dosya repository klasör/dosya mimarisini tanımlar. Uygulama kodu bu yapıya bağlı kalacaktır.

## Kök Yapı

```text
minik-akademi/
├── BUILD_PROCESS.md
├── README.md
├── CHANGELOG.md
├── docs/
├── app/
├── core/
├── feature/
├── content/
├── assets/
├── audio/
├── tools/
├── tests/
└── .github/
```

## Planlanan Nihai Yapı

```text
minik-akademi/
│
├── BUILD_PROCESS.md
├── README.md
├── CHANGELOG.md
├── LICENSE
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradle/
│   └── libs.versions.toml
│
├── .github/
│   └── workflows/
│       ├── android-build.yml
│       ├── unit-tests.yml
│       └── content-validation.yml
│
├── docs/
│   ├── PROJECT_STRUCTURE_V1.md
│   ├── LOCK_INDEX.md
│   ├── PRODUCT_SPEC_V1.md
│   ├── UI_FLOW_V1.md
│   ├── CURRICULUM_LOCK_V1.md
│   ├── SOURCE_MAPPING.md
│   ├── AVATAR_SYSTEM_V1.md
│   ├── VISUAL_STYLE_GUIDE.md
│   ├── ANIMATION_RULES.md
│   ├── AUDIO_STYLE_GUIDE.md
│   ├── PARENT_SYSTEM_V1.md
│   ├── ADMIN_PANEL_V1.md
│   ├── THEME_ACCESSIBILITY_V1.md
│   ├── CHILD_SAFETY_RULES.md
│   └── CONTENT_SCHEMA_V1.md
│
├── app/
│   └── src/
│       ├── main/
│       ├── test/
│       └── androidTest/
│
├── core/
│   ├── model/
│   ├── database/
│   ├── datastore/
│   ├── navigation/
│   ├── design-system/
│   ├── audio/
│   ├── animation/
│   ├── content-engine/
│   ├── progress/
│   ├── avatar/
│   └── safety/
│
├── feature/
│   ├── splash/
│   ├── onboarding/
│   ├── child-profile/
│   ├── avatar-selection/
│   ├── child-home/
│   ├── learning-path/
│   ├── tracing/
│   ├── literacy/
│   ├── mathematics/
│   ├── mini-games/
│   ├── rewards/
│   ├── parent-gate/
│   ├── parent-dashboard/
│   ├── settings/
│   └── admin/
│
├── content/
│   ├── curriculum_manifest.json
│   ├── activity_schema.json
│   ├── literacy/
│   │   ├── preparation/
│   │   ├── group_01_anet/
│   │   ├── group_02_iloku/
│   │   ├── syllables/
│   │   ├── words/
│   │   └── sentences/
│   ├── mathematics/
│   │   ├── spatial/
│   │   ├── objects/
│   │   ├── numbers/
│   │   ├── counting/
│   │   ├── compare/
│   │   ├── geometry/
│   │   ├── measurement/
│   │   └── extensions/
│   │       ├── addition/
│   │       ├── subtraction/
│   │       └── multiplication/
│   └── mini_games/
│
├── assets/
│   ├── avatars/
│   ├── characters/
│   ├── animals/
│   ├── food/
│   ├── fruits/
│   ├── school/
│   ├── toys/
│   ├── nature/
│   ├── transport/
│   ├── household/
│   ├── letters/
│   ├── numbers/
│   ├── shapes/
│   ├── backgrounds/
│   ├── effects/
│   └── rewards/
│
├── audio/
│   ├── instructions/
│   ├── letters/
│   ├── phonemes/
│   ├── numbers/
│   ├── words/
│   ├── praise/
│   ├── corrections/
│   └── effects/
│
├── tools/
│   ├── content-validator/
│   ├── asset-validator/
│   └── source-mapper/
│
└── tests/
    ├── curriculum/
    ├── content/
    ├── ui/
    ├── accessibility/
    ├── safety/
    └── regression/
```

## Modül Sorumlulukları

### app/
Android uygulama giriş noktasıdır. Activity, uygulama composition root'u ve platform entegrasyonları burada bulunur.

### core/
Birden fazla feature tarafından kullanılan ortak altyapıdır. Feature'a özel UI burada bulunmaz.

### feature/
Her kullanıcı deneyimi bağımsız feature modülü olarak tutulur. Avatar seçimi, çocuk dashboard'u, Türkçe ve Matematik birbirine doğrudan bağlanmak yerine core sözleşmelerini kullanır.

### content/
Pedagojik içerik koddan ayrıdır. Etkinlikler veri tabanlı olacaktır. SOURCE / ADAPTED / EXTENSION sınıflandırması ve kaynak izi burada tutulur.

### assets/
Görsel varlıkların kaynak deposudur. Çocuk tarafından seçilen insan avatarlarının tüm pozları ve varyasyonları assets/avatars altında tutulur.

### audio/
Çekirdek fonem, harf, sayı, kelime, yönerge ve geri bildirim sesleri burada tutulur.

### docs/
Kilitli ürün, müfredat, UI, güvenlik ve tasarım sözleşmeleridir.

### tools/
İçerik, görsel ve kaynak eşleme doğrulayıcılarının alanıdır.

### tests/
Müfredat, içerik, UI, erişilebilirlik, güvenlik ve regresyon kontrolleri ayrılır.

## Mimari Kurallar

1. Çocuk-facing içerik ile ebeveyn/admin alanları ayrı feature'larda tutulur.
2. Müfredat kod içine gömülmez.
3. Görsel ve ses dosyaları kaynak kimliği ile eşlenebilir olmalıdır.
4. Avatar çocuk profilinin bir parçasıdır ve tüm çocuk-facing feature'lar aynı avatar kimliğini kullanır.
5. EXTENSION içerikler kaynak-temelli içerikle aynı klasörde karıştırılmaz.
6. İçerik motoru herhangi bir tek nesneye bağlı tasarlanmaz; nesne havuzu veri ile değiştirilebilir.
7. Child-safety kuralları UI'dan bağımsız bir proje kısıtı olarak uygulanır.
8. Kilitli docs dosyaları uygulamanın davranış sözleşmesidir.
9. Build Step 6'ya kadar Android uygulama kodu yazılmaz.
10. Yapısal değişiklik proje sahibinin açık onayını ve sürüm artırımı gerektirir.

## Build Adımı 1 Sonucu

Bu yapı **LOCKED V1.0** olarak kabul edilir.

Henüz Kotlin/Compose uygulama kodu, Gradle modülleri, içerik JSON'ları, ses veya görsel varlıklar oluşturulmamıştır. Bunlar ilgili build aşamalarında eklenecektir.

---

**LOCKED — Project Structure V1.0**
