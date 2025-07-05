package com.github.adnanrangrej.focusmodes.ui.screens.modes.add

import com.github.adnanrangrej.focusmodes.ui.screens.modes.AppInfo
import com.github.adnanrangrej.focusmodes.domain.model.FocusMode

data class AddModeUiState(
    val mode: FocusMode = FocusMode(
        name = "",
        workDuration = 0,
        breakDuration = 0,
        description = null,
        blockedAppPackages = emptyList()
    ),
    val selectedApps: List<AppInfo> = emptyList(),
    val isEntryValid: Boolean = false
)