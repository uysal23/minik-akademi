package com.uysal.minikakademi.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppModelsTest {

    @Test
    fun avatarCatalog_hasTwelveStableUniqueHumanAvatarIds() {
        assertEquals(12, AvatarCatalog.avatars.size)
        assertEquals(12, AvatarCatalog.avatars.map { it.id }.toSet().size)
        assertEquals(
            (1..12).map { "avatar_" + it.toString().padStart(2, '0') },
            AvatarCatalog.avatars.map { it.id }
        )
    }

    @Test
    fun avatarCatalog_unknownIdFallsBackToFirstAvatar() {
        assertEquals("avatar_01", AvatarCatalog.find("missing").id)
    }

    @Test
    fun speechRates_matchLockedOfflineTempoOptions() {
        assertEquals(0.80f, SpeechRateOption.SLOW.multiplier, 0.0001f)
        assertEquals(0.90f, SpeechRateOption.NORMAL.multiplier, 0.0001f)
        assertEquals(1.00f, SpeechRateOption.FAST.multiplier, 0.0001f)
    }

    @Test
    fun defaultSettings_areChildSafeAndOfflineFriendly() {
        val settings = AppSettings()
        assertTrue(settings.narrationEnabled)
        assertTrue(settings.sfxEnabled)
        assertFalse(settings.musicEnabled)
        assertEquals(10, settings.dailyGoalMinutes)
        assertTrue(settings.completedTracingActivities.isEmpty())
        assertTrue(settings.completedLiteracyActivities.isEmpty())
        assertTrue(settings.completedMathematicsActivities.isEmpty())
        assertTrue(settings.completedMiniGames.isEmpty())
    }
}
