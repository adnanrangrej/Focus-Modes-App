package com.github.adnanrangrej.focusmodes

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.adnanrangrej.focusmodes.service.AppBlockerService
import com.github.adnanrangrej.focusmodes.ui.components.PermissionsOnboardingScreen
import com.github.adnanrangrej.focusmodes.ui.navigation.MainDisplay
import com.github.adnanrangrej.focusmodes.ui.utils.AutoStartPermissionHelper
import com.github.adnanrangrej.focusmodes.ui.utils.canDrawOverlays
import com.github.adnanrangrej.focusmodes.ui.utils.isAccessibilityServiceEnabled
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FocusModesApp() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val prefs = remember {
        context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
    }
    val guidedAutostartKey = "has_been_guided_to_autostart"

    // --- State of all three permissions ---
    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else {
        remember {
            object : PermissionState {
                override val permission: String = "POST_NOTIFICATIONS"
                override val status: PermissionStatus = PermissionStatus.Granted
                override fun launchPermissionRequest() {}
            }
        }
    }

    var isAccessibilityServiceEnable by remember {
        mutableStateOf(isAccessibilityServiceEnabled(context, AppBlockerService::class.java))
    }

    var canDrawOverlay by remember { mutableStateOf(canDrawOverlays(context)) }

    val autoStartHelper = remember { AutoStartPermissionHelper.getInstance() }
    val isAutoStartAvailable = remember { autoStartHelper.isAutoStartPermissionAvailable(context) }
    // We can't check if it's granted, so we'll just track if the user has been shown the prompt.
    var hasBeenGuidedToAutostart by remember {
        mutableStateOf(
            prefs.getBoolean(
                guidedAutostartKey,
                false
            )
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isAccessibilityServiceEnable =
                    isAccessibilityServiceEnabled(context, AppBlockerService::class.java)

                canDrawOverlay = canDrawOverlays(context)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }

    }

    val allPermissionsGranted =
        notificationPermissionState.status.isGranted && isAccessibilityServiceEnable && canDrawOverlay && (!isAutoStartAvailable || hasBeenGuidedToAutostart)

    if (allPermissionsGranted) {
        MainDisplay(Modifier.fillMaxSize())
    } else {
        PermissionsOnboardingScreen(
            modifier = Modifier.fillMaxSize(),
            isNotificationPermissionGranted = notificationPermissionState.status.isGranted,
            onNotificationPermissionGranted = { notificationPermissionState.launchPermissionRequest() },
            isAccessibilityEnabled = isAccessibilityServiceEnable,
            onAccessibilityEnabled = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                context.startActivity(intent)
            },
            canDrawOverlay = canDrawOverlay,
            onCanDrawOverlay = {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    "package:${context.packageName}".toUri()
                )
                context.startActivity(intent)
            },
            isAutoStartAvailable = isAutoStartAvailable,
            onAutoStartClick = {
                autoStartHelper.getAutoStartPermission(context)
                prefs.edit { putBoolean(guidedAutostartKey, true) }
                hasBeenGuidedToAutostart = true
            },
            hasBeenGuidedToAutostart = hasBeenGuidedToAutostart
        )
    }
}