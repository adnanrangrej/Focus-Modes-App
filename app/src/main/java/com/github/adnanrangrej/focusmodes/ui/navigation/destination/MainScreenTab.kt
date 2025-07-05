package com.github.adnanrangrej.focusmodes.ui.navigation.destination

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface MainScreenTab : NavKey {
    val icon: ImageVector
    val title: String

    @Serializable
    data object PomodoroTimer : MainScreenTab {
        override val icon: ImageVector = Icons.Default.Timer
        override val title: String = "Pomodoro Timer"
    }

    @Serializable
    data object Modes : MainScreenTab {
        override val icon: ImageVector = Icons.Default.DeveloperMode
        override val title: String = "Focus Modes"
    }

    @Serializable
    data object UserStats : MainScreenTab {
        override val icon: ImageVector = Icons.Default.BarChart
        override val title: String = "User Stats"
    }
}

