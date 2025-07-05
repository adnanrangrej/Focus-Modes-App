package com.github.adnanrangrej.focusmodes.ui.screens.pomodoro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimerControls(
    isRunning: Boolean,
    isPaused: Boolean,
    onStartClick: () -> Unit,
    onPauseResumeClick: () -> Unit,
    onStopClick: () -> Unit,
    onResetClick: () -> Unit
) {
    val isTimerActive = isRunning || isPaused

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp), // Give the box a fixed height to prevent layout shifts
        contentAlignment = Alignment.Center
    ) {
        if (isTimerActive) {
            // Show Pause/Resume, Reset, Stop controls when timer is active
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onResetClick) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Timer",
                        modifier = Modifier.size(28.dp)
                    )
                }

                FloatingActionButton(
                    onClick = onPauseResumeClick,
                    modifier = Modifier.size(72.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Resume",
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(onClick = onStopClick) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop Timer",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        } else {
            // Show only the Start button when the timer is idle
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(0.6f)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start Timer Icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Start Focus Session", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}