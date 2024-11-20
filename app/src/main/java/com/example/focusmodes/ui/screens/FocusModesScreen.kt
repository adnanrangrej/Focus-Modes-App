package com.example.focusmodes.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.focusmodes.data.model.FocusMode
import com.example.focusmodes.ui.components.BlockedAppsDialog
import com.example.focusmodes.ui.components.CreateCustomModeDialog
import com.example.focusmodes.ui.components.FocusModeCard
import com.example.focusmodes.ui.components.PermissionDialog
import com.example.focusmodes.ui.viewmodels.FocusModesViewModel
import com.example.focusmodes.ui.viewmodels.UiEvent

@Composable
fun FocusModesScreen(
    viewModel: FocusModesViewModel,
    onNavigateToPomodoro: () -> Unit
) {
    val context = LocalContext.current
    val focusModes by viewModel.focusModes.collectAsState()
    val activeMode by viewModel.activeMode.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf<UiEvent.ShowPermissionDialog?>(null) }
    var selectedMode by remember { mutableStateOf<FocusMode?>(null) }

    LaunchedEffect(Unit) {
        viewModel.checkPermissions()
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowPermissionDialog -> {
                    showPermissionDialog = event
                }
                is UiEvent.RequestNotificationPermission -> {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    context.startActivity(intent)
                }
                is UiEvent.RequestOverlayPermission -> {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                }
                is UiEvent.RequestUsageStatsPermission -> {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    context.startActivity(intent)
                }
                is UiEvent.NavigateToPomodoro -> {
                    onNavigateToPomodoro()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Create Custom Mode")
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(focusModes) { focusMode ->
                FocusModeCard(
                    focusMode = focusMode,
                    isActive = activeMode?.id == focusMode.id,
                    installedApps = installedApps,
                    onActivate = { viewModel.activateFocusMode(focusMode) },
                    onDeactivate = { viewModel.deactivateFocusMode() },
                    onClick = { selectedMode = focusMode }
                )
            }
        }

        if (showCreateDialog) {
            CreateCustomModeDialog(
                installedApps = installedApps,
                onDismiss = { showCreateDialog = false },
                onConfirm = { newMode ->
                    viewModel.addCustomMode(newMode)
                    showCreateDialog = false
                }
            )
        }

        showPermissionDialog?.let { event ->
            PermissionDialog(
                title = event.title,
                message = event.message,
                onDismiss = { showPermissionDialog = null },
                onConfirm = {
                    event.onConfirm()
                    showPermissionDialog = null
                }
            )
        }

        selectedMode?.let { mode ->
            BlockedAppsDialog(
                focusMode = mode,
                installedApps = installedApps,
                onDismiss = { selectedMode = null }
            )
        }
    }
} 