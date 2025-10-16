package com.github.adnanrangrej.focusmodes.data.triggers

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import com.github.adnanrangrej.focusmodes.domain.model.Trigger
import com.github.adnanrangrej.focusmodes.domain.model.TriggerConfig
import com.github.adnanrangrej.focusmodes.domain.model.TriggerType
import com.github.adnanrangrej.focusmodes.triggers.GeofenceTriggerReceiver
import com.github.adnanrangrej.focusmodes.triggers.TimeTriggerReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartTriggerScheduler @Inject constructor(
    private val application: Application,
    private val wifiTriggerEvaluator: WifiTriggerEvaluator
) {

    private val alarmManager: AlarmManager? = application.getSystemService()
    private val geofencingClient = LocationServices.getGeofencingClient(application)

    suspend fun schedule(trigger: Trigger) {
        if (!trigger.isEnabled) return
        when (trigger.type) {
            TriggerType.TIME -> {
                scheduleTimeEvent(trigger, isStart = true)
                scheduleTimeEvent(trigger, isStart = false)
            }
            TriggerType.LOCATION -> {
                registerGeofence(trigger)
            }
            TriggerType.WIFI -> {
                wifiTriggerEvaluator.evaluate(trigger)
            }
        }
    }

    suspend fun scheduleNextTimeEvent(trigger: Trigger, isStart: Boolean) {
        if (!trigger.isEnabled) return
        scheduleTimeEvent(trigger, isStart)
    }

    fun cancel(trigger: Trigger) {
        when (trigger.type) {
            TriggerType.TIME -> {
                cancelTimeEvent(trigger.id, true)
                cancelTimeEvent(trigger.id, false)
            }
            TriggerType.LOCATION -> removeGeofence(trigger.id)
            TriggerType.WIFI -> {
                // no-op
            }
        }
    }

    suspend fun restore(triggers: List<Trigger>) {
        triggers.filter { it.isEnabled }.forEach { schedule(it) }
    }

    fun cancelTimeTrigger(triggerId: Long) {
        cancelTimeEvent(triggerId, true)
        cancelTimeEvent(triggerId, false)
    }

    private suspend fun scheduleTimeEvent(trigger: Trigger, isStart: Boolean) {
        val triggerTime = nextTimeTriggerMillis(trigger, isStart) ?: return
        val pendingIntent = timePendingIntent(trigger.id, isStart)
        val manager = alarmManager ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && manager.canScheduleExactAlarms()) {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (security: SecurityException) {
            manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    private fun cancelTimeEvent(triggerId: Long, isStart: Boolean) {
        val manager = alarmManager ?: return
        val pendingIntent = timePendingIntent(triggerId, isStart)
        manager.cancel(pendingIntent)
    }

    private fun timePendingIntent(triggerId: Long, isStart: Boolean): PendingIntent {
        val intent = Intent(application, TimeTriggerReceiver::class.java).apply {
            action = TimeTriggerReceiver.ACTION_TIME_TRIGGER
            putExtra(TimeTriggerReceiver.EXTRA_TRIGGER_ID, triggerId)
            putExtra(TimeTriggerReceiver.EXTRA_IS_START, isStart)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(
            application,
            timeRequestCode(triggerId, isStart),
            intent,
            flags
        )
    }

    private fun timeRequestCode(triggerId: Long, isStart: Boolean): Int {
        val base = (triggerId % Int.MAX_VALUE).toInt()
        return base * 2 + if (isStart) 1 else 0
    }

    private fun nextTimeTriggerMillis(trigger: Trigger, isStart: Boolean): Long? {
        val config = trigger.config as? TriggerConfig.TimeConfig ?: return null
        val minutesOfDay = if (isStart) config.startMinutes else config.endMinutes
        val validDays = if (config.daysOfWeek.isEmpty()) {
            (1..7).toList()
        } else {
            config.daysOfWeek
        }

        val zoneId = ZoneId.systemDefault()
        val now = ZonedDateTime.now(zoneId)
        for (offset in 0..7) {
            val candidateDate = now.toLocalDate().plusDays(offset.toLong())
            val dayValue = candidateDate.dayOfWeek.value
            if (validDays.contains(dayValue)) {
                val candidateDateTime = candidateDate.atTime(minutesOfDay / 60, minutesOfDay % 60)
                val candidate = candidateDateTime.atZone(zoneId)
                if (candidate.isAfter(now) || candidate.isEqual(now)) {
                    return candidate.toInstant().toEpochMilli()
                }
            }
        }
        return null
    }

    @SuppressLint("MissingPermission")
    private suspend fun registerGeofence(trigger: Trigger) {
        val config = trigger.config as? TriggerConfig.LocationConfig ?: return
        val requestId = geofenceRequestId(trigger.id)
        removeGeofence(trigger.id)
        val geofence = Geofence.Builder()
            .setRequestId(requestId)
            .setCircularRegion(config.latitude, config.longitude, config.radiusMeters)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val initialTrigger = GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_EXIT
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(initialTrigger)
            .addGeofence(geofence)
            .build()

        try {
            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent())
        } catch (security: SecurityException) {
            // Permissions missing; ignore registration
        }
    }

    private fun removeGeofence(triggerId: Long) {
        geofencingClient.removeGeofences(listOf(geofenceRequestId(triggerId)))
    }

    private fun geofenceRequestId(triggerId: Long): String = "trigger_$triggerId"

    private fun geofencePendingIntent(): PendingIntent {
        val intent = Intent(application, GeofenceTriggerReceiver::class.java)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(application, 0, intent, flags)
    }
}
