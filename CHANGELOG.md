# Changelog

## 2026-09-25 — Content Schema & Validation V1.0

- CONTENT_SCHEMA_V1.md completed and locked.
- Machine-readable JSON Schema added as content/activity_schema.json.
- Locked curriculum manifest added as content/curriculum_manifest.json.
- Manifest includes source-backed, adapted and extension nodes with prerequisite relationships, source/page traceability, unlocked-letter constraints and math number-range constraints.
- Standard activity types, offline audio/visual IDs, teacher voice profiles, animation profiles, feedback rules, avatar use, progression and safety fields locked.
- Python content validator added under tools/content-validator/.
- Validator checks schema, unique IDs, curriculum prerequisite cycles, source/page constraints, locked letter ranges, math number ranges, forbidden remote/runtime keys and safety fields.
- Release mode requires safety review and local audio/visual assets.
- GitHub Actions content-validation workflow added.
- Android application development has not started.

## 2026-09-25 — Experience Systems V1.0

- AVATAR_SYSTEM_V1.md locked with 12+ original human child avatars and a fixed reusable pose set.
- VISUAL_STYLE_GUIDE.md locked with source-inspired but original child-friendly educational cartoon rules.
- ANIMATION_RULES.md locked with slide/pop/fade entry behavior, gentle feedback, counting timing and reduced-motion behavior.
- AUDIO_STYLE_GUIDE.md locked as fully offline: no device TTS, no cloud TTS at runtime, no system notification sounds.
- Female teacher/narrator voice profiles locked: TEACHER_WARM, TEACHER_PHONICS, TEACHER_MATH, TEACHER_STORY, TEACHER_ENCOURAGE.
- Original procedural SFX profiles locked: SOFT_POP, GENTLE_TAP, SUCCESS_CHIME, STAR_SPARKLE, SLIDE_SOFT, RETRY_SOFT, COMPLETE.
- Build-time open/free Turkish TTS strategy documented; Antalia 1 selected as primary candidate subject to release-time license validation and required attribution.
- PARENT_SYSTEM_V1.md, ADMIN_PANEL_V1.md, THEME_ACCESSIBILITY_V1.md and CHILD_SAFETY_RULES.md locked.
- Android application development has not started.

## 2026-09-25 — Curriculum & Source Mapping V1.0

- CURRICULUM_LOCK_V1.md completed and locked from the six uploaded source PDFs.
- SOURCE_MAPPING.md completed and locked with source/page traceability.
- Pre-writing, literacy letter groups, spatial relations, number/numeracy, tens/ones, ordinal numbers, quantity comparison, rhythmic counting, patterns, length and mass topics mapped.
- Addition, subtraction and multiplication explicitly marked as EXTENSION because the uploaded source set does not provide standalone systematic teaching sequences for them.
- Android application development has not started.

## 2026-09-25 — Product & UI V1.0

- PRODUCT_SPEC_V1.md completed and locked.
- UI_FLOW_V1.md completed and locked.
- First-run setup, child profile, avatar selection, dashboard, learning path, activity states, rewards, parent gate, parent dashboard, settings, accessibility and admin boundaries defined.
- Android application development has not started.

## 2026-09-25 — Architecture V1.0

- Repository initialized.
- BUILD_PROCESS.md created and locked.
- GitHub project structure defined and locked.
- No application code has been developed yet.
