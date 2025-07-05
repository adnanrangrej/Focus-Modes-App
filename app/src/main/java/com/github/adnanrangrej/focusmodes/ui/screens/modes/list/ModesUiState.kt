package com.github.adnanrangrej.focusmodes.ui.screens.modes.list

import com.github.adnanrangrej.focusmodes.domain.model.FocusMode

data class ModesUiState(
    val modes: List<FocusMode> = emptyList()
)