package com.uysal.minikakademi.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uysal.minikakademi.core.datastore.AppPreferencesRepository
import com.uysal.minikakademi.core.model.AppSettings
import com.uysal.minikakademi.core.navigation.AppRoute
import com.uysal.minikakademi.feature.avatarselection.AvatarSelectionScreen
import com.uysal.minikakademi.feature.childhome.ChildHomeScreen
import com.uysal.minikakademi.feature.childprofile.ChildProfileScreen
import com.uysal.minikakademi.feature.learningpath.LearningPathScreen
import com.uysal.minikakademi.feature.onboarding.LearningLevelScreen
import com.uysal.minikakademi.feature.onboarding.ParentPinSetupScreen
import com.uysal.minikakademi.feature.onboarding.SetupSummaryScreen
import com.uysal.minikakademi.feature.onboarding.SoundSetupScreen
import com.uysal.minikakademi.feature.onboarding.ThemeSetupScreen
import com.uysal.minikakademi.feature.onboarding.WelcomeScreen
import com.uysal.minikakademi.feature.parentdashboard.ParentDashboardScreen
import com.uysal.minikakademi.feature.parentgate.ParentGateScreen
import com.uysal.minikakademi.feature.settings.ParentSettingsScreen
import com.uysal.minikakademi.feature.splash.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun MinikAkademiNavHost(
    repository: AppPreferencesRepository,
    settings: AppSettings
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    var parentPinError by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = AppRoute.SPLASH
    ) {
        composable(AppRoute.SPLASH) {
            SplashScreen {
                val target = if (settings.setupComplete) AppRoute.HOME else AppRoute.WELCOME
                navController.navigate(target) {
                    popUpTo(AppRoute.SPLASH) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(AppRoute.WELCOME) {
            WelcomeScreen {
                navController.navigate(AppRoute.CHILD_PROFILE)
            }
        }

        composable(AppRoute.CHILD_PROFILE) {
            ChildProfileScreen(
                initialName = settings.childName,
                onContinue = { name ->
                    scope.launch {
                        repository.setChildName(name)
                        navController.navigate(AppRoute.LEARNING_LEVEL)
                    }
                }
            )
        }

        composable(AppRoute.LEARNING_LEVEL) {
            LearningLevelScreen(
                selected = settings.learningLevel,
                onSelected = { level ->
                    scope.launch { repository.setLearningLevel(level) }
                },
                onContinue = { navController.navigate(AppRoute.AVATAR) }
            )
        }

        composable(AppRoute.AVATAR) {
            AvatarSelectionScreen(
                selectedAvatarId = settings.avatarId,
                onAvatarSelected = { avatarId ->
                    scope.launch { repository.setAvatarId(avatarId) }
                },
                onContinue = { navController.navigate(AppRoute.SOUND) }
            )
        }

        composable(AppRoute.SOUND) {
            SoundSetupScreen(
                selected = settings.speechRate,
                onSelected = { rate ->
                    scope.launch { repository.setSpeechRate(rate) }
                },
                onPreview = { },
                onContinue = { navController.navigate(AppRoute.THEME) }
            )
        }

        composable(AppRoute.THEME) {
            ThemeSetupScreen(
                selected = settings.theme,
                onSelected = { theme ->
                    scope.launch { repository.setTheme(theme) }
                },
                onContinue = { navController.navigate(AppRoute.PIN) }
            )
        }

        composable(AppRoute.PIN) {
            ParentPinSetupScreen { pin ->
                scope.launch {
                    repository.setParentPin(pin)
                    navController.navigate(AppRoute.SETUP_SUMMARY)
                }
            }
        }

        composable(AppRoute.SETUP_SUMMARY) {
            SetupSummaryScreen(
                settings = settings,
                onStartChildMode = {
                    scope.launch {
                        repository.setSetupComplete(true)
                        navController.navigate(AppRoute.HOME) {
                            popUpTo(AppRoute.WELCOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(AppRoute.HOME) {
            ChildHomeScreen(
                settings = settings,
                onContinue = {
                    navController.navigate(AppRoute.learningPath("continue"))
                },
                onOpenTracing = {
                    navController.navigate(AppRoute.learningPath("tracing"))
                },
                onOpenLiteracy = {
                    navController.navigate(AppRoute.learningPath("literacy"))
                },
                onOpenMath = {
                    navController.navigate(AppRoute.learningPath("math"))
                },
                onOpenGames = {
                    navController.navigate(AppRoute.learningPath("games"))
                },
                onParentAccess = {
                    parentPinError = false
                    navController.navigate(AppRoute.PARENT_GATE)
                }
            )
        }

        composable(AppRoute.LEARNING_PATH_PATTERN) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category").orEmpty()
            LearningPathScreen(
                category = category,
                onBackHome = { navController.popBackStack() }
            )
        }

        composable(AppRoute.PARENT_GATE) {
            ParentGateScreen(
                hasError = parentPinError,
                onSubmit = { pin ->
                    scope.launch {
                        val valid = repository.verifyParentPin(pin)
                        if (valid) {
                            parentPinError = false
                            navController.navigate(AppRoute.PARENT_DASHBOARD)
                        } else {
                            parentPinError = true
                        }
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(AppRoute.PARENT_DASHBOARD) {
            ParentDashboardScreen(
                settings = settings,
                onOpenSettings = {
                    navController.navigate(AppRoute.PARENT_SETTINGS)
                },
                onReturnToChild = {
                    navController.navigate(AppRoute.HOME) {
                        popUpTo(AppRoute.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoute.PARENT_SETTINGS) {
            ParentSettingsScreen(
                settings = settings,
                onThemeChanged = { value ->
                    scope.launch { repository.setTheme(value) }
                },
                onSpeechRateChanged = { value ->
                    scope.launch { repository.setSpeechRate(value) }
                },
                onNarrationChanged = { value ->
                    scope.launch { repository.setNarrationEnabled(value) }
                },
                onSfxChanged = { value ->
                    scope.launch { repository.setSfxEnabled(value) }
                },
                onMusicChanged = { value ->
                    scope.launch { repository.setMusicEnabled(value) }
                },
                onReducedMotionChanged = { value ->
                    scope.launch { repository.setReducedMotion(value) }
                },
                onLargeUiChanged = { value ->
                    scope.launch { repository.setLargeUi(value) }
                },
                onHighContrastChanged = { value ->
                    scope.launch { repository.setHighContrast(value) }
                },
                onLeftHandedChanged = { value ->
                    scope.launch { repository.setLeftHanded(value) }
                },
                onDailyGoalChanged = { value ->
                    scope.launch { repository.setDailyGoalMinutes(value) }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
