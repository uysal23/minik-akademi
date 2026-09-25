package com.uysal.minikakademi.core.model

enum class LearningLevel(val label: String) {
    PRESCHOOL_START("Okul Öncesi Başlangıç"),
    PRESCHOOL_ADVANCED("Okul Öncesi İleri"),
    GRADE_1("İlkokul 1")
}

enum class ThemePreference(val label: String) {
    PASTEL("Pastel"),
    NATURE("Doğa"),
    SKY("Gökyüzü"),
    HIGH_CONTRAST("Yüksek Kontrast")
}

enum class SpeechRateOption(val label: String, val multiplier: Float) {
    SLOW("Yavaş", 0.80f),
    NORMAL("Normal", 0.90f),
    FAST("Hızlı", 1.00f)
}

data class AvatarSpec(
    val id: String,
    val label: String,
    val description: String
)

object AvatarCatalog {
    val avatars: List<AvatarSpec> = listOf(
        AvatarSpec("avatar_01", "Avatar 1", "Kısa dalgalı saç"),
        AvatarSpec("avatar_02", "Avatar 2", "Uzun düz saç"),
        AvatarSpec("avatar_03", "Avatar 3", "Kıvırcık saç"),
        AvatarSpec("avatar_04", "Avatar 4", "Kısa düz saç"),
        AvatarSpec("avatar_05", "Avatar 5", "Dalgalı orta saç"),
        AvatarSpec("avatar_06", "Avatar 6", "Kısa kıvırcık saç"),
        AvatarSpec("avatar_07", "Avatar 7", "Gözlüklü kısa saç"),
        AvatarSpec("avatar_08", "Avatar 8", "Gözlüklü uzun saç"),
        AvatarSpec("avatar_09", "Avatar 9", "Topuz saç"),
        AvatarSpec("avatar_10", "Avatar 10", "Kısa kabarık saç"),
        AvatarSpec("avatar_11", "Avatar 11", "Örgülü saç"),
        AvatarSpec("avatar_12", "Avatar 12", "Orta boy düz saç")
    )

    fun find(id: String): AvatarSpec =
        avatars.firstOrNull { it.id == id } ?: avatars.first()
}

data class AppSettings(
    val setupComplete: Boolean = false,
    val childName: String = "",
    val learningLevel: LearningLevel = LearningLevel.PRESCHOOL_START,
    val avatarId: String = AvatarCatalog.avatars.first().id,
    val theme: ThemePreference = ThemePreference.PASTEL,
    val speechRate: SpeechRateOption = SpeechRateOption.NORMAL,
    val narrationEnabled: Boolean = true,
    val sfxEnabled: Boolean = true,
    val musicEnabled: Boolean = false,
    val reducedMotion: Boolean = false,
    val largeUi: Boolean = false,
    val highContrast: Boolean = false,
    val leftHanded: Boolean = false,
    val dailyGoalMinutes: Int = 10,
    val completedTracingActivities: Set<String> = emptySet(),
    val completedLiteracyActivities: Set<String> = emptySet()
)
