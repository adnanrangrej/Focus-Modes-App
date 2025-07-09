package com.github.adnanrangrej.focusmodes.ui.screens.pomodoro

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTheme
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTypography
import com.github.adnanrangrej.focusmodes.ui.theme.ScreenThemes

@Composable
fun TimerDisplay(
    progress: Float,
    timeString: String,
    statusText: String,
    isWorking: Boolean
) {

    // Get the timer colors
    val timerColors = ScreenThemes.timerColors()

    // Animate the color to transition smoothly between work and break states
    val indicatorColor by animateColorAsState(
        targetValue = if (isWorking) timerColors["work"]!! else timerColors["shortBreak"]!!,
        animationSpec = tween(FocusTheme.animation.medium),
        label = "ColorAnimation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(300.dp)
            .shadow(
                elevation = FocusTheme.elevation.extraLarge,
                shape = CircleShape
            )
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Background Track
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            strokeWidth = 12.dp,
        )

        // Moving Progress Indicator
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = indicatorColor,
            strokeWidth = 12.dp,
            trackColor = Color.Transparent,
            strokeCap = StrokeCap.Round
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = timeString,
                style = FocusTypography.timerDisplay
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}