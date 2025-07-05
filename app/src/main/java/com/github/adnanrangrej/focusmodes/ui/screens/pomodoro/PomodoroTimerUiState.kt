package com.github.adnanrangrej.focusmodes.ui.screens.pomodoro

data class PomodoroTimerUiState(
    val timeRemaining: Long = 2 * 60 * 1000L,
    val totalTime: Long = 2 * 60 * 1000L,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,
    val isWorking: Boolean = false
)