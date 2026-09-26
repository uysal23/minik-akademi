package com.uysal.minikakademi.core.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppRouteTest {

    @Test
    fun generatedLearningRoutes_matchDeclaredPatterns() {
        assertEquals("tracing_activity/ACT-PRE-001", AppRoute.tracingActivity("ACT-PRE-001"))
        assertEquals("literacy_letter/LIT-G1-A", AppRoute.literacyLetter("LIT-G1-A"))
        assertEquals("literacy_activity/ACT-LIT-001", AppRoute.literacyActivity("ACT-LIT-001"))
        assertEquals("mathematics_category/SEQ-MAT-NUMBERS", AppRoute.mathematicsCategory("SEQ-MAT-NUMBERS"))
        assertEquals("mathematics_activity/ACT-MAT-NUM-02", AppRoute.mathematicsActivity("ACT-MAT-NUM-02"))
        assertEquals("mini_game/GAME-MATH-COUNT-001", AppRoute.miniGame("GAME-MATH-COUNT-001"))
    }

    @Test
    fun childAndParentRoots_remainSeparated() {
        val childRoutes = listOf(
            AppRoute.HOME,
            AppRoute.TRACING_HOME,
            AppRoute.LITERACY_HOME,
            AppRoute.MATHEMATICS_HOME,
            AppRoute.MINI_GAMES_HOME
        )
        val parentRoutes = listOf(
            AppRoute.PARENT_GATE,
            AppRoute.PARENT_DASHBOARD,
            AppRoute.PARENT_SETTINGS
        )

        assertTrue(childRoutes.intersect(parentRoutes.toSet()).isEmpty())
    }
}
