package com.example.focusmodes.ui.viewmodels

sealed class UiEvent {
    data class ShowPermissionDialog(
        val title: String,
        val message: String,
        val onConfirm: () -> Unit
    ) : UiEvent()
    object RequestNotificationPermission : UiEvent()
    object RequestOverlayPermission : UiEvent()
    object RequestUsageStatsPermission : UiEvent()
    object NavigateToPomodoro : UiEvent()
} 