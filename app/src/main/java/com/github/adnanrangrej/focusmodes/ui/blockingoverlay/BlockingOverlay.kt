package com.github.adnanrangrej.focusmodes.ui.blockingoverlay

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.github.adnanrangrej.focusmodes.ui.theme.FocusComponentShapes
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTheme
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTypography

@Composable
fun BlockingOverlay(
    blockedAppIcon: Drawable,
    blockedAppName: String,
    onGoBack: () -> Unit
) {
    // Animation state for a subtle "pop-in" effect
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "OverlayScaleAnimation"
    )

    // Remember the converted bitmap to improve performance
    val bitmap = remember(blockedAppIcon) {
        blockedAppIcon.toBitmap().asImageBitmap()
    }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.9f))
            // This empty, clickable modifier intercepts all touch events
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .scale(scale) // Apply the pop-in animation
                .padding(horizontal = FocusTheme.spacing.extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Surface to give the icon a nice background
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(96.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = FocusTheme.elevation.medium
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "$blockedAppName icon",
                    modifier = Modifier
                        .padding(FocusTheme.spacing.medium)
                        .clip(CircleShape)
                )
            }

            Spacer(Modifier.height(FocusTheme.spacing.large))

            Text(
                text = "Focus Mode is Active",
                style = FocusTypography.overlayTitle,
                color = Color.White
            )

            Spacer(Modifier.height(FocusTheme.spacing.small))

            Text(
                text = "$blockedAppName is currently blocked.",
                style = FocusTypography.overlayMessage,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(FocusTheme.spacing.extraLarge))

            Button(
                onClick = onGoBack,
                shape = FocusComponentShapes.button
            ) {
                Text(
                    text = "Back to Focus",
                    style = FocusTypography.primaryButton
                )
            }
        }
    }
}