package com.github.adnanrangrej.focusmodes.triggers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.adnanrangrej.focusmodes.data.triggers.TriggerExecutionManager
import com.github.adnanrangrej.focusmodes.domain.model.TriggerConfig
import com.github.adnanrangrej.focusmodes.domain.repository.TriggersRepository
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GeofenceTriggerReceiver : BroadcastReceiver() {

    @Inject
    lateinit var triggersRepository: TriggersRepository

    @Inject
    lateinit var triggerExecutionManager: TriggerExecutionManager

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val event = GeofencingEvent.fromIntent(intent)
                if (event == null || event.hasError()) {
                    return@launch
                }

                val transition = event.geofenceTransition
                val triggerIds = event.triggeringGeofences?.mapNotNull { geofence ->
                    geofence.requestId.removePrefix("trigger_").toLongOrNull()
                } ?: emptyList()

                triggerIds.forEach { triggerId ->
                    val trigger = triggersRepository.getTriggerById(triggerId)
                    if (trigger == null || !trigger.isEnabled) return@forEach

                    val config = trigger.config as? TriggerConfig.LocationConfig ?: return@forEach
                    val shouldActivate = if (config.activateOnEntry) {
                        transition == Geofence.GEOFENCE_TRANSITION_ENTER
                    } else {
                        transition == Geofence.GEOFENCE_TRANSITION_EXIT
                    }
                    val shouldDeactivate = if (config.activateOnEntry) {
                        transition == Geofence.GEOFENCE_TRANSITION_EXIT
                    } else {
                        transition == Geofence.GEOFENCE_TRANSITION_ENTER
                    }

                    when {
                        shouldActivate -> triggerExecutionManager.activate(trigger)
                        shouldDeactivate -> triggerExecutionManager.deactivate(trigger)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
