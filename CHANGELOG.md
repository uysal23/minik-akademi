# Changelog

## 2026-09-26 — Manual Pedagogical / UI Review V1.0

- Build Step 13 completed and locked.
- GitHub-hosted Android emulator review flow completed without requiring local user setup.
- Final Pedagogical UI Review #25 (run 36242603661) completed successfully on commit `9935f261079c586c513de204086c7b69841b012f`.
- 17/17 required PNG evidence screens were generated and visually reviewed.
- Evidence artifact `minik-akademi-step13-ui-review-evidence` (artifact ID 10905809486) was verified.
- Companion instrumentation reports artifact ID is 10906447727.
- Android Build #123 and Automated Tests #23 both passed on the same commit.
- Manual review confirmed child-safe language, large/reachable controls, offline UI behavior, avatar continuity, visible listening/navigation controls, locked-content wording, parent/child separation, and explicit EXTENSION mathematics separation.
- A stale-frame evidence defect found after technically successful Run #24 was not accepted; screenshot capture was strengthened to require a new settled physical frame, then Run #25 was rerun and re-reviewed successfully.
- Build Step 14 (regression tests) is now the next gated stage.

## 2026-09-26 — Automated Tests V1.0

- Build Step 12 completed and locked.
- GitHub-only automated test workflow added under `.github/workflows/automated-tests.yml`; no local PC/Codex execution is required.
- Added 20 Python automated tests covering locked content counts, curriculum mapping, child-safety flags, EXTENSION separation, Android offline permission rules, runtime TTS/network bans, navigation wiring, DataStore progress persistence, PIN hashing, and audio/visual hooks.
- Added content-validator contract tests for valid content, unknown curriculum IDs, punitive feedback, external URLs, locked number/letter violations, release safety review, missing offline audio/visual assets, duplicate IDs, and malformed JSON.
- Added 6 Kotlin/JUnit tests: 4 core model tests and 2 navigation tests.
- Existing Content Validation, Audio Validation and Visual Validation are rerun inside the Step 12 automated gate.
- Automated Tests run 36224514555 completed successfully.
- Python tests: 20/20 PASS.
- Kotlin/JUnit tests: PASS.
- Gradle configuration and `:app:assembleDebug`: PASS.
- JUnit reports uploaded as `automated-test-reports`.
- Tested debug APK uploaded as `minik-akademi-step12-tested-debug`.
- Build Step 13 remains NOT STARTED.

## 2026-09-26 — Asset Integration V1.0

- Build Step 11 completed and locked.
- 159 Turkish teacher/narrator OGG clips were generated offline with 0 failures and committed under `audio/generated/speech/`.
- Seven original procedural animation/interaction SFX were generated and connected: soft pop, gentle tap, success chime, star sparkle, slide soft, retry soft and complete.
- `core:audio` and `OfflineAudioPlayer` were added; runtime Android/system/cloud TTS is not used.
- Activity instruction, success and retry audio IDs are connected in tracing, literacy, mathematics and mini-games.
- Parent narration/SFX preferences and 0.80x / 0.90x / 1.00x speech-rate settings are respected.
- Letter phoneme clips, 0–20 number clips, onboarding welcome audio and speech-rate preview audio are connected.
- Placeholder A01/A02 avatar rendering was replaced with 12 original offline vector child identities and a 16-pose renderer.
- Original learning-object pictograms were connected to literacy sound-awareness, mathematics counting/operations and mini-games.
- Generate Offline Audio run 36198942500 completed successfully: 159/159 speech, 7 SFX, 0 failed.
- Audio Validation run 36223818177 completed successfully.
- Visual Validation run 36223854933 completed successfully: 12 avatars, 16 poses, 25 literacy object pictograms.
- Android Build run 36223801498 completed successfully, including `:app:assembleDebug` and debug APK artifact upload.

## 2026-09-26 — Mini Games V1.0

- Build Step 10 completed and locked.
- Five pedagogical mini games added under `content/mini_games/`: Harf Avı, Say ve Seç, Eşini Bul, Yol Bulma and Gruplama.
- Mini games reuse already taught literacy, pre-writing and mathematics concepts rather than creating an unrelated reward loop.
- Game unlock rules are tied to progress in the relevant learning modules.
- Selected child avatar is preserved in game list and game screens.
- Mini-game completion persists locally through Preferences DataStore and is shown in the parent dashboard.
- No countdown pressure, lives, coins, loot boxes, leaderboards or online competition were added.
- Content Validation run 36193843755 completed successfully.
- Android Build run 36193789722 completed successfully, including `:app:assembleDebug` and debug APK artifact upload.
- Final object artwork, avatar artwork, female teacher audio and soft SFX remain deferred to Build Step 11.

## 2026-09-25 — Mathematics V1.0

