package com.example.focusmodes.ui.viewmodels

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.focusmodes.data.model.AppInfo
import com.example.focusmodes.data.model.FocusMode
import com.example.focusmodes.service.AppBlockerService
import com.example.focusmodes.service.NotificationBlockerService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FocusModesViewModel(application: Application) : AndroidViewModel(application) {
    private val _focusModes = MutableStateFlow<List<FocusMode>>(emptyList())
    val focusModes: StateFlow<List<FocusMode>> = _focusModes.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _activeMode = MutableStateFlow<FocusMode?>(null)
    val activeMode: StateFlow<FocusMode?> = _activeMode.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val sharedPreferences = application.getSharedPreferences(
        "focus_modes_prefs",
        Context.MODE_PRIVATE
    )

    init {
        loadPredefinedModes()
        loadCustomModes()
        loadInstalledApps()
        restoreActiveMode()
        viewModelScope.launch {
            combine(
                AppBlockerService.isRunning,
                NotificationBlockerService.isRunning
            ) { appBlockerRunning, notificationBlockerRunning ->
                appBlockerRunning && notificationBlockerRunning
            }.collect { servicesRunning ->
                if (!servicesRunning && _activeMode.value != null) {
                    // Services not running but UI shows active mode
                    _activeMode.value = null
                    sharedPreferences.edit().remove("active_mode_id").apply()
                } else if (servicesRunning && _activeMode.value == null) {
                    // Services running but UI shows no active mode
                    restoreActiveMode()
                }
            }
        }
    }

    private fun loadPredefinedModes() {
        val predefinedModes = listOf(
            FocusMode(
                id = 1,
                name = "Deep Work",
                duration = 50,
                blockedApps = listOf(
                    "com.whatsapp",
                    "com.instagram.android",
                    "com.facebook.katana",
                    "com.twitter.android",
                    "com.snapchat.android",
                    "com.linkedin.android",
                    "com.google.android.youtube",
                    "com.netflix.mediaclient",
                    "com.spotify.music",
                    "com.supercell.clashofclans",
                    "com.king.candycrushsaga",
                    "com.google.android.gm",
                    "com.microsoft.office.outlook",
                    "com.google.android.apps.messaging",
                    "com.android.dialer",
                    "com.amazon.mShop.android.shopping",
                    "com.ebay.mobile",
                    "com.reddit.frontpage"
                ),
                description = "Ultimate distraction-free zone for maximum focus. Blocks all non-essential apps and notifications for deep, uninterrupted work sessions."
            ),
            FocusMode(
                id = 2,
                name = "Study Time",
                duration = 25,
                blockedApps = listOf(
                    "com.whatsapp",
                    "com.instagram.android",
                    "com.facebook.katana",
                    "com.twitter.android",
                    "com.snapchat.android",
                    "com.netflix.mediaclient",
                    "com.spotify.music",
                    "com.google.android.youtube",
                    "com.supercell.clashofclans",
                    "com.king.candycrushsaga",
                    "com.amazon.mShop.android.shopping",
                    "com.ebay.mobile"
                ),
                description = "Balanced study environment that blocks distractions while keeping educational resources accessible. Perfect for focused learning sessions."
            ),
            FocusMode(
                id = 3,
                name = "Reading",
                duration = 30,
                blockedApps = listOf(
                    "com.whatsapp",
                    "com.instagram.android",
                    "com.facebook.katana",
                    "com.twitter.android",
                    "com.snapchat.android",
                    "com.linkedin.android",
                    "com.google.android.youtube",
                    "com.netflix.mediaclient",
                    "com.spotify.music",
                    "com.supercell.clashofclans",
                    "com.king.candycrushsaga",
                    "com.google.android.gm",
                    "com.microsoft.office.outlook",
                    "com.google.android.apps.messaging",
                    "com.amazon.mShop.android.shopping",
                    "com.ebay.mobile",
                    "com.reddit.frontpage"
                ),
                description = "Create a peaceful reading sanctuary by blocking all distracting apps. Perfect for immersive reading sessions and literature focus."
            )
        )

        _focusModes.value = predefinedModes + loadCustomModes()

        val activeModeId = sharedPreferences.getInt("active_mode_id", -1)
        if (activeModeId != -1) {
            _activeMode.value = _focusModes.value.find { it.id == activeModeId }
        }
    }

    private fun loadCustomModes(): List<FocusMode> {
        val customModesJson = sharedPreferences.getString("custom_modes", null)
        return if (customModesJson != null) {
            try {
                val type = object : TypeToken<List<FocusMode>>() {}.type
                Gson().fromJson(customModesJson, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    private fun saveCustomModes(customModes: List<FocusMode>) {
        val json = Gson().toJson(customModes)
        sharedPreferences.edit()
            .putString("custom_modes", json)
            .apply()
        
        // Log the saved custom modes for debugging
        Log.d("FocusModesViewModel", "Saving custom modes: $customModes")
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val pm = getApplication<Application>().packageManager
                val mainIntent = Intent(Intent.ACTION_MAIN, null)
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                
                val apps = pm.queryIntentActivities(mainIntent, 0)
                    .asSequence()
                    .mapNotNull { resolveInfo ->
                        try {
                            val packageName = resolveInfo.activityInfo.packageName
                            // Skip our own app
                            if (packageName == getApplication<Application>().packageName) {
                                return@mapNotNull null
                            }
                            
                            val appInfo = pm.getApplicationInfo(packageName, 0)
                            // Skip system apps
                            if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
                                return@mapNotNull null
                            }
                            
                            AppInfo(
                                packageName = packageName,
                                appName = pm.getApplicationLabel(appInfo).toString(),
                                icon = pm.getApplicationIcon(packageName)
                            )
                        } catch (e: Exception) {
                            Log.e("FocusModesViewModel", "Error loading app info", e)
                            null
                        }
                    }
                    .sortedBy { it.appName }
                    .toList()

                withContext(Dispatchers.Main) {
                    _installedApps.value = apps
                    Log.d("FocusModesViewModel", "Loaded ${apps.size} apps")
                }
            } catch (e: Exception) {
                Log.e("FocusModesViewModel", "Error loading apps", e)
                withContext(Dispatchers.Main) {
                    _installedApps.value = emptyList()
                }
            }
        }
    }

    private fun isSystemApp(packageName: String): Boolean {
        try {
            val pm = getApplication<Application>().packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            return (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        } catch (e: Exception) {
            return true
        }
    }

    fun checkPermissions() {
        val context = getApplication<Application>()
        viewModelScope.launch {
            // Check notification listener permission first
            if (!NotificationBlockerService.hasNotificationAccess(context)) {
                _uiEvent.emit(UiEvent.ShowPermissionDialog(
                    title = "Notification Access Permission",
                    message = "This permission is required to block notifications from restricted apps during focus mode. Please enable notification access for Focus Modes.",
                    onConfirm = {
                        viewModelScope.launch {
                            _uiEvent.emit(UiEvent.RequestNotificationPermission)
                        }
                    }
                ))
                return@launch
            }

            // Check "Display over other apps" permission first
            if (!Settings.canDrawOverlays(context)) {
                _uiEvent.emit(UiEvent.ShowPermissionDialog(
                    title = "Display Over Other Apps Permission",
                    message = "This permission is required to show blocking overlay when you try to open restricted apps. Please enable 'Display over other apps' permission for Focus Modes.",
                    onConfirm = {
                        viewModelScope.launch {
                            _uiEvent.emit(UiEvent.RequestOverlayPermission)
                        }
                    }
                ))
                return@launch
            }

            // Then check usage stats permission
            if (!AppBlockerService.hasUsageStatsPermission(context)) {
                _uiEvent.emit(UiEvent.ShowPermissionDialog(
                    title = "Usage Access Permission",
                    message = "This permission is required to detect when you open restricted apps. Please enable 'Usage access' permission for Focus Modes.",
                    onConfirm = {
                        viewModelScope.launch {
                            _uiEvent.emit(UiEvent.RequestUsageStatsPermission)
                        }
                    }
                ))
                return@launch
            }
        }
    }

    fun activateFocusMode(focusMode: FocusMode) {
        viewModelScope.launch {
            if (checkAllPermissions()) {
                // Log the focus mode being activated
                Log.d("FocusModesViewModel", "Activating focus mode: ${focusMode.name}")
                Log.d("FocusModesViewModel", "Blocked apps list: ${focusMode.blockedApps}")
                
                // Set active mode immediately for UI responsiveness
                _activeMode.value = focusMode
                
                // Start AppBlockerService
                val context = getApplication<Application>()
                val appBlockerIntent = Intent(context, AppBlockerService::class.java).apply {
                    action = "START_BLOCKING"  // Add an action
                    putExtra("mode_name", focusMode.name)  // Add mode name for logging
                    val blockedAppsList = ArrayList(focusMode.blockedApps)
                    putStringArrayListExtra("blocked_apps", blockedAppsList)
                }
                
                // Log the intent extras for debugging
                Log.d("FocusModesViewModel", "Starting AppBlockerService with blocked apps: ${
                    appBlockerIntent.getStringArrayListExtra("blocked_apps")
                }")
                
                context.startService(appBlockerIntent)
                
                // Start NotificationBlockerService
                val notificationBlockerIntent = Intent(context, NotificationBlockerService::class.java).apply {
                    action = "START_BLOCKING"  // Add an action
                    putExtra("mode_name", focusMode.name)  // Add mode name for logging
                    val blockedAppsList = ArrayList(focusMode.blockedApps)
                    putStringArrayListExtra("blocked_apps", blockedAppsList)
                }
                
                // Log the intent extras for debugging
                Log.d("FocusModesViewModel", "Starting NotificationBlockerService with blocked apps: ${
                    notificationBlockerIntent.getStringArrayListExtra("blocked_apps")
                }")
                
                context.startService(notificationBlockerIntent)

                // Save active mode to preferences
                sharedPreferences.edit()
                    .putInt("active_mode_id", focusMode.id)
                    .apply()
            }
        }
    }

    private suspend fun checkAllPermissions(): Boolean {
        val context = getApplication<Application>()
        
        if (!NotificationBlockerService.hasNotificationAccess(context)) {
            _uiEvent.emit(UiEvent.ShowPermissionDialog(
                title = "Notification Access Required",
                message = "This permission is required to block notifications from restricted apps.",
                onConfirm = {
                    viewModelScope.launch {
                        _uiEvent.emit(UiEvent.RequestNotificationPermission)
                    }
                }
            ))
            return false
        }

        if (!Settings.canDrawOverlays(context)) {
            _uiEvent.emit(UiEvent.ShowPermissionDialog(
                title = "Display Over Other Apps Permission Required",
                message = "This permission is required to show blocking overlay.",
                onConfirm = {
                    viewModelScope.launch {
                        _uiEvent.emit(UiEvent.RequestOverlayPermission)
                    }
                }
            ))
            return false
        }

        if (!AppBlockerService.hasUsageStatsPermission(context)) {
            _uiEvent.emit(UiEvent.ShowPermissionDialog(
                title = "Usage Access Permission Required",
                message = "This permission is required to detect restricted apps.",
                onConfirm = {
                    viewModelScope.launch {
                        _uiEvent.emit(UiEvent.RequestUsageStatsPermission)
                    }
                }
            ))
            return false
        }

        return true
    }

    fun deactivateFocusMode() {
        viewModelScope.launch {
            // Update UI state immediately
            _activeMode.value = null
            
            // Stop services
            val context = getApplication<Application>()
            
            // Stop AppBlockerService
            val appBlockerIntent = Intent(context, AppBlockerService::class.java)
            context.stopService(appBlockerIntent)
            
            // Stop NotificationBlockerService with explicit stop action
            val notificationBlockerIntent = Intent(context, NotificationBlockerService::class.java).apply {
                action = "STOP_BLOCKING"
            }
            context.startService(notificationBlockerIntent) // Send stop command
            
            // Force a reconnect of the NotificationListenerService to clear its state
            val componentName = ComponentName(context, NotificationBlockerService::class.java)
            context.packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
            
            // Small delay to ensure the disable takes effect
            delay(100)
            
            context.packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            
            // Clear active mode from preferences
            sharedPreferences.edit().remove("active_mode_id").apply()
        }
    }

    private fun restoreActiveMode() {
        val activeModeId = sharedPreferences.getInt("active_mode_id", -1)
        if (activeModeId != -1) {
            _focusModes.value.find { it.id == activeModeId }?.let { mode ->
                _activeMode.value = mode
            }
        }
    }

    fun addCustomMode(focusMode: FocusMode) {
        viewModelScope.launch {
            val customModes = _focusModes.value.filter { it.isCustom }
            val newMode = focusMode.copy(
                id = (_focusModes.value.maxOfOrNull { it.id } ?: 0) + 1,
                isCustom = true  // Ensure isCustom is set to true
            )
            
            // Log the new mode being added
            Log.d("FocusModesViewModel", "Adding new custom mode: $newMode")
            Log.d("FocusModesViewModel", "Blocked apps in new mode: ${newMode.blockedApps}")

            val updatedModes = _focusModes.value.filterNot { it.isCustom } + 
                              (customModes + newMode)
            _focusModes.value = updatedModes
            saveCustomModes(updatedModes.filter { it.isCustom })
        }
    }

    // Add Factory class
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FocusModesViewModel::class.java)) {
                return FocusModesViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}