package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.home.HomeDashboardScreen
import com.example.ui.lessons.LessonsScreen
import com.example.ui.notebook.VocabularyNotebookScreen
import com.example.ui.practice.SpeakingPracticeScreen
import com.example.ui.progress.ProgressScreen
import com.example.ui.roleplay.RolePlayScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.TealPrimary

data class NavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

val topLevelNavItems = listOf(
    NavItem(Screen.Home, Icons.Default.Home, "Home"),
    NavItem(Screen.RolePlay, Icons.Default.RecordVoiceOver, "Role-Play"),
    NavItem(Screen.Lessons, Icons.Default.School, "Lessons"),
    NavItem(Screen.Notebook, Icons.AutoMirrored.Filled.MenuBook, "Notebook"),
    NavItem(Screen.Progress, Icons.Default.BarChart, "Progress"),
    NavItem(Screen.Settings, Icons.Default.Settings, "Settings")
)

@Composable
fun SpeakMateApp(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if current screen is the fullscreen speaking practice
    val isPracticeScreen = currentRoute?.startsWith("practice") == true

    // Check screen width for tablet Navigation Rail vs phone Navigation Bar
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    if (isTablet && !isPracticeScreen) {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                topLevelNavItems.forEach { item ->
                    val isSelected = currentRoute == item.screen.route
                    NavigationRailItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = Color(0xFF00382E),
                            indicatorColor = TealPrimary
                        )
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                AppNavGraph(navController = navController)
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                if (!isPracticeScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        tonalElevation = 8.dp
                    ) {
                        topLevelNavItems.forEach { item ->
                            val isSelected = currentRoute == item.screen.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF00382E),
                                    indicatorColor = TealPrimary,
                                    selectedTextColor = TealPrimary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AppNavGraph(navController = navController)
            }
        }
    }
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeDashboardScreen(
                onNavigateToPractice = {
                    navController.navigate(Screen.Practice.createRoute())
                },
                onNavigateToRolePlay = {
                    navController.navigate(Screen.RolePlay.route)
                },
                onNavigateToLessons = {
                    navController.navigate(Screen.Lessons.route)
                },
                onNavigateToNotebook = {
                    navController.navigate(Screen.Notebook.route)
                }
            )
        }

        composable(
            route = "practice?scenarioId={scenarioId}&lessonId={lessonId}",
            arguments = listOf(
                navArgument("scenarioId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("lessonId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val scenarioId = backStackEntry.arguments?.getString("scenarioId").takeIf { !it.isNullOrBlank() }
            val lessonId = backStackEntry.arguments?.getString("lessonId").takeIf { !it.isNullOrBlank() }

            SpeakingPracticeScreen(
                scenarioId = scenarioId,
                lessonId = lessonId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.RolePlay.route) {
            RolePlayScreen(
                onSelectScenario = { scenarioId ->
                    navController.navigate(Screen.Practice.createRoute(scenarioId = scenarioId))
                }
            )
        }

        composable(Screen.Lessons.route) {
            LessonsScreen(
                onStartLessonPractice = { lessonId ->
                    navController.navigate(Screen.Practice.createRoute(lessonId = lessonId))
                }
            )
        }

        composable(Screen.Notebook.route) {
            VocabularyNotebookScreen()
        }

        composable(Screen.Progress.route) {
            ProgressScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
