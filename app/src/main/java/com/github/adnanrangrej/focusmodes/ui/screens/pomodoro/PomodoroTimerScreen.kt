package com.github.adnanrangrej.focusmodes.ui.screens.pomodoro

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PomodoroTimerScreen(
    modifier: Modifier = Modifier,
    viewModel: PomodoroTimerViewModel
) {
    val uiState by viewModel.uiState

    // This calculation remains here, as it's derived from the state
    val targetProgress = if (uiState.totalTime > 0) {
        uiState.timeRemaining.toFloat() / uiState.totalTime.toFloat()
    } else {
        1f
    }

    // The animation state also lives here
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "ProgressAnimation"
    )

    // The main layout composable
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(1f))

        // Pass only the necessary data down to the display component
        TimerDisplay(
            progress = animatedProgress,
            timeString = formatTime(uiState.timeRemaining),
            statusText = when {
                !uiState.isRunning && !uiState.isPaused -> "Ready"
                uiState.isWorking -> "Working"
                !uiState.isWorking -> "Relaxing"
                else -> "Paused"
            },
            isWorking = uiState.isWorking
        )

        Spacer(Modifier.weight(1f))

        // Pass state and event lambdas down to the controls component
        TimerControls(
            isRunning = uiState.isRunning,
            isPaused = uiState.isPaused,
            onPauseResumeClick = viewModel::pauseOrResume,
            onStopClick = viewModel::stopTimer,
            onResetClick = viewModel::resetTimer,
            onStartClick = viewModel::startTimer
        )

        Spacer(Modifier.height(48.dp))
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}