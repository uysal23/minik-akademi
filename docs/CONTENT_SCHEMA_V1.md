# Minik Akademi — Content Schema V1

**Status:** LOCKED  
**Version:** 1.0  
**Build Step:** 5

Bu belge, bütün eğitim içeriklerinin Android kodundan bağımsız, doğrulanabilir ve kaynak izlenebilir JSON verisi olarak nasıl tutulacağını tanımlar.

## 1. Ana İlke

Her etkinlik:
- tek bir benzersiz `id` taşır,
- kilitli bir `curriculumId` düğümüne bağlanır,
- `SOURCE / ADAPTED / EXTENSION` sınıfını açıkça belirtir,
- offline görsel/ses kimlikleri kullanır,
- pedagojik hedefi ve etkileşim türünü açıkça tanımlar,
- çocuk güvenliği alanlarını içerir,
- uygulama koduna özel sabit içerik gömülmesini gerektirmez.

## 2. Ana Dosyalar

```text
content/
├── activity_schema.json
├── curriculum_manifest.json
├── literacy/
├── mathematics/
└── mini_games/
```

- `activity_schema.json`: tek bir etkinlik JSON dosyasının makine tarafından doğrulanabilir şemasıdır.
- `curriculum_manifest.json`: kilitli öğrenme yolu düğümlerini, sıralarını, prerequisite ilişkilerini ve kaynak sınırlarını tutar.

## 3. Activity ID Kuralı

Önerilen biçim:

```text
DOMAIN-TOPIC-LEVEL-SEQUENCE
```

Örnekler:
- `LIT-A-SOUND-001`
- `LIT-N-BUILD-003`
- `MAT-DIGIT-4-COUNT-002`
- `PRE-TRACE-PATH-005`
- `EXT-ADD-OBJECTS-001`

ID:
- büyük harf/rakam,
- tire, alt çizgi veya nokta,
- 3–64 karakter

ile sınırlıdır.

## 4. Domain

İzinli değerler:

- `PREWRITING`
- `LITERACY`
- `MATHEMATICS`
- `MINI_GAME`

## 5. Learning Level

- `PRESCHOOL_START`
- `PRESCHOOL_ADVANCED`
- `GRADE_1`

Bu alan müfredat düğümünü değiştirmez; etkinliğin sunulabileceği öğrenme seviyesini belirtir.

## 6. Activity Type

V1 motorunun destekleyeceği veri tipleri:

### Ön Yazı
- `TRACE_PATH`
- `TRACE_SHAPE`
- `TRACE_LETTER`
- `TRACE_NUMBER`
- `FIND_DIFFERENCE`

### Türkçe
- `LETTER_INTRO`
- `FIND_LETTER`
- `FIND_SOUND_OBJECT`
- `FIND_TARGET_SYMBOL`
- `BUILD_SYLLABLE`
- `BUILD_WORD`
- `WORD_IMAGE_MATCH`
- `STORY_LISTEN`
- `STORY_QUESTION`

### Matematik
- `NUMBER_INTRO`
- `COUNT_OBJECTS`
- `NUMBER_OBJECT_MATCH`
- `POSITION_SELECT`
- `COMPARE_QUANTITY`
- `COMPARE_LENGTH`
- `COMPARE_MASS`
- `MEASURE_NONSTANDARD`
- `RHYTHMIC_COUNT`
- `PATTERN_COMPLETE`
- `ORDER_ITEMS`
- `MAZE_TARGET_SYMBOL`

### Ortak Etkileşim
- `TAP_CHOICE`
- `MULTI_SELECT`
- `DRAG_DROP`
- `MATCH_PAIR`
- `BUILD_SEQUENCE`

### EXTENSION
- `ADD_OBJECTS`
- `SUBTRACT_OBJECTS`
- `GROUP_OBJECTS`

## 7. Source Type

### SOURCE

Zorunlu:
- curriculumId,
- sourceRef.sourceCode,
- sourceRef.pageStart,
- sourceRef.pageEnd.

### ADAPTED

SOURCE ile aynı kaynak izi zorunludur. Nesne/görsel/soru özgünleştirilebilir, fakat öğrenme hedefi ve kaynak sınırı korunur.

