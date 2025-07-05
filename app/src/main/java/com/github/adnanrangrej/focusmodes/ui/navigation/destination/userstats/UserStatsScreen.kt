package com.github.adnanrangrej.focusmodes.ui.navigation.destination.userstats

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface UserStatsScreen : NavKey {

    @Serializable
    data object UserStatsMain : UserStatsScreen
}