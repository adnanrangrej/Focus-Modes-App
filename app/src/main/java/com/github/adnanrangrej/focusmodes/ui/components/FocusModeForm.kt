package com.github.adnanrangrej.focusmodes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
    onConfirm: () -> Unit
) {
    var isAppSelectorOpen by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Focus Mode Name"
            )
            TextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = {
                    Text("Enter a name")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Working Time"
                    )
                    TextField(
                        value = workingTime,
                        onValueChange = onWorkingTimeChange,
                        placeholder = {
                            Text("Enter a time")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Break Time"
                    )
                    TextField(
                        value = breakTime,
                        onValueChange = onBreakTimeChange,
                        placeholder = {
                            Text("Enter a time")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { isAppSelectorOpen = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Apps to Block (${selectedApps.size} selected)")
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Description"
            )
            TextField(
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = {
                    Text("Enter a description")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3
            )
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    onConfirm()
                },
                enabled = canCreateMode,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create New Focus Mode")
            }
        }

        if (isAppSelectorOpen) {
            AppSelectorWindow(
                modifier = Modifier
                    .heightIn(max = 400.dp)
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