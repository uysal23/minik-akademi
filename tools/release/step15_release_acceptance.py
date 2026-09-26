from __future__ import annotations

import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from pathlib import Path

APK = Path(sys.argv[1] if len(sys.argv) > 1 else "step15-release/MinikAkademi-0.1.0-release-ci-signed.apk")
PACKAGE = "com.uysal.minikakademi"
COMPONENT = "com.uysal.minikakademi/com.uysal.minikakademi.app.MainActivity"
OUT = Path("step15-smoke")
XML = Path("/tmp/minik-akademi-window.xml")
REMOTE_XML = "/data/local/tmp/minik-akademi-window.xml"


def run(*args: str, check: bool = True, capture: bool = False) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        list(args),
        check=check,
        text=True,
        stdout=subprocess.PIPE if capture else None,
        stderr=subprocess.PIPE if capture else None,
    )


def adb(*args: str, check: bool = True, capture: bool = False) -> subprocess.CompletedProcess[str]:
    return run("adb", *args, check=check, capture=capture)


def dump_nodes() -> list[ET.Element]:
    """Read the UI hierarchy without depending on emulated shared storage.

    GitHub Android emulators can transiently return a null UiAutomation root or
    expose /sdcard late after boot. /data/local/tmp is shell-owned and stable,
    so retry there before treating a missing hierarchy as an app failure.
    """
    last_error: Exception | None = None
    for attempt in range(1, 9):
        try:
            adb("shell", "rm", "-f", REMOTE_XML, check=False)
            dumped = adb(
                "shell", "uiautomator", "dump", REMOTE_XML,
                check=False, capture=True,
            )
            if dumped.returncode != 0:
                raise RuntimeError(
                    f"uiautomator dump failed rc={dumped.returncode}: "
                    f"{dumped.stderr or dumped.stdout}"
                )

            cat = adb("shell", "cat", REMOTE_XML, check=False, capture=True)
            xml_text = cat.stdout
            if cat.returncode != 0 or "<hierarchy" not in xml_text:
                raise RuntimeError(
                    f"UI hierarchy unavailable rc={cat.returncode}: "
                    f"{cat.stderr or xml_text}"
                )

            XML.write_text(xml_text, encoding="utf-8")
            root = ET.fromstring(xml_text)
            return list(root.iter("node"))
        except Exception as exc:
            last_error = exc
            print(f"UI DUMP RETRY {attempt}/8: {exc}")
            time.sleep(1.0)

    raise RuntimeError(f"Unable to read UI hierarchy after retries: {last_error}")


def node_text(node: ET.Element) -> str:
    return " ".join(
        value for value in (
            node.attrib.get("text", ""),
            node.attrib.get("content-desc", ""),
        ) if value
    )


def find_text(value: str) -> ET.Element:
    for node in dump_nodes():
        if value in node_text(node):
            return node
    raise RuntimeError(f"UI text not found: {value}")


def wait_text(value: str, timeout: float = 20.0) -> ET.Element:
    deadline = time.time() + timeout
    last: Exception | None = None
    while time.time() < deadline:
        try:
            return find_text(value)
        except Exception as exc:
            last = exc
            time.sleep(0.5)
    raise RuntimeError(f"Timed out waiting for UI text {value!r}: {last}")


def scroll_until_text(value: str, max_swipes: int = 6) -> ET.Element:
    for attempt in range(max_swipes + 1):
        try:
            return find_text(value)
        except Exception:
            if attempt == max_swipes:
                break
            adb("shell", "input", "swipe", "540", "1650", "540", "500", "550")
            time.sleep(0.7)
    raise RuntimeError(f"UI text not reachable after scrolling: {value}")


def center(bounds: str) -> tuple[int, int]:
    values = [int(x) for x in re.findall(r"\d+", bounds)]
    if len(values) != 4:
        raise RuntimeError(f"Invalid bounds: {bounds}")
    x1, y1, x2, y2 = values
    return (x1 + x2) // 2, (y1 + y2) // 2


def tap_text(value: str) -> None:
    node = wait_text(value)
    x, y = center(node.attrib["bounds"])
    adb("shell", "input", "tap", str(x), str(y))
    time.sleep(0.8)


def long_press_text(value: str, duration_ms: int = 3200) -> None:
    node = wait_text(value)
    x, y = center(node.attrib["bounds"])
    adb("shell", "input", "swipe", str(x), str(y), str(x), str(y), str(duration_ms))
    time.sleep(0.8)


def tap_class(class_fragment: str, index: int = 0) -> None:
    matches = [
        node for node in dump_nodes()
        if class_fragment in node.attrib.get("class", "")
        and node.attrib.get("enabled", "true") == "true"
    ]
    if index >= len(matches):
        raise RuntimeError(
            f"UI class {class_fragment!r} index {index} unavailable; found {len(matches)}"
        )
    x, y = center(matches[index].attrib["bounds"])
    adb("shell", "input", "tap", str(x), str(y))
    time.sleep(0.4)


