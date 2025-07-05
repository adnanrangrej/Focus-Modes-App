package com.github.adnanrangrej.focusmodes

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FocusModesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)
    }

    private fun createNotificationChannel(context: Context) {
        val name = "Pomodoro Timer"
        val descriptionText = "Shows the current timer status"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(
            "TIMER_CHANNEL_ID",
            name,
            importance
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            description = descriptionText
            enableVibration(false)
            setSound(null, null)
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

    }
}