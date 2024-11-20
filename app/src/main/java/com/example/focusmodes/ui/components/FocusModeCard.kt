package com.example.focusmodes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.focusmodes.data.model.AppInfo
import com.example.focusmodes.data.model.FocusMode

@Composable
fun FocusModeCard(
    focusMode: FocusMode,
    isActive: Boolean,
    installedApps: List<AppInfo>,
    onActivate: () -> Unit,
    onDeactivate: () -> Unit,
    onClick: () -> Unit
) {
    val actualBlockedAppsCount = installedApps.count { app ->
        focusMode.blockedApps.contains(app.packageName)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray.copy(alpha = 0.95f)
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = focusMode.name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            
            Text(
                text = focusMode.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$actualBlockedAppsCount Apps Blocked",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                
                Switch(
                    checked = isActive,
                    onCheckedChange = { checked ->
                        if (checked) onActivate() else onDeactivate()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.Red,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.DarkGray
                    )
                )
            }
        }
    }
} 