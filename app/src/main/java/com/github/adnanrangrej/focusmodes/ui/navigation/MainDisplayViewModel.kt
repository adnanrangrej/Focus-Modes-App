package com.github.adnanrangrej.focusmodes.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.adnanrangrej.focusmodes.ui.navigation.destination.MainScreenTab
import com.github.adnanrangrej.focusmodes.ui.navigation.destination.modes.ModesScreen
import com.github.adnanrangrej.focusmodes.ui.navigation.destination.pomodoro.PomodoroTimerScreen
import com.github.adnanrangrej.focusmodes.ui.navigation.destination.userstats.UserStatsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainDisplayViewModel @Inject constructor() : ViewModel() {

    var selectedTab by mutableStateOf<MainScreenTab>(MainScreenTab.PomodoroTimer)
        private set


    val backStacks = mapOf(
        MainScreenTab.PomodoroTimer to mutableStateListOf<Any>(PomodoroTimerScreen.PomodoroMain),
        MainScreenTab.Modes to mutableStateListOf<Any>(ModesScreen.ModesMain),
        MainScreenTab.UserStats to mutableStateListOf<Any>(UserStatsScreen.UserStatsMain)
    )

    fun onTabSelected(tab: MainScreenTab) {
        selectedTab = tab
    }

    fun navigateInCurrentTab(screen: Any) {
        backStacks[selectedTab]?.add(screen)
    }

    fun navigateUp() {
        backStacks[selectedTab]?.remove(backStacks[selectedTab]?.last())
    }

}