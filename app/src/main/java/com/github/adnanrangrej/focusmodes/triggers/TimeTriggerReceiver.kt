package com.github.adnanrangrej.focusmodes.triggers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.github.adnanrangrej.focusmodes.data.triggers.SmartTriggerScheduler
import com.github.adnanrangrej.focusmodes.data.triggers.TriggerExecutionManager
import com.github.adnanrangrej.focusmodes.domain.repository.TriggersRepository

@AndroidEntryPoint
class TimeTriggerReceiver : BroadcastReceiver() {

    @Inject
    lateinit var triggersRepository: TriggersRepository

    @Inject
    lateinit var smartTriggerScheduler: SmartTriggerScheduler

    @Inject
    lateinit var triggerExecutionManager: TriggerExecutionManager

    override fun onReceive(context: Context, intent: Intent) {
        val triggerId = intent.getLongExtra(EXTRA_TRIGGER_ID, -1L)
        if (triggerId == -1L) return
        val isStartEvent = intent.getBooleanExtra(EXTRA_IS_START, false)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val trigger = triggersRepository.getTriggerById(triggerId)
                if (trigger != null && trigger.isEnabled) {
                    if (isStartEvent) {
                        triggerExecutionManager.activate(trigger)
                    } else {
                        triggerExecutionManager.deactivate(trigger)
                    }
                    smartTriggerScheduler.scheduleNextTimeEvent(trigger, isStartEvent)
                } else {
                    smartTriggerScheduler.cancelTimeTrigger(triggerId)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_TIME_TRIGGER = "com.github.adnanrangrej.focusmodes.TIME_TRIGGER"
        const val EXTRA_TRIGGER_ID = "extra_trigger_id"
        const val EXTRA_IS_START = "extra_is_start"
    }
}
