package com.example.focusmodes.navigation

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.focusmodes.ui.screens.FocusModesScreen
import com.example.focusmodes.ui.screens.PomodoroScreen
import com.example.focusmodes.ui.screens.ProgressScreen
import com.example.focusmodes.ui.viewmodels.FocusModesViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val viewModel: FocusModesViewModel = viewModel(
        factory = FocusModesViewModel.Factory(LocalContext.current.applicationContext as Application)
    )
    val activeMode by viewModel.activeMode.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.FocusModes.route,
        modifier = modifier
    ) {
        composable(Screen.Pomodoro.route) {
            PomodoroScreen(
                onDeactivateMode = {
                    viewModel.deactivateFocusMode()
                }
            )
        }
        composable(Screen.FocusModes.route) {
            FocusModesScreen(
                viewModel = viewModel,
                onNavigateToPomodoro = {
                    navController.navigate(Screen.Pomodoro.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.Progress.route) {
            ProgressScreen()
        }
    }
}

sealed class Screen(val route: String) {
    object Pomodoro : Screen("pomodoro")
    object FocusModes : Screen("focus_modes")
    object Progress : Screen("progress")
} 