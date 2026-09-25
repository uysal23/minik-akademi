# Minik Akademi

Çocuklar için Türkçe okuma-yazma, çizgi çalışmaları ve matematik öğrenme uygulaması.

## Proje Durumu

- Build süreci: **LOCKED V1.0**
- Build Adımı 1 — GitHub klasör/dosya mimarisi: **LOCKED V1.0**
- Build Adımı 2 — Ürün senaryosu ve UI akışı: **LOCKED V1.0**
- Build Adımı 3 — Müfredat ve kaynak eşleme: **LOCKED V1.0**
- Build Adımı 4 — Avatar / görsel / animasyon / ses / ebeveyn / admin / tema / safety: **LOCKED V1.0**
- Build Adımı 5 — İçerik şemaları ve doğrulama: **LOCKED V1.0**
- Build Adımı 6 — Android uygulama iskeleti ve navigasyon: **LOCKED V1.0**
- Build Adımı 7 — Çizgi / ön-yazı çalışmaları: **LOCKED V1.0**\n- Build Adımı 8 — Türkçe okuma-yazma modülü: **NOT STARTED**

> Bu repository kilitli build sırasına göre geliştirilecektir. Bir aşama tamamlanmadan sonraki aşamaya geçilmez.

## Kilitli Dokümanlar

- [BUILD_PROCESS.md](BUILD_PROCESS.md)
- [docs/PROJECT_STRUCTURE_V1.md](docs/PROJECT_STRUCTURE_V1.md)
- [docs/PRODUCT_SPEC_V1.md](docs/PRODUCT_SPEC_V1.md)
- [docs/UI_FLOW_V1.md](docs/UI_FLOW_V1.md)
- [docs/CURRICULUM_LOCK_V1.md](docs/CURRICULUM_LOCK_V1.md)
- [docs/SOURCE_MAPPING.md](docs/SOURCE_MAPPING.md)
- [docs/AVATAR_SYSTEM_V1.md](docs/AVATAR_SYSTEM_V1.md)
- [docs/VISUAL_STYLE_GUIDE.md](docs/VISUAL_STYLE_GUIDE.md)
- [docs/ANIMATION_RULES.md](docs/ANIMATION_RULES.md)
- [docs/AUDIO_STYLE_GUIDE.md](docs/AUDIO_STYLE_GUIDE.md)
- [docs/PARENT_SYSTEM_V1.md](docs/PARENT_SYSTEM_V1.md)
- [docs/ADMIN_PANEL_V1.md](docs/ADMIN_PANEL_V1.md)
- [docs/THEME_ACCESSIBILITY_V1.md](docs/THEME_ACCESSIBILITY_V1.md)
- [docs/CHILD_SAFETY_RULES.md](docs/CHILD_SAFETY_RULES.md)
- [docs/CONTENT_SCHEMA_V1.md](docs/CONTENT_SCHEMA_V1.md)
- [app/README.md](app/README.md) — Android App Shell V1.0

## Makine Tarafından Doğrulanan İçerik

- [content/activity_schema.json](content/activity_schema.json)
- [content/curriculum_manifest.json](content/curriculum_manifest.json)
- [tools/content-validator/validate_content.py](tools/content-validator/validate_content.py)

Authoring validation:

```bash
python -m pip install -r tools/content-validator/requirements.txt
python tools/content-validator/validate_content.py --mode authoring
```

Release validation:

```bash
python tools/content-validator/validate_content.py --mode release
```

Android uygulama iskeleti ve çizgi/ön-yazı motoru derlenebilir durumdadır. Sıradaki aşama Türkçe okuma-yazma modülüdür.
