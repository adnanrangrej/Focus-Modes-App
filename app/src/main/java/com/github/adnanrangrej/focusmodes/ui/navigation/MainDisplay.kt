package com.github.adnanrangrej.focusmodes.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.github.adnanrangrej.focusmodes.ui.components.FocusModesBottomNavBar
import com.github.adnanrangrej.focusmodes.ui.navigation.destination.MainScreenTab.Modes
import com.github.adnanrangrej.focusmodes.ui.navigation.destination.MainScreenTab.PomodoroTimer
import com.github.adnanrangrej.focusmodes.ui.navigation.destination.MainScreenTab.UserStats
import com.github.adnanrangrej.focusmodes.ui.navigation.graphs.modes.modesGraph
import com.github.adnanrangrej.focusmodes.ui.navigation.graphs.pomodoro.pomodoroGraph
import com.github.adnanrangrej.focusmodes.ui.navigation.graphs.userstats.userStatsGraph
import com.github.adnanrangrej.focusmodes.ui.screens.pomodoro.PomodoroTimerViewModel

@Composable
fun MainDisplay(
    modifier: Modifier = Modifier
) {

    val mainDisplayViewModel: MainDisplayViewModel = viewModel()
    val pomodoroTimerViewModel: PomodoroTimerViewModel = viewModel()

    val currentStackToShow = mainDisplayViewModel.backStacks[mainDisplayViewModel.selectedTab]!!

    val navItems = listOf(
        PomodoroTimer,
        Modes,
        UserStats
    )


    Scaffold(
        modifier = modifier,
        bottomBar = {
            FocusModesBottomNavBar(
                navItems = navItems,
                selectedItem = mainDisplayViewModel.selectedTab,
                onNavItemClick = {
                    mainDisplayViewModel.onTabSelected(it)
                }
            )

        }
    ) { innerPadding ->

        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = currentStackToShow,
            onBack = { mainDisplayViewModel.navigateUp() },
            entryDecorators = listOf(
                rememberSceneSetupNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                pomodoroGraph()
                modesGraph(
                    navigateInCurrentTab = mainDisplayViewModel::navigateInCurrentTab,
                    navigateUp = mainDisplayViewModel::navigateUp,
                    onModeToggled = { mode ->
                        if (mode != null) {
                            mainDisplayViewModel.onTabSelected(PomodoroTimer)
                            pomodoroTimerViewModel.startTimer(
                                mode.workDuration * 60 * 1000L,
                                mode.breakDuration * 60 * 1000L,
                                mode.name
                            )
                        } else {
                            pomodoroTimerViewModel.stopTimer()
                        }
                    }
                )
                userStatsGraph()
            }
        )
    }
}