package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Home")
    data object Practice : Screen("practice?scenarioId={scenarioId}&lessonId={lessonId}", "Speaking Practice") {
        fun createRoute(scenarioId: String? = null, lessonId: String? = null): String {
            val s = scenarioId ?: ""
            val l = lessonId ?: ""
            return "practice?scenarioId=$s&lessonId=$l"
        }
    }
    data object RolePlay : Screen("roleplay", "Role-play")
    data object Lessons : Screen("lessons", "Daily Lessons")
    data object Notebook : Screen("notebook", "Vocabulary")
    data object Progress : Screen("progress", "Progress")
    data object Settings : Screen("settings", "Settings")
}
