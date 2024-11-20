package com.example.focusmodes.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.focusmodes.MainActivity
import com.example.focusmodes.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationBlockerService : NotificationListenerService() {
    private var blockedApps = setOf<String>()
    private val NOTIFICATION_ID = 2
    private val CHANNEL_ID = "NotificationBlockerService"
    
    companion object {
        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

        fun hasNotificationAccess(context: Context): Boolean {
            val packageName = context.packageName
            val flat = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            )
            val hasAccess = flat?.contains(packageName) == true
            Log.d("NotificationBlockerService", "Checking notification access: $hasAccess")
            return hasAccess
        }
    }

    override fun onCreate() {
        super.onCreate()
        _isRunning.value = true
        Log.d("NotificationBlockerService", "Service created")
        createNotificationChannel()
        
        // Force reconnect the service
        toggleNotificationListenerService()
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        
        when (intent?.action) {
            "START_BLOCKING" -> {
                val modeName = intent.getStringExtra("mode_name") ?: "Unknown Mode"
                val blockedAppsList = intent.getStringArrayListExtra("blocked_apps") ?: ArrayList()
                
                Log.d("NotificationBlockerService", "Starting blocking for mode: $modeName")
                Log.d("NotificationBlockerService", "Received blocked apps: $blockedAppsList")
                
                startBlocking(blockedAppsList)
            }
            "STOP_BLOCKING" -> {
                Log.d("NotificationBlockerService", "Received stop blocking command")
                stopBlocking()
            }
        }
        
        return START_STICKY
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationBlockerService", "Listener connected")
        // Remove existing notifications from blocked apps
        removeExistingNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d("NotificationBlockerService", "Listener disconnected")
        // Try to reconnect
        requestRebind(ComponentName(this, NotificationBlockerService::class.java))
    }

    private fun toggleNotificationListenerService() {
        Log.d("NotificationBlockerService", "Toggling notification listener service")
        packageManager.setComponentEnabledSetting(
            ComponentName(this, NotificationBlockerService::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        packageManager.setComponentEnabledSetting(
            ComponentName(this, NotificationBlockerService::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun removeExistingNotifications() {
        if (!_isRunning.value) return
        
        try {
            val notifications = activeNotifications
            Log.d("NotificationBlockerService", "Current active notifications: ${notifications.size}")
            for (notification in notifications) {
                if (blockedApps.contains(notification.packageName)) {
                    cancelNotification(notification.key)
                    Log.d("NotificationBlockerService", "Removed existing notification from: ${notification.packageName}")
                }
            }
        } catch (e: Exception) {
            Log.e("NotificationBlockerService", "Error removing existing notifications", e)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.d("NotificationBlockerService", "onNotificationPosted called")
        Log.d("NotificationBlockerService", "Is blocking enabled: ${_isRunning.value}")
        
        if (!_isRunning.value) {
            Log.d("NotificationBlockerService", "Blocking is disabled, ignoring notification")
            return
        }
        
        val packageName = sbn.packageName
        Log.d("NotificationBlockerService", "Notification received from: $packageName")
        Log.d("NotificationBlockerService", "Current blocked apps: $blockedApps")
        
        if (blockedApps.contains(packageName)) {
            try {
                cancelNotification(sbn.key)
                Log.d("NotificationBlockerService", "Successfully blocked notification from: $packageName")
            } catch (e: Exception) {
                Log.e("NotificationBlockerService", "Error blocking notification", e)
            }
        } else {
            Log.d("NotificationBlockerService", "Package not in blocked list, allowing notification")
        }
    }

    override fun onDestroy() {
        Log.d("NotificationBlockerService", "Service being destroyed")
        super.onDestroy()
        stopBlocking()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        _isRunning.value = false
        
        // Add this line to ensure the service is properly disconnected
        requestRebind(ComponentName(this, NotificationBlockerService::class.java))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Blocker"
            val descriptionText = "Blocks notifications during focus mode"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Focus Mode Active")
        .setContentText("Blocking notifications from selected apps")
        .setSmallIcon(R.drawable.ic_notification)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        .build()

    private fun startBlocking(apps: List<String>) {
        Log.d("NotificationBlockerService", "Starting blocking with apps: $apps")
        blockedApps = apps.toSet()
        _isRunning.value = true
        
        // Remove any existing notifications
        removeExistingNotifications()
        
        // Force reconnect the service
        toggleNotificationListenerService()
    }

    private fun stopBlocking() {
        Log.d("NotificationBlockerService", "Stopping blocking")
        blockedApps = emptySet()
        _isRunning.value = false
        
        // Just toggle the service to reset its state
        toggleNotificationListenerService()
    }
} 