### EXTENSION

- curriculumId `EXT-` ile başlar.
- sourceRef null olabilir.
- kaynak-temelli içerik gibi gösterilemez.

## 8. Instruction

Her etkinlikte:
- kısa Türkçe yönerge,
- offline `audioId`,
- izinli kadın öğretmen `voiceProfile`

zorunludur.

İzinli profiller:
- TEACHER_WARM
- TEACHER_PHONICS
- TEACHER_MATH
- TEACHER_STORY
- TEACHER_ENCOURAGE

Runtime TTS alanı yoktur.

## 9. Learning Target

Etkinlik hedefi veri içinde açıkça tutulur.

Kullanılabilecek alanlar:
- conceptId,
- targetSymbol,
- allowedLetters,
- numberRange,
- quantity,
- relation.

Örnek:

```json
{
  "conceptId": "MAT-DIGIT-4",
  "numberRange": {"min": 0, "max": 4},
  "quantity": 4
}
```

## 10. Prerequisites

Her etkinlik gerekirse:
- önceki curriculum node,
- önceki activity

bağımlılıklarını tanımlayabilir.

Müfredat sırasını değiştiren gizli prerequisite oluşturulamaz.

## 11. Interaction

Ortak interaction modları:

- TAP
- MULTI_TAP
- DRAG
- TRACE
- MATCH
- ORDER
- LISTEN
- PASSIVE_INTRO

Seçenekli etkinliklerde her seçenek benzersiz option id taşır.

## 12. Feedback

Zorunlu alanlar:
- successAudioId,
- retryAudioId,
- successSfx,
- retrySfx,
- punitive: false.

İzinli SFX:
- SFX_SOFT_POP
- SFX_GENTLE_TAP
- SFX_SUCCESS_CHIME
- SFX_STAR_SPARKLE
- SFX_SLIDE_SOFT
- SFX_RETRY_SOFT
- SFX_COMPLETE

Puan düşürme veya cezalandırma alanı bulunmaz.

## 13. Animation

Giriş:
- SLIDE_LEFT
- SLIDE_RIGHT
- SLIDE_UP
- SOFT_DROP
- POP_IN
- FADE_IN
- NONE

Doğru:
- BOUNCE_ONCE
- SOFT_GLOW
- SMALL_STARS
- NONE

Yanlış:
- SOFT_SHAKE
- HIGHLIGHT_HINT
- NONE

Reduced motion fallback zorunludur ve genellikle `FADE_IN` veya `NONE` olur.

## 14. Avatar Kullanımı

`assets.avatarUse`:
- enabled,
- role,
- pose

alanlarını taşır.

Role:
- GUIDE
- PARTICIPANT
- TARGET
- NONE

Pose, AVATAR_SYSTEM_V1 içindeki kilitli poz listesinden seçilir.

## 15. Offline Asset Kuralı

İçerik JSON'larında:
- URL,
- remote URI,
- cloud TTS provider,
- streaming audio

bulunamaz.

Görsel/sesler kimlik ile referanslanır:
- `audioId`
- `visualId`
- `backgroundId`.

Build/release doğrulamasında eksik asset release blocker olacaktır.

## 16. Safety

Her etkinlik:
- `reviewed`
- `prohibitedContentPresent: false`
- `externalUrl: false`
- `ads: false`
- `purchase: false`

alanlarını taşır.

Otomatik validation insan pedagojik/güvenlik incelemesinin yerine geçmez.

## 17. Progression

Etkinlik:
- completionRule,
- repeatEligible,
- unlocks

alanlarını taşıyabilir.

Completion rule:
- COMPLETE_ON_SUCCESS
- COMPLETE_ON_ATTEMPT
- COMPLETE_ON_LISTEN

Çocuk yanlış cevap verdiği için kalıcı olarak bloke edilmez; tekrar/destek akışı kullanılabilir.

## 18. Curriculum Manifest

Manifest her node için:
- id,
- domain,
- title,
- sourceType,
- sourceCode/pageRange,
- order,
- prerequisites,
- constraints,
- unlockedLetters

