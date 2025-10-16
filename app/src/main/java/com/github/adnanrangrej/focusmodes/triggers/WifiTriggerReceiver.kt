package com.github.adnanrangrej.focusmodes.triggers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.github.adnanrangrej.focusmodes.data.triggers.WifiTriggerEvaluator
import com.github.adnanrangrej.focusmodes.domain.model.TriggerType
import com.github.adnanrangrej.focusmodes.domain.repository.TriggersRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WifiTriggerReceiver : BroadcastReceiver() {

    @Inject
    lateinit var triggersRepository: TriggersRepository

    @Inject
    lateinit var wifiTriggerEvaluator: WifiTriggerEvaluator

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        if (!SUPPORTED_ACTIONS.contains(action)) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val triggers = triggersRepository.getEnabledTriggersByType(TriggerType.WIFI)
                triggers.forEach { trigger ->
                    wifiTriggerEvaluator.evaluate(trigger)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_EVALUATE_WIFI = "com.github.adnanrangrej.focusmodes.ACTION_EVALUATE_WIFI"

        private val SUPPORTED_ACTIONS = setOf(
            WifiManager.NETWORK_STATE_CHANGED_ACTION,
            ConnectivityManager.CONNECTIVITY_ACTION,
            ACTION_EVALUATE_WIFI
        )
    }
}