def assert_text(value: str) -> None:
    wait_text(value)
    print(f"ASSERT OK: {value}")


def screenshot(name: str) -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        ["adb", "exec-out", "screencap", "-p"],
        check=True,
        stdout=subprocess.PIPE,
    )
    path = OUT / f"{name}.png"
    path.write_bytes(result.stdout)
    if path.stat().st_size == 0:
        raise RuntimeError(f"Empty screenshot: {path}")


def assert_process_alive() -> None:
    pid = adb("shell", "pidof", PACKAGE, capture=True).stdout.strip()
    if not pid:
        raise RuntimeError("Release app process is not alive")


def clear_focused_pin() -> None:
    for _ in range(4):
        adb("shell", "input", "keyevent", "KEYCODE_DEL")
        time.sleep(0.1)


def start_app() -> None:
    last_error: Exception | None = None
    for attempt in range(1, 4):
        adb("shell", "am", "broadcast", "-a", "android.intent.action.CLOSE_SYSTEM_DIALOGS", check=False)
        if attempt > 1:
            adb("shell", "am", "force-stop", PACKAGE, check=False)
            time.sleep(0.8)

        result = adb("shell", "am", "start", "-W", "-n", COMPONENT, capture=True)
        print(result.stdout)
        if result.returncode != 0:
            last_error = RuntimeError(result.stderr or result.stdout)
            continue

        try:
            wait_text("Minik Akademi", timeout=25)
            assert_process_alive()
            return
        except Exception as exc:
            last_error = exc
            pid = adb("shell", "pidof", PACKAGE, check=False, capture=True).stdout.strip()
            window = adb("shell", "dumpsys", "window", check=False, capture=True).stdout
            print(f"START RETRY {attempt}: pid={pid!r}; reason={exc}")
            print("\n".join(
                line for line in window.splitlines()
                if "mCurrentFocus" in line or "mFocusedApp" in line
            ))
            # Clear transient launcher/ANR/system surfaces before retrying.
            adb("shell", "input", "keyevent", "KEYCODE_BACK", check=False)
            adb("shell", "am", "broadcast", "-a", "android.intent.action.CLOSE_SYSTEM_DIALOGS", check=False)
            time.sleep(1.0)

    raise RuntimeError(f"Release app could not reach its welcome UI after retries: {last_error}")


