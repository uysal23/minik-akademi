# Workflows

GitHub Actions üzerinde çalışan proje otomasyonları:

- `android-build.yml` — debug APK derleme ve artifact yükleme
- `content-validation.yml` — içerik şema/müfredat doğrulaması
- `generate-offline-audio.yml` — offline konuşma ve SFX üretimi
- `audio-validation.yml` — offline audio bütünlük doğrulaması
- `visual-validation.yml` — avatar ve öğrenme görseli doğrulaması
- `automated-tests.yml` — Build Step 12 Python sözleşme testleri, Kotlin/JUnit testleri, validator'lar ve test sonrası debug APK build

Tüm bu işlemler GitHub üzerinde çalışır; proje sahibinin bilgisayarında Android Studio, Gradle, Python veya Codex çalıştırması gerekmez.
