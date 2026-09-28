# Changelog

## 2026-09-28 — Full Content Pedagogy Audit V1

- Audited all 125 authored activities plus all 20 live V4 instruction overrides for learning-target, answer, progression, safety and runtime consistency.
- Pre-writing tracing now requires the correct start point, forward progress, at least 90% path coverage and the real endpoint; the former unordered ~72% point-collection completion was removed.
- Letter tracing now uses ordered MEB-aligned multi-stroke guides for a, n, e, t, i, l, o, k and u; the former unordered ~70% point-collection completion was removed.
- Letter model previews are rendered from the same vector geometry as their trace paths and use three-line writing guides with direction arrows.
- Find Difference now gives gentle retry feedback on incorrect selections instead of silently ignoring them.
- Added a permanent semantic CI audit covering all activity files, live instruction overrides, option correctness, count/arithmetic/rhythmic answers, Turkish İ/ı normalization, allowed-letter constraints, mini-game semantics and ordered trace engines.
- Locked content/curriculum tree remains unchanged; no release APK build was started.

## 2026-09-28 — Digit Writing Maintenance V1

- Project-owner approved maintenance audits all 0–9 digit-writing activities against the current MEB basic-form and writing-direction reference.
- Corrected malformed or ambiguous tracing geometry, with specific remediation for 2, 3, 5, 6, 8 and 9 and direction/stroke-order corrections for 0, 1, 4 and 5.
- Digit tracing is now stroke-aware: each stroke has its own start point and direction arrow, forward progress is enforced, at least 90% plus the stroke endpoint is required, and every stroke must be completed before success.
- Added upper/baseline guides plus a dashed middle guide to the tracing board.
- The displayed model digit now uses the exact same vector geometry as the trace path instead of the Android system font, preventing example-vs-trace form mismatches.
- Added automated QA covering all ten digit activities, known legacy geometry regressions, ordered tracing behavior and model/trace geometry identity.
- New release APK build is not part of this maintenance change and remains separately gated.

## 2026-09-28 — Scene Visual Alignment Maintenance V2

- Project-owner approved maintenance fixes the 24 previously mismatched and 10 partially aligned authored scenes without changing curriculum IDs, learning targets or progression.
- Quantity comparison scenes now render the intended 3=3 and 6>4 relationships instead of the legacy shared 3-vs-5 fallback.
- Equal-object, tens/ones, abacus, pattern, ordinal, measurement, mass and spatial-relation scenes now use scene-specific visual compositions.
- Subtraction now visually separates removed objects and shows the remaining group.
- LearningObjectArt adds scene-required objects plus soft grounding/highlight depth for an offline 2.5D/CGI-inspired presentation.
- A 125-scene static alignment validator, 34-scene automated regression test and dedicated audit workflow gate were added.
- Release APK build is not part of this maintenance change and remains separately gated.

## 2026-09-26 — Release APK Build / Technical Acceptance V1.0

- Build Step 15 completed and locked for technical release acceptance.
- Final APK: `MinikAkademi-0.1.0-release-ci-signed.apk`.
- APK SHA-256: `4c7841b86d623f74baad2590dbe061df8d4e1cf591f8c1f595a2bc2a414fcba6`.
- Package `com.uysal.minikakademi`, versionCode 1, versionName 0.1.0, minSdk 26, targetSdk 37.
- APK Signature Scheme v2/v3 verification passed.
- INTERNET permission is absent; offline payload contains 166 OGG files and 127 JSON files.
- Release Candidate #12 build/sign/comprehensive acceptance job passed.
- 11/11 API 35 release acceptance screenshots passed, including onboarding, dashboard, tracing, literacy, mathematics, mini-games, parent PIN/settings and reinstall persistence.
- In-place reinstall preserved child data; full release-session crash/ANR sweep passed.
- Android API 26 / Android 8.0 verification #2 (run 36259338347) passed: install, MainActivity launch, visible welcome UI and clean crash/ANR log.
- Step 15 Final Technical Gate #1 (run 36259741949) passed and cross-verified release, acceptance and API 26 artifacts.
- Regression Tests #23 and Automated Tests #40 passed after the Step 15 maintenance fixes.
- A real release acceptance defect was found and corrected: Parent Settings was not vertically scrollable on the phone viewport; the screen now exposes Accessibility, Daily Goal and navigation controls by scrolling.
- Emulator/test-harness flakes discovered during acceptance were not accepted as product PASS results; cold-start retry and portable API 26 shell verification were added.
- Step 15 signing identity is CI-generated and valid for this tested APK. Long-term production/Play Store update continuity still requires a persistent production keystore stored outside source control in a secure deployment secret.

## 2026-09-26 — Regression Tests V1.0

- Build Step 14 completed and locked.
- Final Regression Tests #11 (run 36251160389) completed successfully.
- Locked contracts and validators: PASS.
- Kotlin unit tests and debug regression build: PASS.
- Persistence instrumentation and full UI regression: PASS.
- 17/17 regression evidence screens were generated and visually reviewed; no launcher/ANR/system-dialog contamination remained.
- UI evidence artifact: `minik-akademi-step14-regression-ui-evidence` (artifact ID 10909565688, SHA-256 `a6520af58163a90b16b3714f9e60254dd0a3c0d616df0a2110d3bf016fd8e57f`).
- Instrumentation reports artifact ID: 10909231153.
- Debug regression artifact ID: 10908977135.
- A previous technically successful run with Pixel Launcher system-dialog contamination was rejected rather than accepted as evidence.
- The faulty focus guard introduced during remediation was removed after its shell-command limitation was identified.
- Locked baseline now protects through Build Step 14.
- Release APK/bundle/signing was not started; Build Step 15 remains gated.
- Post-lock Regression Tests #15 (run 36251680063) also completed successfully after the Step 14 lock index, baseline and lock-contract updates; all three jobs passed.

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
