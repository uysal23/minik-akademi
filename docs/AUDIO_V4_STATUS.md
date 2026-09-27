# Minik Akademi — Audio V4 C Status

**Status:** USER APPROVED — PROMOTION READY / GITHUB ACTIONS RUNNER BLOCKED
**Voice:** C
**Engine:** ResembleAI Chatterbox Multilingual V3
**Seed:** 618034
**Paid API:** none
**API key:** none

## Completed

- Spoken-text inventory: 159/159
- Spoken Text QA: PASS
- Content Validation: PASS
- C-voice generation: 159/159
- Generation chunks: 16/16 SUCCESS
- Format QA of assembled C set: PASS
  - OGG/Vorbis
  - mono
  - 24 kHz
- Phonemes a/n/e/t/i/l/o/k/u: USER APPROVED
- Live-promotion workflow is committed at:
  `.github/workflows/audio-v4-promote-live.yml`

## Current external blocker

GitHub Actions is currently refusing to start hosted runners for this private repository.
The promotion job and even a previously successful small content-validation job fail before step 1 with no runner steps/logs. Therefore this is not an audio-generation or validation failure.

The existing live V1 speech assets remain untouched until the V4 C promotion can execute atomically. This prevents a partial/mixed voice deployment.

No paid service or API is required or authorized.
