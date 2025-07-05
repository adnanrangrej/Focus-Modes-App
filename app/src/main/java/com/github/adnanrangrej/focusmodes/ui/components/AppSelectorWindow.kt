package com.github.adnanrangrej.focusmodes.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.github.adnanrangrej.focusmodes.ui.screens.modes.AppInfo

@OptIn(ExperimentalMaterial3Api::class)
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
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        content = {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(appList) { app ->
                            AppItem(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Button(
                            onClick = onDismissRequest,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = { onConfirm(currentSelectedApps) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Done ${currentSelectedApps.size} selected")
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun AppItem(
    modifier: Modifier = Modifier,
    app: AppInfo,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // Checkbox
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectedChange
        )

        // Icon
        // Convert Drawable to Bitmap
        val bitmap = remember(app.appIcon) {
            app.appIcon.toBitmap().asImageBitmap()
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

        // Text
        Text(
            text = app.appName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}