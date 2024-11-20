package com.example.focusmodes.service

import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.focusmodes.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppBlockerService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var blockedApps = setOf<String>()
    private var isBlocking = false

    private val _blockingState = MutableStateFlow(false)
    val blockingState: StateFlow<Boolean> = _blockingState.asStateFlow()

    private val CHANNEL_ID = "FocusModeService"
    private val NOTIFICATION_ID = 1

    private var windowManager: WindowManager? = null
    private var blockingView: View? = null

    private var isHandlingBlock = false
    private var lastBlockedTime = 0L
    private var lastBlockedApp: String? = null
    private var blockingInProgress = false

    companion object {
        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

        fun hasUsageStatsPermission(context: Context): Boolean {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val mode = appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                )
                return mode == AppOpsManager.MODE_ALLOWED
            } else {
                @Suppress("DEPRECATION")
                val mode = appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                )
                return mode == AppOpsManager.MODE_ALLOWED
            }
        }

        fun openUsageAccessSettings(context: Context) {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate() {
        super.onCreate()
        _isRunning.value = true
        createNotificationChannel()
        startForegroundService()
        startBlockingCheck()
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun startForegroundService() {
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        
        if (intent?.action == "START_BLOCKING") {
            val modeName = intent.getStringExtra("mode_name") ?: "Unknown Mode"
            val blockedAppsList = intent.getStringArrayListExtra("blocked_apps") ?: ArrayList()
            
            Log.d("AppBlockerService", "Starting blocking for mode: $modeName")
            Log.d("AppBlockerService", "Received blocked apps: $blockedAppsList")
            
            startBlocking(blockedAppsList)
        }
        
        return START_STICKY
    }

    private fun startBlocking(apps: List<String>) {
        blockedApps = apps.toSet()
        isBlocking = true
        _blockingState.value = true
        Log.d("AppBlockerService", "Started blocking. Apps: $blockedApps")
    }

    fun stopBlocking() {
        isBlocking = false
        blockedApps = emptySet()
        _blockingState.value = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startBlockingCheck() {
        serviceScope.launch(Dispatchers.Default) {
            while (isActive) {
                if (isBlocking && !isHandlingBlock) {
                    withContext(Dispatchers.Main) {
                        checkAndBlockApps()
                    }
                }
                delay(1000) // Check every second
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAndBlockApps() {
        if (blockingInProgress) return

        val currentApp = getForegroundApp()
        val currentTime = System.currentTimeMillis()
        
        if (blockedApps.contains(currentApp) && 
            (currentTime - lastBlockedTime > 2000) && 
            currentApp != lastBlockedApp) {
            
            Log.d("AppBlockerService", "Blocking app: $currentApp")
            blockingInProgress = true
            lastBlockedTime = currentTime
            lastBlockedApp = currentApp
            
            Handler(Looper.getMainLooper()).post {
                try {
                    // Show overlay
                    showBlockingOverlay()
                    
                    // Clear recent tasks for blocked app
                    val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
                    if (currentApp != null) {
                        activityManager.appTasks.forEach { task ->
                            if (task.taskInfo.baseActivity?.packageName == currentApp) {
                                task.finishAndRemoveTask()
                            }
                        }
                    }
                    
                    // Return to home after a short delay
                    Handler(Looper.getMainLooper()).postDelayed({
                        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                   Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or
                                   Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                   Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(homeIntent)
                        
                        // Hide overlay and reset flags after returning home
                        Handler(Looper.getMainLooper()).postDelayed({
                            hideBlockingOverlay()
                            blockingInProgress = false
                            lastBlockedApp = null
                        }, 2000)
                    }, 500)
                } catch (e: Exception) {
                    Log.e("AppBlockerService", "Error in blocking sequence", e)
                    blockingInProgress = false
                    lastBlockedApp = null
                }
            }
        }
    }

    private fun getForegroundApp(): String? {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            time - 1000 * 1000,
            time
        )
        if (stats.isEmpty()) {
            Log.d("AppBlockerService", "No usage stats available")
            return null
        }
        return stats.maxByOrNull { it.lastTimeUsed }?.packageName
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Mode Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps focus mode active"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Focus Mode Active")
            .setContentText("Blocking distracting apps")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showBlockingOverlay() {
        try {
            // First, clean up any existing view
            hideBlockingOverlay()

            // Initialize window manager if needed
            if (windowManager == null) {
                windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            }

            // Create blocking overlay view
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            blockingView = inflater.inflate(R.layout.blocking_overlay, null)

            // Set up window parameters
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = android.view.Gravity.CENTER
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
            }

            // Add new view
            windowManager?.addView(blockingView, params)
            Log.d("AppBlockerService", "Overlay added successfully")

        } catch (e: Exception) {
            Log.e("AppBlockerService", "Error showing overlay", e)
        }
    }

    private fun hideBlockingOverlay() {
        try {
            if (blockingView != null && windowManager != null) {
                try {
                    windowManager?.removeView(blockingView)
                    Log.d("AppBlockerService", "Overlay removed successfully")
                } catch (e: IllegalArgumentException) {
                    // View was not attached, ignore
                    Log.d("AppBlockerService", "View was not attached to window manager")
                }
                blockingView = null
            }
        } catch (e: Exception) {
            Log.e("AppBlockerService", "Error hiding overlay", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Handler(Looper.getMainLooper()).post {
            hideBlockingOverlay()
            windowManager = null
            blockingInProgress = false
            lastBlockedApp = null
        }
        serviceScope.cancel()
        _isRunning.value = false
    }
} 