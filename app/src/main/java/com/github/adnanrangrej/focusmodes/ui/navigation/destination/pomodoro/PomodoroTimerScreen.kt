package com.github.adnanrangrej.focusmodes.ui.navigation.destination.pomodoro

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface PomodoroTimerScreen : NavKey {

    @Serializable
    data object PomodoroMain : PomodoroTimerScreen
}