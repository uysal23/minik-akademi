#!/usr/bin/env python3
from __future__ import annotations

import hashlib
import io
import json
import os
import shutil
import subprocess
import sys
import urllib.error
import urllib.request
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
RUNTIME = ROOT / "audio_v4" / "runtime"
SPEECH = RUNTIME / "speech"
SFX = RUNTIME / "sfx"
MANIFEST = ROOT / "audio_v4" / "manifest" / "audio_manifest.json"
APPROVED_PHONEMES_DIR = ROOT / "audio_v4" / "approved_phonemes"
BUNDLED_ARCHIVE = ROOT / "audio_v4" / "source" / "v4_live.zip"
BUNDLED_ARCHIVE_SHA256_FILE = ROOT / "audio_v4" / "source" / "v4_live.sha256"

REPO = os.environ.get("GITHUB_REPOSITORY", "uysal23/minik-akademi")
def git_checkout_token() -> str | None:
    try:
        header = subprocess.check_output(
            ["git", "config", "--get", "http.https://github.com/.extraheader"],
            cwd=ROOT, text=True, stderr=subprocess.DEVNULL
        ).strip()
    except Exception:
        return None
    prefix = "AUTHORIZATION: basic "
    if not header.upper().startswith(prefix.upper()):
        return None
    import base64
    try:
        raw = base64.b64decode(header[len(prefix):]).decode("utf-8")
    except Exception:
        return None
    if ":" not in raw:
        return None
    return raw.split(":", 1)[1]

TOKEN = os.environ.get("GITHUB_TOKEN") or os.environ.get("GH_TOKEN") or git_checkout_token()
LOCAL_ARCHIVE = os.environ.get("MINIK_V4_AUDIO_ARCHIVE")

ARTIFACTS = [
    (10934530775, "e1efee1da61b4b6e9fd9e58c1269117c55ca6c1f5b78d791bc5be8f61631d3c1"),
    (10934520470, "dec5dc7194eb2c1b2b97ad4b8d6cb1e07516dba57e7397087bf5cd8fb70aeb36"),
    (10934137791, "363817218db29f9a58720fc871ac7a0510e45ddeb55951f0a65db8a70d1e57ee"),
    (10933494299, "814c738ae37861eeafcb7cff284149ae2ac41bd80c563e08b0cb26e067f64cb3"),
    (10933768803, "16f5ffa565d6f512f6b4f3306a5a707bdd36bb2f00a17cc3cbf7d72a4ede80ff"),
    (10933544334, "02bcae91b35f69eb46a960a0d5104f1d16985479b6205e655f88516dbc7c0b1b"),
    (10933959440, "423f2585feaa5bff5c562f2a6f20baadb3cf1c9f18c012caea9837f511077105"),
    (10934261948, "3e163f1bf525eb84923f3f5060c353eabe4515cb1c7db93a628cd3413628646a"),
    (10934615218, "6a8244f9025f88089fd3f2bef43667875751b30c07c70e841c2f335aee922a6a"),
    (10933089807, "822fb5f3c0275add3bae07d14936ca0606820794aec3784c96522cc88854db74"),
    (10933883786, "24af0cc6876c2a7e592d6a9a58f1260872aea481f5a9172870d2efa0102d3b8a"),
    (10934436488, "ff8330ccc7b48001adb0d91ad32d43b835dbe26466cbd36dd9665958c59f2a93"),
    (10934112768, "3cbd6ccf99f3334d55ebced1b3c255beb3954bebeb16fd444f0d5aeaf84a7cc1"),
    (10934117425, "8cc1907ccb2bce7ad4aa9405e76245dacba0cae6e051a58bfa4e887494c9677d"),
    (10934102830, "b5efabf6925b0e3743d2da7d1d90462ca3e9d03c4d1e442e5e37fcc541ac8b86"),
    (10933569101, "fbff24013e91382e96459a1c2267e6668faef2a3037308c6b4499bd1b43ed8ff"),
]

PHONEMES = {"a", "n", "e", "t", "i", "l", "o", "k", "u"}


def expected_ids() -> set[str]:
    data = json.loads(MANIFEST.read_text(encoding="utf-8"))
    ids = {str(x) for x in data.get("speechIds", [])}
    if len(ids) != 159:
        raise SystemExit(f"V4 C manifest must pin 159 unique audio IDs, found {len(ids)}")
    return ids

def download_artifact(artifact_id: int, expected_sha256: str) -> bytes:
    if not TOKEN:
        raise SystemExit(
            "V4 C audio is not cached locally and no GITHUB_TOKEN/GH_TOKEN is available. "
            "For local builds set MINIK_V4_AUDIO_ARCHIVE to the approved V4 C ZIP."
        )
    url = f"https://api.github.com/repos/{REPO}/actions/artifacts/{artifact_id}/zip"
    req = urllib.request.Request(
        url,
        headers={
            "Authorization": f"Bearer {TOKEN}",
            "Accept": "application/vnd.github+json",
            "X-GitHub-Api-Version": "2022-11-28",
            "User-Agent": "minik-akademi-v4-audio-preparer",
        },
    )
    try:
        with urllib.request.urlopen(req, timeout=120) as response:
            payload = response.read()
    except urllib.error.HTTPError as exc:
        raise SystemExit(f"Cannot download approved V4 C artifact {artifact_id}: HTTP {exc.code}") from exc
    digest = hashlib.sha256(payload).hexdigest()
    if digest != expected_sha256:
        raise SystemExit(
            f"Artifact {artifact_id} checksum mismatch: expected {expected_sha256}, got {digest}"
        )
    return payload


