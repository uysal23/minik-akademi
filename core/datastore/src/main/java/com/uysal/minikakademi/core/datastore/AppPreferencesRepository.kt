package com.uysal.minikakademi.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.uysal.minikakademi.core.model.AppSettings
import com.uysal.minikakademi.core.model.AvatarCatalog
import com.uysal.minikakademi.core.model.LearningLevel
import com.uysal.minikakademi.core.model.SpeechRateOption
import com.uysal.minikakademi.core.model.ThemePreference
import java.security.MessageDigest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.minikAkademiDataStore by preferencesDataStore(name = "minik_akademi_settings")

class AppPreferencesRepository(private val context: Context) {

    private object Keys {
        val setupComplete = booleanPreferencesKey("setup_complete")
        val childName = stringPreferencesKey("child_name")
        val learningLevel = stringPreferencesKey("learning_level")
        val avatarId = stringPreferencesKey("avatar_id")
        val theme = stringPreferencesKey("theme")
        val speechRate = stringPreferencesKey("speech_rate")
        val narrationEnabled = booleanPreferencesKey("narration_enabled")
        val sfxEnabled = booleanPreferencesKey("sfx_enabled")
        val musicEnabled = booleanPreferencesKey("music_enabled")
        val reducedMotion = booleanPreferencesKey("reduced_motion")
        val largeUi = booleanPreferencesKey("large_ui")
        val highContrast = booleanPreferencesKey("high_contrast")
        val leftHanded = booleanPreferencesKey("left_handed")
        val dailyGoalMinutes = intPreferencesKey("daily_goal_minutes")
        val parentPinHash = stringPreferencesKey("parent_pin_hash")
        val completedTracingActivities = stringSetPreferencesKey("completed_tracing_activities")
        val completedLiteracyActivities = stringSetPreferencesKey("completed_literacy_activities")
        val completedMathematicsActivities = stringSetPreferencesKey("completed_mathematics_activities")
        val completedMiniGames = stringSetPreferencesKey("completed_mini_games")
    }

    val settings: Flow<AppSettings> = context.minikAkademiDataStore.data.map { prefs ->
        AppSettings(
            setupComplete = prefs[Keys.setupComplete] ?: false,
            childName = prefs[Keys.childName].orEmpty(),
            learningLevel = enumOrDefault(
                prefs[Keys.learningLevel],
                LearningLevel.PRESCHOOL_START
            ),
            avatarId = prefs[Keys.avatarId] ?: AvatarCatalog.avatars.first().id,
            theme = enumOrDefault(prefs[Keys.theme], ThemePreference.PASTEL),
            speechRate = enumOrDefault(prefs[Keys.speechRate], SpeechRateOption.NORMAL),
            narrationEnabled = prefs[Keys.narrationEnabled] ?: true,
            sfxEnabled = prefs[Keys.sfxEnabled] ?: true,
            musicEnabled = prefs[Keys.musicEnabled] ?: false,
            reducedMotion = prefs[Keys.reducedMotion] ?: false,
            largeUi = prefs[Keys.largeUi] ?: false,
            highContrast = prefs[Keys.highContrast] ?: false,
            leftHanded = prefs[Keys.leftHanded] ?: false,
            dailyGoalMinutes = prefs[Keys.dailyGoalMinutes] ?: 10,
            completedTracingActivities = prefs[Keys.completedTracingActivities] ?: emptySet(),
            completedLiteracyActivities = prefs[Keys.completedLiteracyActivities] ?: emptySet(),
            completedMathematicsActivities = prefs[Keys.completedMathematicsActivities] ?: emptySet(),
            completedMiniGames = prefs[Keys.completedMiniGames] ?: emptySet()
        )
    }

    suspend fun setChildName(value: String) = edit { it[Keys.childName] = value.trim() }
    suspend fun setLearningLevel(value: LearningLevel) = edit { it[Keys.learningLevel] = value.name }
    suspend fun setAvatarId(value: String) = edit { it[Keys.avatarId] = value }
    suspend fun setTheme(value: ThemePreference) = edit { it[Keys.theme] = value.name }
    suspend fun setSpeechRate(value: SpeechRateOption) = edit { it[Keys.speechRate] = value.name }
    suspend fun setNarrationEnabled(value: Boolean) = edit { it[Keys.narrationEnabled] = value }
    suspend fun setSfxEnabled(value: Boolean) = edit { it[Keys.sfxEnabled] = value }
    suspend fun setMusicEnabled(value: Boolean) = edit { it[Keys.musicEnabled] = value }
    suspend fun setReducedMotion(value: Boolean) = edit { it[Keys.reducedMotion] = value }
    suspend fun setLargeUi(value: Boolean) = edit { it[Keys.largeUi] = value }
    suspend fun setHighContrast(value: Boolean) = edit { it[Keys.highContrast] = value }
    suspend fun setLeftHanded(value: Boolean) = edit { it[Keys.leftHanded] = value }

    suspend fun setDailyGoalMinutes(value: Int) = edit {
        it[Keys.dailyGoalMinutes] = value.coerceIn(5, 30)
    }

    suspend fun markTracingActivityComplete(activityId: String) = edit { prefs ->
        val current = prefs[Keys.completedTracingActivities] ?: emptySet()
        prefs[Keys.completedTracingActivities] = current + activityId
    }

    suspend fun markLiteracyActivityComplete(activityId: String) = edit { prefs ->
        val current = prefs[Keys.completedLiteracyActivities] ?: emptySet()
        prefs[Keys.completedLiteracyActivities] = current + activityId
    }

    suspend fun markMathematicsActivityComplete(activityId: String) = edit { prefs ->
        val current = prefs[Keys.completedMathematicsActivities] ?: emptySet()
        prefs[Keys.completedMathematicsActivities] = current + activityId
    }

    suspend fun markMiniGameComplete(gameId: String) = edit { prefs ->
        val current = prefs[Keys.completedMiniGames] ?: emptySet()
        prefs[Keys.completedMiniGames] = current + gameId
    }

    suspend fun setParentPin(pin: String) = edit {
        it[Keys.parentPinHash] = hashPin(pin)
    }

    suspend fun verifyParentPin(pin: String): Boolean {
        val stored = context.minikAkademiDataStore.data.first()[Keys.parentPinHash] ?: return false
        return stored == hashPin(pin)
    }

    suspend fun setSetupComplete(value: Boolean) = edit {
        it[Keys.setupComplete] = value
    }

    private suspend fun edit(block: (MutablePreferences) -> Unit) {
        context.minikAkademiDataStore.edit { preferences ->
            block(preferences)
        }
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(raw: String?, default: T): T =
        runCatching { enumValueOf<T>(raw.orEmpty()) }.getOrDefault(default)

    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return digest.joinToString(separator = "") { byte ->
            "%02x".format(byte.toInt() and 0xff)
        }
    }
}
