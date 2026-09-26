package com.uysal.minikakademi.app

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileOutputStream
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PedagogicalUiReviewTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

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

    private fun shot(name: String) {
        rule.waitForIdle()
        val bitmap = checkNotNull(
            InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        )
        val dir = File(rule.activity.getExternalFilesDir(null), "ui-review")
        check(dir.exists() || dir.mkdirs())
        val file = File(dir, "$name.png")
        FileOutputStream(file).use { stream ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
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