def copy_ogg_from_zip(payload: bytes, target: Path, seen: set[str]) -> None:
    with zipfile.ZipFile(io.BytesIO(payload)) as zf:
        for info in zf.infolist():
            if info.is_dir() or not info.filename.lower().endswith(".ogg"):
                continue
            name = Path(info.filename).name
            if name in seen:
                raise SystemExit(f"Duplicate C speech file across artifacts: {name}")
            (target / name).write_bytes(zf.read(info))
            seen.add(name)


def assemble_from_github(target: Path) -> None:
    seen: set[str] = set()
    for artifact_id, digest in ARTIFACTS:
        print(f"Downloading approved V4 C chunk {artifact_id}")
        copy_ogg_from_zip(download_artifact(artifact_id, digest), target, seen)


def assemble_from_local_archive(path: Path, target: Path) -> None:
    if not path.is_file():
        raise SystemExit(f"MINIK_V4_AUDIO_ARCHIVE does not exist: {path}")
    seen: set[str] = set()
    copy_ogg_from_zip(path.read_bytes(), target, seen)


def overlay_approved_phonemes(target: Path) -> None:
    present: set[str] = set()
    for phoneme in sorted(PHONEMES):
        name = f"aud_phoneme_{phoneme}.ogg"
        src = APPROVED_PHONEMES_DIR / name
        if not src.is_file():
            raise SystemExit(f"Approved phoneme file is missing: {src}")
        data = src.read_bytes()
        if len(data) <= 256 or not data.startswith(b"OggS") or b"\x01vorbis" not in data[:8192]:
            raise SystemExit(f"Approved phoneme is not valid OGG/Vorbis: {src}")
        (target / name).write_bytes(data)
        present.add(phoneme)

    if present != PHONEMES:
        raise SystemExit(f"Approved phoneme set mismatch: {sorted(present)}")
    print("Applied exact 9/9 owner-approved phoneme OGG files")

def validate_speech(target: Path) -> None:
    ids = expected_ids()
    files = {p.stem: p for p in target.glob("*.ogg")}
    if set(files) != ids:
        missing = sorted(ids - set(files))
        extra = sorted(set(files) - ids)
        raise SystemExit(f"V4 C runtime set mismatch. missing={missing} extra={extra}")
    for audio_id, path in files.items():
        data = path.read_bytes()
        if len(data) <= 256 or not data.startswith(b"OggS") or b"\x01vorbis" not in data[:8192]:
            raise SystemExit(f"Invalid OGG/Vorbis runtime speech file: {audio_id}")
    print("V4 C speech coverage PASS: 159/159")


def copy_sfx() -> None:
    source = ROOT / "audio" / "generated" / "sfx"
    SFX.mkdir(parents=True, exist_ok=True)
    for old in SFX.glob("*.ogg"):
        old.unlink()
    for item in source.glob("*.ogg"):
        shutil.copy2(item, SFX / item.name)
    count = len(list(SFX.glob("*.ogg")))
    if count != 7:
        raise SystemExit(f"Expected 7 locked SFX files, found {count}")
    print("Locked SFX copied unchanged: 7/7")


def main() -> None:
    if RUNTIME.exists():
        shutil.rmtree(RUNTIME)
    SPEECH.mkdir(parents=True, exist_ok=True)

    if LOCAL_ARCHIVE:
        assemble_from_local_archive(Path(LOCAL_ARCHIVE), SPEECH)
    elif BUNDLED_ARCHIVE.is_file():
        if not BUNDLED_ARCHIVE_SHA256_FILE.is_file():
            raise SystemExit(f"Bundled V4 C checksum file is missing: {BUNDLED_ARCHIVE_SHA256_FILE}")
        expected_digest = BUNDLED_ARCHIVE_SHA256_FILE.read_text(encoding="utf-8").strip().split()[0]
        digest = hashlib.sha256(BUNDLED_ARCHIVE.read_bytes()).hexdigest()
        if digest != expected_digest:
            raise SystemExit(
                f"Bundled V4 C archive checksum mismatch: expected {expected_digest}, got {digest}"
            )
        print(f"Using bundled approved V4 C archive: {BUNDLED_ARCHIVE.relative_to(ROOT)}")
        assemble_from_local_archive(BUNDLED_ARCHIVE, SPEECH)
    else:
        assemble_from_github(SPEECH)

    overlay_approved_phonemes(SPEECH)
    validate_speech(SPEECH)
    copy_sfx()

    marker = {
        "audioRevision": "V4_C",
        "voice": "C",
        "engine": "ResembleAI Chatterbox Multilingual V3",
        "seed": 618034,
        "speech": 159,
        "phonemeQa": "USER_APPROVED",
        "sfx": 7,
        "paidApiUsed": False,
    }
    (RUNTIME / "V4_C_READY.json").write_text(
        json.dumps(marker, ensure_ascii=False, indent=2) + "\n", encoding="utf-8"
    )
    print("Minik Akademi runtime audio ready: V4 C")


if __name__ == "__main__":
    main()
