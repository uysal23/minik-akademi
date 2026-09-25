# tools

İçerik, asset ve source-mapping doğrulama araçları için ayrılmış alandır.

## content-validator

Build Adımı 5'te oluşturuldu.

```bash
python -m pip install -r tools/content-validator/requirements.txt
python tools/content-validator/validate_content.py --mode authoring
```

Release APK öncesi:

```bash
python tools/content-validator/validate_content.py --mode release
```

Validator başarısızsa release build durdurulmalıdır.

## asset-validator

Görsel üretim/asset entegrasyon aşamasında geliştirilecektir.

## source-mapper

İçerik çoğaltma aşamasında SOURCE / ADAPTED / EXTENSION izlenebilirliğini destekleyecektir.
