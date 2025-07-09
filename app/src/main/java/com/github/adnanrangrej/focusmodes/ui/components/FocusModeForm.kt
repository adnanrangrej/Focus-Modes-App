package com.github.adnanrangrej.focusmodes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.adnanrangrej.focusmodes.ui.screens.modes.AppInfo
import com.github.adnanrangrej.focusmodes.ui.theme.FocusComponentShapes
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTheme
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTypography

@Composable
fun FocusModeForm(
    modifier: Modifier = Modifier,
    name: String,
    workingTime: String,
    breakTime: String,
    description: String,
    canCreateMode: Boolean,
    installedApps: List<AppInfo>,
    selectedApps: List<AppInfo> = emptyList(),
    onNameChange: (String) -> Unit,
    onWorkingTimeChange: (String) -> Unit,
    onBreakTimeChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAppChange: (List<AppInfo>) -> Unit,
    onConfirm: () -> Unit,
    confirmButtonText: String = "Create New Focus Mode"
) {
    var isAppSelectorOpen by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(FocusTheme.spacing.medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(FocusTheme.spacing.medium)
        ) {
            // --- Focus Mode Name ---
            Text(
                text = "Focus Mode Name",
                style = MaterialTheme.typography.titleSmall
            )
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("e.g., Deep Work") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(FocusTheme.spacing.small))

            // --- Work and Break Time ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FocusTheme.spacing.medium)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(FocusTheme.spacing.small)
                ) {
                    Text(
                        text = "Work Time (min)",
                        style = MaterialTheme.typography.titleSmall
                    )
                    OutlinedTextField(
                        value = workingTime,
                        onValueChange = onWorkingTimeChange,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(FocusTheme.spacing.small)
                ) {
                    Text(
                        text = "Break Time (min)",
                        style = MaterialTheme.typography.titleSmall
                    )
                    OutlinedTextField(
                        value = breakTime,
                        onValueChange = onBreakTimeChange,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(FocusTheme.spacing.small))

            // --- App Selection ---
            Button(
                onClick = { isAppSelectorOpen = true },
                shape = FocusComponentShapes.button,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Select Apps to Block (${selectedApps.size} selected)",
                    style = FocusTypography.primaryButton
                )
            }

            Spacer(Modifier.height(FocusTheme.spacing.small))

            // --- Description ---
            Text(
                text = "Description (Optional)",
                style = FocusTypography.focusModeTitle
            )
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("What is this mode for?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                singleLine = false
            )

            // Spacer to push confirm button to the bottom
            Spacer(Modifier.weight(1f))

            // --- Confirm Button ---
            Button(
                onClick = onConfirm,
                enabled = canCreateMode,
                shape = FocusComponentShapes.button,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = confirmButtonText,
                    style = FocusTypography.primaryButton
                )
            }
        }

        if (isAppSelectorOpen) {
            AppSelectorWindow(
                modifier = Modifier
                    .padding(FocusTheme.spacing.medium)
                    .align(Alignment.Center),
                appList = installedApps,
                onDismissRequest = { isAppSelectorOpen = false },
                onConfirm = { appList ->
                    onAppChange(appList)
                    isAppSelectorOpen = false
                },
                selectedApps = selectedApps
            )
        }
    }
}