package com.uysal.minikakademi.app

import android.os.ParcelFileDescriptor
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.security.MessageDigest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PedagogicalUiReviewTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private var previousShotDigest: String? = null

    private fun waitForText(text: String, timeoutMillis: Long = 10_000) {
        rule.waitUntil(timeoutMillis) {
            rule.onAllNodesWithText(text, substring = false)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        rule.onNodeWithText(text).assertIsDisplayed()
    }

    private fun clickText(text: String) {
        rule.onNodeWithText(text).performClick()
        rule.waitForIdle()
    }

    private fun clickCardContaining(text: String) {
        rule.onNode(
            hasText(text, substring = true) and hasClickAction()
        ).performClick()
        rule.waitForIdle()
    }

    private fun runShellCommandBytes(command: String): ByteArray {
        val output = InstrumentationRegistry.getInstrumentation()
            .uiAutomation
            .executeShellCommand(command)
        return ParcelFileDescriptor.AutoCloseInputStream(output)
            .use { it.readBytes() }
    }

    private fun runShellCommand(command: String): String =
        runShellCommandBytes(command).toString(Charsets.UTF_8)

    private fun sha256(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString("") { byte -> "%02x".format(byte) }

    private fun waitForAppFocusedWindow(name: String) {
        var lastFocus = ""
        repeat(12) {
            rule.waitForIdle()
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            lastFocus = runShellCommand("dumpsys window")
            if (
                lastFocus.contains("com.uysal.minikakademi") &&
                !lastFocus.contains("Application Not Responding", ignoreCase = true) &&
                !lastFocus.contains("Launcher", ignoreCase = true)
            ) {
                return
            }
            Thread.sleep(250)
        }
        error("Step 14 evidence frame $name is obscured by a non-app/system window: $lastFocus")
    }

    private fun shot(name: String) {
        waitForAppFocusedWindow(name)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val directory = "/sdcard/Download/minik-akademi-ui-review"
        val file = "$directory/$name.png"
        runShellCommand("mkdir -p $directory")

        var candidateDigest: String? = null
        var stableFrameCount = 0
        var settledDigest: String? = null

        // Compose semantics can become idle a frame before SurfaceFlinger presents the
        // new screen. Capture until the physical frame has changed from the previous
        // evidence image and remains identical across two consecutive captures.
        for (attempt in 1..12) {
            rule.waitForIdle()
            instrumentation.waitForIdleSync()
            Thread.sleep(if (attempt == 1) 450 else 250)

            runShellCommand("screencap -p $file")
            val bytes = runShellCommandBytes("cat $file")
            check(bytes.isNotEmpty()) {
                "Step 13 screenshot was empty: $file"
            }

            val digest = sha256(bytes)
            if (digest == candidateDigest) {
                stableFrameCount += 1
            } else {
                candidateDigest = digest
                stableFrameCount = 1
            }

            if (stableFrameCount >= 2 && digest != previousShotDigest) {
                settledDigest = digest
                break
            }
        }

        check(settledDigest != null) {
            "Step 13 did not reach a new settled frame for $name"
        }
        previousShotDigest = settledDigest

        val listing = runShellCommand("ls -l $file")
        check(listing.contains("$name.png")) {
            "Step 13 screenshot was not created: $file"
        }
    }

    @Test
    fun fullFirstRunAndLearningAreaReview() {
        waitForText("Minik Akademi'ye Hoş Geldiniz")
        shot("01_welcome")

        clickText("Kuruluma Başla")
        waitForText("Yetişkin Kurulumu")
        shot("02_adult_setup_gate")

        clickText("Yetişkin Olarak Devam Et")
        waitForText("Çocuk Profili")
        shot("03_child_profile")

        rule.onAllNodes(hasSetTextAction())[0].performTextInput("Ece")
        clickText("Devam Et")

        waitForText("Eğitim Seviyesi")
        shot("04_learning_level")
        clickText("Devam Et")

        waitForText("Avatarını Seç")
        shot("05_avatar_selection")
        clickText("Bu Benim")

        waitForText("Konuşma Hızı")
        rule.onNodeWithText("Örneği Dinle").assertIsDisplayed()
        shot("06_offline_voice_speed")
        clickText("Devam Et")

        waitForText("Tema")
        shot("07_theme")
        clickText("Devam Et")

        waitForText("Ebeveyn PIN'i")
        shot("08_parent_pin")
        val pinFields = rule.onAllNodes(hasSetTextAction())
        pinFields[0].performTextInput("1234")
        pinFields[1].performTextInput("1234")
        clickText("Devam Et")

        waitForText("Kurulum Özeti")
        rule.onNodeWithText("Ece").assertIsDisplayed()
        shot("09_setup_summary")

        clickText("Çocuk Modunu Başlat")
        waitForText("Merhaba, Ece")
        rule.onNodeWithText("DEVAM ET").assertIsDisplayed()
        rule.onNodeWithText("Çiziyorum").assertIsDisplayed()
        rule.onNodeWithText("Harfleri Öğreniyorum").assertIsDisplayed()
        rule.onNodeWithText("Matematik Öğreniyorum").assertIsDisplayed()
        rule.onNodeWithText("Oyun Zamanı").assertIsDisplayed()
        shot("10_child_home")

        clickText("Çiziyorum")
        waitForText("Çiziyorum")
        rule.onNodeWithText(
            "Parmağınla çizgileri takip et. Acele etmene gerek yok."
        ).assertIsDisplayed()
        shot("11_tracing_home")

        clickCardContaining("Yolu Takip Et")
        waitForText("Yolu Takip Et")
        rule.onNodeWithText("🔊 Dinle").assertIsDisplayed()
        shot("12_tracing_activity")
        clickText("Etkinlik Listesi")
        waitForText("Çiziyorum")
        clickText("Ana Sayfa")

        waitForText("Merhaba, Ece")
        clickText("Harfleri Öğreniyorum")
        waitForText("Harfleri Öğreniyorum")
        rule.onNodeWithText(
            "Sesleri dinle, harfleri bul, yaz ve kelimeler oluştur."
        ).assertIsDisplayed()
        shot("13_literacy_home")
        clickText("Ana Sayfa")

        waitForText("Merhaba, Ece")
        clickText("Matematik Öğreniyorum")
        waitForText("Matematik Öğreniyorum")
        rule.onNodeWithText("Nesnelerle düşün, say, karşılaştır ve çöz.").assertIsDisplayed()
        shot("14_math_home")

        clickCardContaining("Yer ve Yön")
        waitForText("Yer ve Yön")
        shot("15_math_category")

        clickCardContaining("Altında / üstünde")
        waitForText("Altında / üstünde")
        rule.onNodeWithText("Top masanın neresinde?").assertIsDisplayed()
        rule.onNodeWithText("🔊 Dinle").assertIsDisplayed()
        shot("16_math_activity")
        clickText("Etkinlik Listesine Dön")
        waitForText("Yer ve Yön")
        clickText("Matematik Menüsüne Dön")
        waitForText("Matematik Öğreniyorum")
        clickText("Ana Sayfa")

        waitForText("Merhaba, Ece")
        clickText("Oyun Zamanı")
        waitForText("Oyun Zamanı")
        rule.onNodeWithText("Öğrendiklerini kısa oyunlarla tekrar et.").assertIsDisplayed()
        rule.onAllNodesWithText("Biraz daha çalışınca açılacak.", substring = true)[0]
            .assertIsDisplayed()
        shot("17_mini_games_locked")
    }
}
