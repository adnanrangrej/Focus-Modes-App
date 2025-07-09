package com.github.adnanrangrej.focusmodes.ui.screens.pomodoro

import androidx.compose.animation.core.LinearEasing
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
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTheme

@Composable
fun PomodoroTimerScreen(
    modifier: Modifier = Modifier,
    viewModel: PomodoroTimerViewModel
) {
    val uiState by viewModel.uiState

    // Calculate the progress based on the time remaining
    val targetProgress = if (uiState.totalTime > 0) {
        uiState.timeRemaining.toFloat() / uiState.totalTime.toFloat()
    } else {
        1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(
            durationMillis = FocusTheme.animation.slow,
            easing = LinearEasing // linear easing for a steady clock-like feel
        ),
        label = "ProgressAnimation"
    )

    // The main layout composable
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(1f))

        TimerDisplay(
            progress = animatedProgress,
            timeString = formatTime(uiState.timeRemaining),
            statusText = when {
                !uiState.isRunning && !uiState.isPaused -> "Ready to Focus?"
                uiState.isWorking -> "Working"
                !uiState.isWorking -> "On a Break"
                else -> "Paused"
            },
            isWorking = uiState.isWorking
        )

        Spacer(Modifier.weight(1f))

        TimerControls(
            isRunning = uiState.isRunning,
            isPaused = uiState.isPaused,
            onPauseResumeClick = viewModel::pauseOrResume,
            onStopClick = viewModel::stopTimer,
            onResetClick = viewModel::resetTimer,
            onStartClick = viewModel::startTimer
        )

        Spacer(Modifier.height(FocusTheme.spacing.huge))
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}