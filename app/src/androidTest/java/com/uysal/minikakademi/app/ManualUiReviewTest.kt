package com.uysal.minikakademi.app

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.test.platform.app.InstrumentationRegistry
import com.uysal.minikakademi.core.designsystem.MinikAkademiTheme
import com.uysal.minikakademi.core.model.AppSettings
import com.uysal.minikakademi.core.model.AvatarCatalog
import com.uysal.minikakademi.core.model.LearningLevel
import com.uysal.minikakademi.core.model.SpeechRateOption
import com.uysal.minikakademi.core.model.ThemePreference
import com.uysal.minikakademi.feature.avatarselection.AvatarSelectionScreen
import com.uysal.minikakademi.feature.childhome.ChildHomeScreen
import com.uysal.minikakademi.feature.literacy.LiteracyActivityScreen
import com.uysal.minikakademi.feature.literacy.LiteracyHomeScreen
import com.uysal.minikakademi.feature.mathematics.MathematicsActivityScreen
import com.uysal.minikakademi.feature.mathematics.MathematicsHomeScreen
import com.uysal.minikakademi.feature.minigames.MiniGameScreen
import com.uysal.minikakademi.feature.minigames.MiniGamesHomeScreen
import com.uysal.minikakademi.feature.onboarding.WelcomeScreen
import com.uysal.minikakademi.feature.parentdashboard.ParentDashboardScreen
import com.uysal.minikakademi.feature.settings.ParentSettingsScreen
import com.uysal.minikakademi.feature.tracing.TracingActivityScreen
import com.uysal.minikakademi.feature.tracing.TracingHomeScreen
import java.io.File
import java.io.FileOutputStream
import org.junit.Rule
import org.junit.Test

class ManualUiReviewTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val settings = AppSettings(
        setupComplete = true,
        childName = "Deniz",
        learningLevel = LearningLevel.GRADE_1,
        avatarId = AvatarCatalog.avatars[6].id,
        theme = ThemePreference.PASTEL,
        speechRate = SpeechRateOption.NORMAL,
        narrationEnabled = false,
        sfxEnabled = false,
        completedTracingActivities = setOf("PRE-TRACE-PATH-001"),
        completedLiteracyActivities = setOf("LIT-A-INTRO-001", "LIT-A-FIND-001"),
        completedMathematicsActivities = setOf("ACT-MAT-NUM-02", "ACT-MAT-EQ-02", "ACT-MAT-TENS-01"),
        completedMiniGames = setOf("GAME-MATH-MATCH-001")
    )

    @Test
    fun review01_welcome() = render("01_welcome") {
        WelcomeScreen(onStart = {})
    }

    @Test
    fun review02_avatarSelection() = render("02_avatar_selection") {
        AvatarSelectionScreen(
            selectedAvatarId = settings.avatarId,
            onAvatarSelected = {},
            onContinue = {}
        )
    }

    @Test
    fun review03_childHome() = render("03_child_home") {
        ChildHomeScreen(
            settings = settings,
            onContinue = {},
            onOpenTracing = {},
            onOpenLiteracy = {},
            onOpenMath = {},
            onOpenGames = {},
            onParentAccess = {}
        )
    }

    @Test
    fun review04_tracingHome() = render("04_tracing_home", expectedText = "Yolu Takip Et") {
        TracingHomeScreen(
            avatarId = settings.avatarId,
            completedActivityIds = settings.completedTracingActivities,
            onOpenActivity = {},
            onBack = {}
        )
    }

    @Test
    fun review05_tracingActivity() = render(
        "05_tracing_activity",
        forbiddenText = "Etkinlik bulunamadı."
    ) {
        TracingActivityScreen(
            activityId = "PRE-TRACE-PATH-001",
            avatarId = settings.avatarId,
            narrationEnabled = false,
            sfxEnabled = false,
            speechRate = 0.90f,
            onBack = {},
            onComplete = {}
        )
    }

    @Test
    fun review06_literacyHome() = render("06_literacy_home") {
        LiteracyHomeScreen(
            avatarId = settings.avatarId,
            completedActivityIds = settings.completedLiteracyActivities,
            onOpenLetter = {},
            onBack = {}
        )
    }

    @Test
    fun review07_literacySoundActivity() = render(
        "07_literacy_sound_activity",
        expectedText = "a Sesini Bul",
        forbiddenText = "Etkinlik bulunamadı."
    ) {
        LiteracyActivityScreen(
            activityId = "LIT-A-SOUND-001",
            avatarId = settings.avatarId,
            narrationEnabled = false,
            sfxEnabled = false,
            speechRate = 0.90f,
            onBack = {},
            onComplete = {}
        )
    }

    @Test
    fun review08_mathematicsHome() = render("08_mathematics_home") {
        MathematicsHomeScreen(
            avatarId = settings.avatarId,
            completedActivityIds = settings.completedMathematicsActivities,
            onOpenCategory = {},
            onBack = {}
        )
    }

    @Test
    fun review09_mathematicsCounting() = render(
        "09_mathematics_counting",
        forbiddenText = "Etkinlik bulunamadı."
    ) {
        MathematicsActivityScreen(
            activityId = "ACT-MAT-NUM-02",
            avatarId = settings.avatarId,
            narrationEnabled = false,
            sfxEnabled = false,
            speechRate = 0.90f,
            onBack = {},
            onComplete = {}
        )
    }

    @Test
    fun review10_miniGamesHome() = render("10_mini_games_home", expectedText = "Eşini Bul") {
        MiniGamesHomeScreen(
            avatarId = settings.avatarId,
            completedMiniGames = settings.completedMiniGames,
            completedTracingActivities = settings.completedTracingActivities,
            completedLiteracyActivities = settings.completedLiteracyActivities,
            completedMathematicsActivities = settings.completedMathematicsActivities,
            onOpenGame = {},
            onBack = {}
        )
    }

    @Test
    fun review11_miniGame() = render(
        "11_mini_game",
        expectedText = "Eşini Bul",
        forbiddenText = "Oyun bulunamadı."
    ) {
        MiniGameScreen(
            gameId = "GAME-MATH-MATCH-001",
            avatarId = settings.avatarId,
            narrationEnabled = false,
            sfxEnabled = false,
            speechRate = 0.90f,
            onBack = {},
            onComplete = {}
        )
    }

    @Test
    fun review12_parentDashboard() = render("12_parent_dashboard", expectedText = "Ebeveyn Paneli") {
        ParentDashboardScreen(
            settings = settings,
            onOpenSettings = {},
            onReturnToChild = {}
        )
    }

    @Test
    fun review13_parentSettings() = render("13_parent_settings", expectedText = "Ayarlar") {
        ParentSettingsScreen(
            settings = settings,
            onThemeChanged = {},
            onSpeechRateChanged = {},
            onNarrationChanged = {},
            onSfxChanged = {},
            onMusicChanged = {},
            onReducedMotionChanged = {},
            onLargeUiChanged = {},
            onHighContrastChanged = {},
            onLeftHandedChanged = {},
            onDailyGoalChanged = {},
            onBack = {}
        )
    }

    @Test
    fun review14_highContrastLargeUiHome() {
        val accessible = settings.copy(
            theme = ThemePreference.HIGH_CONTRAST,
            highContrast = true,
            largeUi = true,
            reducedMotion = true
        )
        render(
            name = "14_high_contrast_large_ui_home",
            settingsOverride = accessible
        ) {
            ChildHomeScreen(
                settings = accessible,
                onContinue = {},
                onOpenTracing = {},
                onOpenLiteracy = {},
                onOpenMath = {},
                onOpenGames = {},
                onParentAccess = {}
            )
        }
    }

    private fun render(
        name: String,
        expectedText: String? = null,
        forbiddenText: String? = null,
        settingsOverride: AppSettings = settings,
        content: @Composable () -> Unit
    ) {
        composeRule.setContent {
            MinikAkademiTheme(
                themePreference = settingsOverride.theme,
                highContrast = settingsOverride.highContrast,
                largeUi = settingsOverride.largeUi
            ) {
                content()
            }
        }
        composeRule.waitForIdle()

        if (expectedText != null) {
            composeRule.waitUntil(timeoutMillis = 8_000) {
                composeRule.onAllNodesWithText(expectedText, substring = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText(expectedText, substring = true)
                .assertIsDisplayed()
        }
        if (forbiddenText != null) {
            composeRule.onNodeWithText(forbiddenText, substring = true)
                .assertDoesNotExist()
        }

        val image = composeRule.onRoot().captureToImage().asAndroidBitmap()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dir = File(context.getExternalFilesDir(null), "manual-ui").apply { mkdirs() }
        FileOutputStream(File(dir, "$name.png")).use {
            image.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
