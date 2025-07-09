package com.github.adnanrangrej.focusmodes.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.github.adnanrangrej.focusmodes.ui.screens.modes.AppInfo
import com.github.adnanrangrej.focusmodes.ui.theme.FocusComponentShapes
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTheme
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTypography

@Composable
fun AppSelectorWindow(
    modifier: Modifier = Modifier,
    appList: List<AppInfo>,
    onDismissRequest: () -> Unit,
    onConfirm: (List<AppInfo>) -> Unit,
    selectedApps: List<AppInfo>
) {
    val currentSelectedApps = remember { mutableStateListOf<AppInfo>() }

    LaunchedEffect(selectedApps) {
        currentSelectedApps.clear()
        currentSelectedApps.addAll(selectedApps)
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Select Apps to Block",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                HorizontalDivider(modifier = Modifier.padding(bottom = FocusTheme.spacing.medium))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(), // Allow LazyColumn to take available space
                    verticalArrangement = Arrangement.spacedBy(FocusTheme.spacing.small)
                ) {
                    items(appList) { app ->
                        AppItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(FocusComponentShapes.buttonSmall)
                                .clickable {
                                    if (currentSelectedApps.contains(app)) {
                                        currentSelectedApps.remove(app)
                                    } else {
                                        currentSelectedApps.add(app)
                                    }
                                },
                            app = app,
                            isSelected = currentSelectedApps.contains(app),
                            onSelectedChange = { checked ->
                                if (checked) {
                                    currentSelectedApps.add(app)
                                } else {
                                    currentSelectedApps.remove(app)
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(currentSelectedApps) }
            ) {
                Text("Done (${currentSelectedApps.size})")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismissRequest
            ) {
                Text("Cancel")
            }
        },
        modifier = modifier,
        shape = FocusComponentShapes.dialog,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = FocusTheme.elevation.large
    )
}

@Composable
fun AppItem(
    modifier: Modifier = Modifier,
    app: AppInfo,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        label = "AppItemBackgroundColor"
    )

    Row(
        modifier = modifier
            .background(backgroundColor, shape = FocusComponentShapes.buttonSmall)
            .padding(FocusTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FocusTheme.spacing.medium)
    ) {
        // Icon
        val bitmap = remember(app.appIcon) {
            app.appIcon.toBitmap().asImageBitmap()
        }

        Image(
            bitmap = bitmap,
            contentDescription = "App icon for ${app.appName}",
            modifier = Modifier
                .size(40.dp)
                .clip(FocusComponentShapes.buttonSmall)
        )

        // Text
        Text(
            text = app.appName,
            style = FocusTypography.blockedAppName,
            modifier = Modifier.weight(1f)
        )

        // Checkbox
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectedChange
        )
    }
}