package com.github.adnanrangrej.focusmodes.ui.screens.modes.add

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.adnanrangrej.focusmodes.ui.screens.modes.AppInfo
import com.github.adnanrangrej.focusmodes.domain.model.FocusMode
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.InsertFocusModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale.getDefault
import javax.inject.Inject

@HiltViewModel
class AddModeScreenViewModel @Inject constructor(
    private val insertFocusModeUseCase: InsertFocusModeUseCase,
    private val app: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddModeUiState())
    val uiState: StateFlow<AddModeUiState> = _uiState

    private val _installedAppsList = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedAppsList: StateFlow<List<AppInfo>> = _installedAppsList


    init {
        loadInstalledApplications()
    }

    fun updateUiState(uiState: AddModeUiState) {
        _uiState.update {
            uiState.copy(
                isEntryValid = checkIfEntryValid(uiState.mode)
            )
        }
    }

    fun insertFocusMode(focusMode: FocusMode) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { insertFocusModeUseCase(focusMode) }
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
            }
        }
    }


    private fun checkIfEntryValid(focusMode: FocusMode): Boolean {
        return focusMode.name.isNotBlank() && focusMode.workDuration > 0 && focusMode.breakDuration > 0 && focusMode.blockedAppPackages.isNotEmpty()
    }

}