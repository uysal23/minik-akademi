package com.uysal.minikakademi.core.navigation

object AppRoute {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val CHILD_PROFILE = "child_profile"
    const val LEARNING_LEVEL = "learning_level"
    const val AVATAR = "avatar"
    const val SOUND = "sound"
    const val THEME = "theme"
    const val PIN = "pin"
    const val SETUP_SUMMARY = "setup_summary"
    const val HOME = "home"
    const val LEARNING_PATH = "learning_path"
    const val LEARNING_PATH_PATTERN = "learning_path/{category}"
    const val PARENT_GATE = "parent_gate"
    const val PARENT_DASHBOARD = "parent_dashboard"
    const val PARENT_SETTINGS = "parent_settings"

    fun learningPath(category: String): String = "$LEARNING_PATH/$category"
}
