package com.github.adnanrangrej.focusmodes.ui.navigation.destination.modes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface ModesScreen : NavKey {

    @Serializable
    data object ModesMain : ModesScreen

    @Serializable
    data object CreateMode : ModesScreen

    @Serializable
    data class EditMode(val modeId: Long) : ModesScreen

}