- Source-mapped mathematics feature module completed and locked.
- 63 mathematics activity JSON files are active: 13 spatial/equal-object, 36 number/quantity, 11 measurement/mass, and 3 explicit EXTENSION activities.
- Spatial relations, equal objects/shapes, digit tracing 0–9, counting, tens/ones, ordinal positions, quantity comparison, rhythmic counting, patterns, length, non-standard measurement, and mass comparison are implemented.
- Addition, subtraction, and multiplication/grouping remain explicitly classified as EXTENSION because the supplied mathematics PDFs do not provide standalone systematic teaching sequences for those operations.
- Selected child avatar is carried into mathematics home, category, and activity screens.
- Mathematics completion persists locally through Preferences DataStore and is surfaced in the parent dashboard.
- Temporary authoring placeholders were removed.
- Content Validation run 36190586885 completed successfully.
- Android Build run 36190716305 completed successfully, including `:app:assembleDebug` and debug APK artifact upload.
- Real object artwork, final avatar artwork, female teacher audio, and SFX remain deferred to Build Step 11 without changing locked curriculum/activity IDs.

## 2026-09-25 — Pre-Writing / Tracing V1.0

- Interactive tracing feature module added and locked.
- Five source-mapped pre-writing activities added under `content/literacy/preparation/`.
- Wave, spiral, zigzag and curved tracing guides implemented.
- Finger stroke rendering and guide-proximity completion detection implemented.
- Five-difference visual-attention activity implemented.
- Selected child avatar is used in tracing home and activity screens.
- Completed tracing activities persist locally in Preferences DataStore.
- Root content directory is packaged into Android assets for offline runtime loading.
- Content validator defects discovered during first real content authoring were corrected; authoring validation now passes.
- Content Validation run 36168713215 completed successfully.
- Android Build run 36169159298 completed the debug APK build and artifact upload successfully.

## 2026-09-25 — Android App Shell V1.0

- Multi-module Kotlin + Jetpack Compose Android project created.
- AGP 9.4.1, Gradle 9.6 CI, Java 17, minSdk 26 and Android API 37 configuration established.
- Core model, navigation, design-system and Preferences DataStore modules added.
- Splash, onboarding, child profile, learning level, avatar selection, speech speed, theme, parent PIN, setup summary and child dashboard navigation implemented.
- Child dashboard category shell and learning-path shell implemented.
- Parent access requires an approximately 3-second long press followed by the local parent PIN.
- Parent dashboard and settings shell implemented.
- Theme, speech-rate, narration, SFX, music, reduced motion, large UI, high contrast, left-handed mode and daily goal settings persist locally.
- Android manifest contains no INTERNET permission and disables cleartext traffic.
- Device/system TTS is not used.
- Avatar visuals, teaching audio and real lesson engines remain intentionally deferred to their locked later build steps.
- GitHub Actions Android build workflow established.
- Build verification run 36167430659 completed successfully, including `:app:assembleDebug`.
- Temporary CI verification pull requests #1–#6 were closed after successful verification.

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
- Android application development had not started at this stage.

## 2026-09-25 — Experience Systems V1.0

- AVATAR_SYSTEM_V1.md locked with 12+ original human child avatars and a fixed reusable pose set.
- VISUAL_STYLE_GUIDE.md locked with source-inspired but original child-friendly educational cartoon rules.
- ANIMATION_RULES.md locked with slide/pop/fade entry behavior, gentle feedback, counting timing and reduced-motion behavior.
- AUDIO_STYLE_GUIDE.md locked as fully offline: no device TTS, no cloud TTS at runtime, no system notification sounds.
- Female teacher/narrator voice profiles locked: TEACHER_WARM, TEACHER_PHONICS, TEACHER_MATH, TEACHER_STORY, TEACHER_ENCOURAGE.
- Original procedural SFX profiles locked: SOFT_POP, GENTLE_TAP, SUCCESS_CHIME, STAR_SPARKLE, SLIDE_SOFT, RETRY_SOFT, COMPLETE.
- PARENT_SYSTEM_V1.md, ADMIN_PANEL_V1.md, THEME_ACCESSIBILITY_V1.md and CHILD_SAFETY_RULES.md locked.

## 2026-09-25 — Curriculum & Source Mapping V1.0

- CURRICULUM_LOCK_V1.md completed and locked from the six uploaded source PDFs.
- SOURCE_MAPPING.md completed and locked with source/page traceability.
- Pre-writing, literacy letter groups, spatial relations, number/numeracy, tens/ones, ordinal numbers, quantity comparison, rhythmic counting, patterns, length and mass topics mapped.
- Addition, subtraction and multiplication explicitly marked as EXTENSION because the uploaded source set does not provide standalone systematic teaching sequences for them.

## 2026-09-25 — Product & UI V1.0

- PRODUCT_SPEC_V1.md completed and locked.
- UI_FLOW_V1.md completed and locked.
- First-run setup, child profile, avatar selection, dashboard, learning path, activity states, rewards, parent gate, parent dashboard, settings, accessibility and admin boundaries defined.

## 2026-09-25 — Architecture V1.0

- Repository initialized.
- BUILD_PROCESS.md created and locked.
- GitHub project structure defined and locked.
