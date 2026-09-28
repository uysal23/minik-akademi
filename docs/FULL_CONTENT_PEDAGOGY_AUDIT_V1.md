# Minik Akademi — Full Content Pedagogy Audit V1

**Status:** OWNER-APPROVED MAINTENANCE  
**Date:** 2026-09-28  
**Scope:** 125 authored activities + live V4 instruction overrides + tracing/literacy/mathematics/mini-game runtime semantics

## Audit Standard

The audit checks that every child-facing activity remains:

- aligned with its learning target,
- non-punitive and free from time/score pressure,
- free from external URLs, ads and purchases,
- backed by a valid success condition rather than mere attempt,
- semantically consistent between target, selectable answer and displayed visual,
- compatible with its runtime interaction engine,
- progression-safe,
- repeatable without exploit-style completion.

For writing activities, current MEB İlkokul Türkçe Dersi Öğretim Programı writing-form and direction rules are used. Circular basic strokes start around the 2 o'clock direction and progress counter-clockwise; basic writing direction is left-to-right and top-to-bottom. Letter forms and stroke counts follow MEB Figure 2.

## Runtime defects found and corrected

### Pre-writing trace engine

Previous behavior:
- guide points could be collected out of order,
- about 72% guide coverage could complete the activity,
- the child did not have to start at the defined start point,
- reaching the actual target/end point was not required.

Corrected behavior:
- tracing must start from the large start point,
- progress is accepted only forward along a bounded point window,
- at least 90% of the path plus the real end point is required,
- a direction arrow is shown,
- incorrect start does not advance completion.

### Letter trace engine

Previous behavior:
- all strokes were flattened into one point list,
- about 70% unordered point coverage could complete a letter,
- multi-stroke letters did not preserve stroke order,
- some forms/directions did not follow the current MEB reference; notably lowercase n was driven bottom-to-top.

Corrected behavior:
- every letter is modeled as an ordered list of strokes,
- each stroke has its own visible start and direction,
- at least 90% plus the stroke endpoint is required,
- all strokes must complete in order,
- writing guide lines are displayed,
- the model letter is rendered from the same vector geometry as the trace path.

The audited letter set is:
a, n, e, t, i, l, o, k, u.

### Find Difference

Wrong selections now invoke the existing gentle retry feedback instead of silently doing nothing.

## Permanent semantic audit

Added:
- `tools/content-validator/audit_pedagogical_semantics.py`
- `tests/automated/test_full_content_pedagogy.py`

The audit covers all 125 authored activity JSON files and all 20 OWNER_APPROVED V4 live instruction overrides.

It validates:
- unique activity IDs,
- supported domain/activity-type combinations,
- child-safe/non-punitive wording,
- safety flags,
- completion rules,
- required options,
- single/multiple correct-answer semantics,
- count target vs correct-answer agreement,
- arithmetic result vs correct-answer agreement,
- rhythmic counting sequence results,
- Turkish dotted/dotless I handling,
- letter target consistency,
- allowed-letter constraints for syllable/word building,
- ten-object grouping semantics,
- mini-game runtime coverage,
- trace target and TRACE-mode requirements,
- ordered endpoint-aware trace engines for pre-writing, letters and digits.

## Curriculum preservation

The locked authored content tree remains unchanged:
`58b64a85eda9661deebbe605e29b44c0ed2cc3f4`

No curriculum ID, learning target or progression graph was silently changed.

## Release status

No new release APK build is started by this maintenance.
A new release build requires separate owner approval.