gibi alanları tutabilir.

Manifest, CURRICULUM_LOCK_V1 ve SOURCE_MAPPING ile çelişemez.

## 19. Validator Kapıları

### STRUCTURE

- JSON parse ediliyor mu?
- activity schema uyuyor mu?
- id benzersiz mi?
- curriculumId manifestte var mı?
- prerequisite düğümleri mevcut mu?
- döngü var mı?

### SOURCE

- SOURCE/ADAPTED sourceRef var mı?
- sourceCode manifest ile uyumlu mu?
- pageStart <= pageEnd mi?
- sayfa aralığı kaynak mapping sınırını aşıyor mu?
- EXTENSION yanlışlıkla SOURCE gibi işaretlenmiş mi?

### PEDAGOGY

- harf etkinliğinde allowedLetters sırası aşılmış mı?
- SOURCE/ADAPTED matematik numberRange kilitli sınırı aşıyor mu?
- bir etkinlik gerekli prerequisite'i atlıyor mu?

### OFFLINE

- URL/remote URI alanı var mı?
- runtime TTS/provider alanı var mı?
- release modunda audio/visual referansları mevcut mu?

### SAFETY

- punitive true olamaz,
- externalUrl/ads/purchase false olmak zorunda,
- prohibitedContentPresent false olmak zorunda,
- insan review işareti release öncesi true olmak zorunda.

## 20. Validation Seviyeleri

### authoring

İçerik geliştirilirken:
- şema,
- ID,
- curriculum,
- source,
- temel safety

kontrol edilir.

Eksik henüz üretilmemiş asset warning olabilir.

### release

APK release öncesinde:
- bütün authoring kontrolleri,
- bütün audio assetleri,
- bütün visual assetleri,
- safety review,
- phoneme/audio QA

zorunlu geçer.

## 21. Örnek Activity

```json
{
  "schemaVersion": "1.0",
  "id": "MAT-DIGIT-4-COUNT-001",
  "domain": "MATHEMATICS",
  "curriculumId": "MAT-DIGIT-4",
  "sourceType": "ADAPTED",
  "sourceRef": {
    "sourceCode": "MA-02",
    "pageStart": 27,
    "pageEnd": 28
  },
  "title": "Dört Balonu Bul",
  "learningLevel": "GRADE_1",
  "activityType": "COUNT_OBJECTS",
  "instruction": {
    "text": "Dört balonu bul.",
    "audioId": "aud_mat_digit4_count_001_instruction",
    "voiceProfile": "TEACHER_MATH",
    "replayAllowed": true
  },
  "learningTarget": {
    "conceptId": "MAT-DIGIT-4",
    "numberRange": {"min": 0, "max": 4},
    "quantity": 4
  },
  "prerequisites": ["MAT-DIGIT-3"],
  "interaction": {
    "mode": "TAP",
    "retryStrategy": "HIGHLIGHT_HINT"
  },
  "assets": {
    "visualIds": ["balloon_blue", "balloon_yellow", "balloon_green", "balloon_pink"],
    "avatarUse": {
      "enabled": true,
      "role": "GUIDE",
      "pose": "point_right"
    }
  },
  "audio": {
    "extraAudioIds": []
  },
  "animation": {
    "entry": "SLIDE_RIGHT",
    "success": "BOUNCE_ONCE",
    "retry": "SOFT_SHAKE",
    "reducedMotionFallback": "FADE_IN"
  },
  "feedback": {
    "successAudioId": "aud_common_success_01",
    "retryAudioId": "aud_common_retry_01",
    "successSfx": "SFX_SUCCESS_CHIME",
    "retrySfx": "SFX_RETRY_SOFT",
    "punitive": false
  },
  "safety": {
    "reviewed": false,
    "prohibitedContentPresent": false,
    "externalUrl": false,
    "ads": false,
    "purchase": false
  },
  "progression": {
    "completionRule": "COMPLETE_ON_SUCCESS",
    "repeatEligible": true,
    "unlocks": []
  },
  "metadata": {
    "locale": "tr-TR",
    "authoringVersion": 1
  }
}
```

---

**LOCKED — Content Schema V1.0**
