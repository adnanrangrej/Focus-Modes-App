package com.github.adnanrangrej.focusmodes.ui.navigation.graphs.pomodoro

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import com.github.adnanrangrej.focusmodes.ui.navigation.destination.pomodoro.PomodoroTimerScreen
import com.github.adnanrangrej.focusmodes.ui.screens.pomodoro.PomodoroTimerScreen
import com.github.adnanrangrej.focusmodes.ui.screens.pomodoro.PomodoroTimerViewModel

fun EntryProviderBuilder<Any>.pomodoroGraph() {

    entry<PomodoroTimerScreen.PomodoroMain> { navEntry ->
        val viewModel: PomodoroTimerViewModel = hiltViewModel()
        PomodoroTimerScreen(modifier = Modifier.fillMaxSize(), viewModel = viewModel)
    }
}