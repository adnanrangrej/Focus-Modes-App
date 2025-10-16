package com.github.adnanrangrej.focusmodes.data.triggers

import android.app.Application
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.github.adnanrangrej.focusmodes.R
import com.github.adnanrangrej.focusmodes.domain.model.Trigger
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.GetFocusModeSnapshotUseCase
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.SetFocusModeActiveUseCase
import com.github.adnanrangrej.focusmodes.service.PomodoroTimerService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TriggerExecutionManager @Inject constructor(
    private val application: Application,
    private val setFocusModeActiveUseCase: SetFocusModeActiveUseCase,
    private val getFocusModeSnapshotUseCase: GetFocusModeSnapshotUseCase,
    private val activeTriggerStore: ActiveTriggerStore
) {

    suspend fun activate(trigger: Trigger) {
        val mode = getFocusModeSnapshotUseCase(trigger.focusModeId) ?: return

        setFocusModeActiveUseCase(true, mode.id)

        val startIntent = Intent(application, PomodoroTimerService::class.java).apply {
            action = PomodoroTimerService.ACTION_START
            putExtra(
                PomodoroTimerService.EXTRA_WORK_DURATION,
                mode.workDuration * 60 * 1000L
            )
            putExtra(
                PomodoroTimerService.EXTRA_BREAK_DURATION,
                mode.breakDuration * 60 * 1000L
            )
            putExtra(PomodoroTimerService.FOCUS_MODE_NAME, mode.name)
        }
        ContextCompat.startForegroundService(application, startIntent)
        activeTriggerStore.setActiveTrigger(trigger.id)

        postNotification(
            title = application.getString(R.string.trigger_notification_title, mode.name),
            message = application.getString(R.string.trigger_notification_message, trigger.name, mode.name),
            notificationId = SMART_TRIGGER_NOTIFICATION_ID_BASE + trigger.id.toInt()
        )
    }

    fun deactivate(trigger: Trigger) {
        val currentId = activeTriggerStore.getActiveTriggerId()
        if (currentId != trigger.id) {
            return
        }

        setFocusModeActiveUseCase(false, null)

        val stopIntent = Intent(application, PomodoroTimerService::class.java).apply {
            action = PomodoroTimerService.ACTION_STOP
        }
        application.startService(stopIntent)

        activeTriggerStore.clearActiveTrigger(trigger.id)

        postNotification(
            title = application.getString(R.string.trigger_notification_stopped_title),
            message = application.getString(R.string.trigger_notification_stopped_message, trigger.name),
            notificationId = SMART_TRIGGER_NOTIFICATION_ID_BASE + trigger.id.toInt() + 1
        )
    }

    private fun postNotification(title: String, message: String, notificationId: Int) {
        val manager = NotificationManagerCompat.from(application)
        if (!manager.areNotificationsEnabled()) {
            return
        }
        val notification = NotificationCompat.Builder(application, SMART_TRIGGER_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_shield_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        manager.notify(notificationId, notification)
    }
}
