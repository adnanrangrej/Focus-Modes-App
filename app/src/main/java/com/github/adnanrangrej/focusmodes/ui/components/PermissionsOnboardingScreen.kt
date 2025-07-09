package com.github.adnanrangrej.focusmodes.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.adnanrangrej.focusmodes.ui.theme.FocusComponentShapes
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTheme
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTypography

@Composable
fun PermissionsOnboardingScreen(
    modifier: Modifier = Modifier,
    isNotificationPermissionGranted: Boolean,
    onNotificationPermissionGranted: () -> Unit,
    isAccessibilityEnabled: Boolean,
    onAccessibilityEnabled: () -> Unit,
    canDrawOverlay: Boolean,
    onCanDrawOverlay: () -> Unit,
    isAutoStartAvailable: Boolean,
    onAutoStartClick: () -> Unit,
    hasBeenGuidedToAutostart: Boolean
) {

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(FocusTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Permissions Shield",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(Modifier.height(FocusTheme.spacing.large))

            Text(
                text = "One Last Step!",
                style = FocusTypography.overlayTitle,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(FocusTheme.spacing.medium))

            Text(
                text = "To provide the best focus experience, this app needs a few permissions to work correctly.",
                style = FocusTypography.permissionDescription,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = FocusTheme.spacing.medium)
            )

            Spacer(Modifier.height(FocusTheme.spacing.huge))

            Column(verticalArrangement = Arrangement.spacedBy(FocusTheme.spacing.medium)) {
                PermissionChecklistItem(
                    icon = Icons.Default.Notifications,
                    title = "Notification Permission",
                    subtitle = "Required to show the timer in a notification.",
                    isGranted = isNotificationPermissionGranted,
                    onClick = onNotificationPermissionGranted
                )

                PermissionChecklistItem(
                    icon = Icons.Default.TouchApp,
                    title = "Accessibility Service",
                    subtitle = "Required to detect which app you have opened.",
                    isGranted = isAccessibilityEnabled,
                    onClick = onAccessibilityEnabled
                )

                PermissionChecklistItem(
                    icon = Icons.Default.Visibility,
                    title = "Display Over Other Apps",
                    subtitle = "Required to show the blocking screen.",
                    isGranted = canDrawOverlay,
                    onClick = onCanDrawOverlay
                )

                if (isAutoStartAvailable) {
                    PermissionChecklistItem(
                        icon = Icons.Default.Autorenew,
                        title = "Enable Autostart",
                        subtitle = "Needed for timers to work reliably on your device.",
                        isGranted = hasBeenGuidedToAutostart,
                        onClick = onAutoStartClick
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionChecklistItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isGranted: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isGranted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceContainerHigh,
        label = "BackgroundColorAnimation"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isGranted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "ContentColorAnimation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(FocusComponentShapes.focusModeCard)
            .clickable(enabled = !isGranted, onClick = onClick)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
                FocusComponentShapes.focusModeCard
            ),
        color = backgroundColor,
        tonalElevation = FocusTheme.elevation.small
    ) {
        Row(
            modifier = Modifier.padding(FocusTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FocusTheme.spacing.medium)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    style = FocusTypography.cardSubtitle,
                    color = contentColor
                )
            }
            if (isGranted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Granted",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Grant Permission",
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}