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
        modifier = modifier
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "One Last Step!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))
            Text(
                text = "To provide the best focus experience, this app needs a few permissions to work correctly.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(48.dp))

            PermissionChecklistItem(
                icon = Icons.Default.Notifications,
                title = "Notification Permission",
                subtitle = "Required to show the timer in a notification.",
                isGranted = isNotificationPermissionGranted,
                onClick = onNotificationPermissionGranted
            )

            Spacer(Modifier.height(16.dp))

            PermissionChecklistItem(
                icon = Icons.Default.TouchApp,
                title = "Accessibility Service",
                subtitle = "Required to detect which app you have opened.",
                isGranted = isAccessibilityEnabled,
                onClick = onAccessibilityEnabled
            )

            Spacer(Modifier.height(16.dp))

            PermissionChecklistItem(
                icon = Icons.Default.Visibility,
                title = "Display Over Other Apps",
                subtitle = "Required to show the blocking screen.",
                isGranted = canDrawOverlay,
                onClick = onCanDrawOverlay
            )

            if (isAutoStartAvailable) {
                Spacer(Modifier.height(16.dp))
                PermissionChecklistItem(
                    icon = Icons.Default.Autorenew,
                    title = "Enable Autostart",
                    subtitle = "Needed for timers to work reliably on your device.",
                    // We can't check this, so we assume it's not granted until clicked.
                    isGranted = hasBeenGuidedToAutostart,
                    onClick = onAutoStartClick
                )
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
        targetValue = if (isGranted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant,
        label = "BackgroundColorAnimation"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isGranted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "ContentColorAnimation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(enabled = !isGranted, onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                    style = MaterialTheme.typography.bodyMedium,
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