def main() -> None:
    if not APK.is_file():
        raise RuntimeError(f"Release APK missing: {APK}")

    OUT.mkdir(parents=True, exist_ok=True)
    adb("shell", "settings", "put", "global", "hide_error_dialogs", "1", check=False)
    adb("logcat", "-c", check=False)
    adb("install", "--no-incremental", "-r", str(APK))

    # Offline-first acceptance: networking is disabled before first launch and remains off.
    adb("shell", "svc", "wifi", "disable", check=False)
    adb("shell", "svc", "data", "disable", check=False)

    start_app()
    assert_text("Minik Akademi'ye Hoş Geldiniz")
    screenshot("01_release_welcome")
    tap_text("Karşılama Sesini Dinle")
    time.sleep(0.8)
    assert_process_alive()

    tap_text("Kuruluma Başla")
    assert_text("Yetişkin Kurulumu")
    tap_text("Yetişkin Olarak Devam Et")

    assert_text("Çocuk Profili")
    tap_class("EditText", 0)
    adb("shell", "input", "text", "Ece")
    tap_text("Devam Et")

    assert_text("Eğitim Seviyesi")
    tap_text("Devam Et")

    assert_text("Avatarını Seç")
    tap_text("Bu Benim")

    assert_text("Konuşma Hızı")
    assert_text("Örneği Dinle")
    tap_text("Örneği Dinle")
    time.sleep(0.8)
    assert_process_alive()
    tap_text("Devam Et")

    assert_text("Tema")
    tap_text("Devam Et")

    assert_text("Ebeveyn PIN'i")
    tap_class("EditText", 0)
    adb("shell", "input", "text", "1234")
    tap_class("EditText", 1)
    adb("shell", "input", "text", "1234")
    tap_text("Devam Et")

    assert_text("Kurulum Özeti")
    assert_text("Ece")
    screenshot("02_release_setup_summary")
    tap_text("Çocuk Modunu Başlat")

    assert_text("Merhaba, Ece")
    for label in ("Çiziyorum", "Harfleri Öğreniyorum", "Matematik Öğreniyorum", "Oyun Zamanı"):
        assert_text(label)
    screenshot("03_release_dashboard")

    # DataStore persistence in the actual release APK while still offline.
    adb("shell", "am", "force-stop", PACKAGE)
    adb("shell", "am", "start", "-W", "-n", COMPONENT)
    assert_text("Merhaba, Ece")
    assert_text("Çiziyorum")
    screenshot("04_release_persistence_relaunch")

    tap_text("Çiziyorum")
    assert_text("Parmağınla çizgileri takip et. Acele etmene gerek yok.")
    tap_text("Yolu Takip Et")
    assert_text("Yolu Takip Et")
    assert_text("Dinle")
    tap_text("Dinle")
    time.sleep(0.8)
    assert_process_alive()
    screenshot("05_release_tracing")
    tap_text("Etkinlik Listesi")
    tap_text("Ana Sayfa")

    assert_text("Merhaba, Ece")
    tap_text("Harfleri Öğreniyorum")
    assert_text("Sesleri dinle, harfleri bul, yaz ve kelimeler oluştur.")
    screenshot("06_release_literacy")
    tap_text("Ana Sayfa")

    assert_text("Merhaba, Ece")
    tap_text("Matematik Öğreniyorum")
    assert_text("Nesnelerle düşün, say, karşılaştır ve çöz.")
    tap_text("Yer ve Yön")
    assert_text("Yer ve Yön")
    tap_text("Altında / üstünde")
    assert_text("Top masanın neresinde?")
    screenshot("07_release_mathematics")
    tap_text("Etkinlik Listesine Dön")
    tap_text("Matematik Menüsüne Dön")
    tap_text("Ana Sayfa")

    assert_text("Merhaba, Ece")
    tap_text("Oyun Zamanı")
    assert_text("Öğrendiklerini kısa oyunlarla tekrar et.")
    assert_text("Biraz daha çalışınca açılacak.")
    screenshot("08_release_mini_games")

    # Return to the dashboard and validate the actual release parent gate.
    adb("shell", "input", "keyevent", "KEYCODE_BACK")
    assert_text("Merhaba, Ece")
    long_press_text("⚙", 3300)
    assert_text("Ebeveyn Alanı")

    # Wrong PIN must be rejected.
    tap_class("EditText", 0)
    adb("shell", "input", "text", "0000")
    tap_text("Giriş")
    assert_text("PIN eşleşmedi. Tekrar deneyin.")

    # Correct PIN must open the parent dashboard and settings.
    tap_class("EditText", 0)
    clear_focused_pin()
    adb("shell", "input", "text", "1234")
    tap_text("Giriş")
    assert_text("Ebeveyn Paneli")
    assert_text("Ayarlar")
    screenshot("09_release_parent_gate")

    tap_text("Ayarlar")
    assert_text("Konuşma Hızı")
    assert_text("Tema")
    scroll_until_text("Erişilebilirlik")
    print("ASSERT OK: Erişilebilirlik (reachable by scrolling)")
    scroll_until_text("Günlük Hedef")
    print("ASSERT OK: Günlük Hedef (reachable by scrolling)")
    scroll_until_text("Ebeveyn Paneline Dön")
    print("ASSERT OK: Ebeveyn Paneline Dön (reachable by scrolling)")
    screenshot("10_release_parent_settings")

    tap_text("Ebeveyn Paneline Dön")
    assert_text("Ebeveyn Paneli")
    tap_text("Çocuk Moduna Dön")
    assert_text("Merhaba, Ece")

    package_path = adb("shell", "pm", "path", PACKAGE, capture=True).stdout.strip()
    pid = adb("shell", "pidof", PACKAGE, capture=True).stdout.strip()
    if not package_path.startswith("package:") or not pid:
        raise RuntimeError(f"Installed release is not healthy: path={package_path!r} pid={pid!r}")

    # Same-signature reinstall/update smoke: app data must survive an in-place reinstall.
    adb("install", "-r", str(APK))
    adb("shell", "am", "force-stop", PACKAGE)
    adb("shell", "am", "start", "-W", "-n", COMPONENT)
    assert_text("Merhaba, Ece")
    assert_text("Çiziyorum")
    screenshot("11_release_reinstall_persistence")
    assert_process_alive()

    # Final crash/ANR sweep over the whole release acceptance session.
    logcat = adb("logcat", "-d", capture=True).stdout
    (OUT / "release-logcat.txt").write_text(logcat, encoding="utf-8")
    if f"ANR in {PACKAGE}" in logcat:
        raise RuntimeError("Release acceptance detected an ANR")
    if "FATAL EXCEPTION" in logcat and f"Process: {PACKAGE}" in logcat:
        raise RuntimeError("Release acceptance detected a fatal exception")

    screenshots = sorted(OUT.glob("*.png"))
    if len(screenshots) != 11:
        raise RuntimeError(f"Expected 11 release evidence screenshots, found {len(screenshots)}")

    print("STEP 15 COMPREHENSIVE RELEASE ACCEPTANCE: PASS")


if __name__ == "__main__":
    main()
