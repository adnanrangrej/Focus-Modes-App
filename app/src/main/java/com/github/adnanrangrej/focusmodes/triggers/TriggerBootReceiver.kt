package com.github.adnanrangrej.focusmodes.triggers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.adnanrangrej.focusmodes.domain.usecase.triggers.RestoreTriggersUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TriggerBootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var restoreTriggersUseCase: RestoreTriggersUseCase

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                restoreTriggersUseCase()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
