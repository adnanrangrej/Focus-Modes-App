package com.example.focusmodes.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.focusmodes.data.model.AppInfo
import com.example.focusmodes.data.model.FocusMode


@Composable
fun CreateCustomModeDialog(
    installedApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onConfirm: (FocusMode) -> Unit
) {
    var modeName by remember { mutableStateOf("") }
    var focusTime by remember { mutableStateOf("") }
    var breakTime by remember { mutableStateOf("") }
    var selectedApps by remember { mutableStateOf(emptySet<String>()) }
    var showAppSelector by remember { mutableStateOf(false) }

    if (showAppSelector) {
        AppSelectorDialog(
            installedApps = installedApps,
            selectedApps = selectedApps,
            onDismiss = { showAppSelector = false },
            onConfirm = { apps ->
                selectedApps = apps
                showAppSelector = false
                Log.d("CreateCustomModeDialog", "Selected apps: $apps")
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Custom Mode") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = modeName,
                    onValueChange = { modeName = it },
                    label = { Text("Mode Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = focusTime,
                    onValueChange = { focusTime = it },
                    label = { Text("Focus Time (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = breakTime,
                    onValueChange = { breakTime = it },
                    label = { Text("Break Time (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { showAppSelector = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Apps to Block (${selectedApps.size} selected)")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    Log.d("CreateCustomModeDialog", "Selected apps for blocking: $selectedApps")
                    
                    val newMode = FocusMode(
                        id = 0,
                        name = modeName,
                        duration = focusTime.toIntOrNull() ?: 25,
                        blockedApps = selectedApps.toList(),
                        description = "Custom mode for focused $modeName sessions",
                        isCustom = true
                    )
                    
                    Log.d("CreateCustomModeDialog", "Created new mode: $newMode")
                    Log.d("CreateCustomModeDialog", "Blocked apps in new mode: ${newMode.blockedApps}")
                    
                    onConfirm(newMode)
                },
                enabled = modeName.isNotBlank() && 
                         focusTime.toIntOrNull() != null && 
                         selectedApps.isNotEmpty()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AppSelectorDialog(
    installedApps: List<AppInfo>,
    selectedApps: Set<String>,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit
) {
    var currentSelection by remember { mutableStateOf(selectedApps) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Apps to Block") },
        text = {
            if (installedApps.isEmpty()) {
                Text("Loading apps...")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(installedApps) { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentSelection = if (currentSelection.contains(app.packageName)) {
                                        currentSelection - app.packageName
                                    } else {
                                        currentSelection + app.packageName
                                    }
                                }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = currentSelection.contains(app.packageName),
                                onCheckedChange = { checked ->
                                    currentSelection = if (checked) {
                                        currentSelection + app.packageName
                                    } else {
                                        currentSelection - app.packageName
                                    }
                                }
                            )

                            // Convert Drawable to ImageBitmap safely
                            val bitmap = remember(app.icon) {
                                app.icon.toBitmap().asImageBitmap()
                            }
                            
                            Image(
                                bitmap = bitmap,
                                contentDescription = "App icon for ${app.appName}",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline,
                                        shape = MaterialTheme.shapes.small
                                    )
                            )

                            Text(
                                text = app.appName,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(currentSelection) },
                enabled = installedApps.isNotEmpty()
            ) {
                Text("Done (${currentSelection.size} selected)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 