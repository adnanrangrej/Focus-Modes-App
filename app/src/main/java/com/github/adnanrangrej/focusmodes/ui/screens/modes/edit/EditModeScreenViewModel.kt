package com.github.adnanrangrej.focusmodes.ui.screens.modes.edit

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.adnanrangrej.focusmodes.ui.screens.modes.AppInfo
import com.github.adnanrangrej.focusmodes.domain.model.FocusMode
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.GetFocusModeByIdUseCase
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.UpdateFocusModeUseCase
import com.github.adnanrangrej.focusmodes.ui.screens.modes.add.AddModeUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale.getDefault

@HiltViewModel(assistedFactory = EditModeScreenViewModel.Factory::class)
class EditModeScreenViewModel @AssistedInject constructor(
    private val getFocusModeByIdUseCase: GetFocusModeByIdUseCase,
    private val updateFocusModeUseCase: UpdateFocusModeUseCase,
    private val app: Application,
    @Assisted private val modeId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddModeUiState())
    val uiState: MutableStateFlow<AddModeUiState> = _uiState

    private val _installedAppsList = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedAppsList: StateFlow<List<AppInfo>> = _installedAppsList

    init {
        loadInstalledApplications()
        viewModelScope.launch {
            getFocusModeByIdUseCase(modeId)?.collect { mode ->
                _uiState.update { it.copy(mode = mode) }
                maybeLoadSelectedApps()
            }
        }
    }

    private fun maybeLoadSelectedApps() {
        val mode = _uiState.value.mode
        val apps = _installedAppsList.value

        if (mode.blockedAppPackages.isNotEmpty() && apps.isNotEmpty()) {
            val selectedApps = apps.filter { appInfo ->
                mode.blockedAppPackages.contains(appInfo.packageName)
            }

            _uiState.update {
                it.copy(selectedApps = selectedApps)
            }
        }
    }

    fun updateUiState(uiState: AddModeUiState) {
        _uiState.update {
            uiState.copy(
                isEntryValid = checkIfEntryValid(uiState.mode)
            )
        }
    }

    fun updateFocusMode(focusMode: FocusMode) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { updateFocusModeUseCase(focusMode) }
        }
    }

    private fun loadInstalledApplications() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val packageManager = app.packageManager

                val mainIntent = Intent(Intent.ACTION_MAIN, null)
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

                val resolveInfo = packageManager.queryIntentActivities(mainIntent, 0)

                val installedApps = resolveInfo.mapNotNull { resolveInfo ->
                    val appInfo = resolveInfo.activityInfo.applicationInfo

                    if (appInfo.packageName == app.packageName) {
                        return@mapNotNull null
                    }

                    AppInfo(
                        packageName = appInfo.packageName,
                        appName = appInfo.loadLabel(packageManager).toString(),
                        appIcon = appInfo.loadIcon(packageManager)
                    )
                }.sortedBy { it.packageName.lowercase(getDefault()) }

                _installedAppsList.update { installedApps }
                maybeLoadSelectedApps()
            }
        }
    }

    private fun checkIfEntryValid(focusMode: FocusMode): Boolean {
        return focusMode.name.isNotBlank() && focusMode.workDuration > 0 && focusMode.breakDuration > 0 && focusMode.blockedAppPackages.isNotEmpty()
    }

    @AssistedFactory
    interface Factory {
        fun create(modeId: Long): EditModeScreenViewModel
    }
}
