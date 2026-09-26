package com.uysal.minikakademi.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.uysal.minikakademi.core.datastore.AppPreferencesRepository
import com.uysal.minikakademi.core.model.LearningLevel
import com.uysal.minikakademi.core.model.SpeechRateOption
import com.uysal.minikakademi.core.model.ThemePreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegressionPersistenceTest {

    @Test
    fun lockedSettingsProgressAndPinPersistAcrossRepositoryRecreation() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val first = AppPreferencesRepository(context)

        first.setChildName("  Regresyon Ece  ")
        first.setLearningLevel(LearningLevel.GRADE_1)
        first.setAvatarId("avatar_12")
        first.setTheme(ThemePreference.HIGH_CONTRAST)
        first.setSpeechRate(SpeechRateOption.FAST)
        first.setNarrationEnabled(false)
        first.setSfxEnabled(false)
        first.setMusicEnabled(true)
        first.setReducedMotion(true)
        first.setLargeUi(true)
        first.setHighContrast(true)
        first.setLeftHanded(true)
        first.setDailyGoalMinutes(99)
        first.setParentPin("2468")
        first.markTracingActivityComplete("REG-TRACING")
        first.markLiteracyActivityComplete("REG-LITERACY")
        first.markMathematicsActivityComplete("REG-MATH")
        first.markMiniGameComplete("REG-GAME")
        first.setSetupComplete(true)

        val second = AppPreferencesRepository(context)
        val persisted = second.settings.first()

        assertTrue(persisted.setupComplete)
        assertEquals("Regresyon Ece", persisted.childName)
        assertEquals(LearningLevel.GRADE_1, persisted.learningLevel)
        assertEquals("avatar_12", persisted.avatarId)
        assertEquals(ThemePreference.HIGH_CONTRAST, persisted.theme)
        assertEquals(SpeechRateOption.FAST, persisted.speechRate)
        assertFalse(persisted.narrationEnabled)
        assertFalse(persisted.sfxEnabled)
        assertTrue(persisted.musicEnabled)
        assertTrue(persisted.reducedMotion)
        assertTrue(persisted.largeUi)
        assertTrue(persisted.highContrast)
        assertTrue(persisted.leftHanded)
        assertEquals(30, persisted.dailyGoalMinutes)
        assertTrue("REG-TRACING" in persisted.completedTracingActivities)
        assertTrue("REG-LITERACY" in persisted.completedLiteracyActivities)
        assertTrue("REG-MATH" in persisted.completedMathematicsActivities)
        assertTrue("REG-GAME" in persisted.completedMiniGames)
        assertTrue(second.verifyParentPin("2468"))
        assertFalse(second.verifyParentPin("0000"))
    }
}
