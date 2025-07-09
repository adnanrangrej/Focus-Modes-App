package com.github.adnanrangrej.focusmodes.ui.screens.modes.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.adnanrangrej.focusmodes.ui.components.FocusModeForm

@Composable
fun EditModeScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: EditModeScreenViewModel
) {

    val uiState by viewModel.uiState.collectAsState()
    val installedApps by viewModel.installedAppsList.collectAsState()

    FocusModeForm(
        modifier = modifier,
        name = uiState.mode.name,
        workingTime = if (uiState.mode.workDuration == 0L) "" else uiState.mode.workDuration.toString(),
        breakTime = if (uiState.mode.breakDuration == 0L) "" else uiState.mode.breakDuration.toString(),
        description = uiState.mode.description ?: "",
        installedApps = installedApps,
        selectedApps = uiState.selectedApps,
        onNameChange = {
            viewModel.updateUiState(
                uiState.copy(
                    mode = uiState.mode.copy(
                        name = it
                    )
                )
            )
        },
        onWorkingTimeChange = {
            viewModel.updateUiState(
                uiState.copy(
                    mode = uiState.mode.copy(
                        workDuration = it.toLongOrNull() ?: 0L
                    )
                )
            )
        },
        onBreakTimeChange = {
            viewModel.updateUiState(
                uiState.copy(
                    mode = uiState.mode.copy(
                        breakDuration = it.toLongOrNull() ?: 0L
                    )
                )
            )
        },
        onDescriptionChange = {
            viewModel.updateUiState(
                uiState.copy(
                    mode = uiState.mode.copy(
                        description = it
                    )
                )
            )
        },
        onConfirm = {
            viewModel.updateFocusMode(uiState.mode)
            navigateBack()
        },
        onAppChange = { appList ->
            viewModel.updateUiState(
                uiState.copy(
                    mode = uiState.mode.copy(
                        blockedAppPackages = appList.map { it.packageName }
                    ),
                    selectedApps = appList
                )
            )
        },
        canCreateMode = uiState.isEntryValid,
        confirmButtonText = "Save Changes"
    )
}