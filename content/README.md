# content

Koddan ayrılmış pedagojik içerik deposudur.

## Kilitli Dosyalar

- `activity_schema.json` — Activity JSON Schema V1
- `curriculum_manifest.json` — kilitli curriculum node manifesti

## İçerik Sınıfları

Her activity:
- SOURCE
- ADAPTED
- EXTENSION

değerlerinden birini taşımak zorundadır.

## İçerik Dizinleri

Planlanan:
- `literacy/`
- `mathematics/`
- `mini_games/`

Gerçek etkinlik JSON dosyaları ilgili build modülü geliştirilirken bu klasörlere eklenecektir.

## Doğrulama

```bash
python tools/content-validator/validate_content.py --mode authoring
```

Release öncesi:

```bash
python tools/content-validator/validate_content.py --mode release
```

Çekirdek içerik runtime TTS, URL veya uzak asset kullanamaz.
