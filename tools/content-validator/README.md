# Content Validator

Build Adımı 5'in makine doğrulama aracıdır.

## Kurulum

```bash
python -m pip install -r tools/content-validator/requirements.txt
```

## Authoring kontrolü

```bash
python tools/content-validator/validate_content.py --mode authoring
```

Kontrol eder:
- JSON Schema,
- benzersiz activity/curriculum ID,
- curriculum prerequisite varlığı ve döngüleri,
- SOURCE/ADAPTED/EXTENSION uyumu,
- kaynak kodu ve sayfa aralığı,
- harf unlock sınırı,
- matematik sayı aralığı,
- offline/remote alan yasağı,
- temel safety alanları.

Henüz üretilmemiş audio/visual assetleri authoring modunda hata değildir.

## Release kontrolü

```bash
python tools/content-validator/validate_content.py --mode release
```

Ek olarak:
- safety.reviewed = true,
- bütün audioId dosyaları,
- bütün visualId/backgroundId dosyaları

zorunludur.

Offline asset referanslarında JSON kimliği ile dosya adının **stem** kısmı aynı olmalıdır.

Örnek:

```text
audio id: aud_common_success_01
file: audio/praise/aud_common_success_01.ogg
```

## Release Blocker

Validator exit code 1 verirse release APK üretilemez.
