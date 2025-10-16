package com.github.adnanrangrej.focusmodes.data.triggers

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveTriggerStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("smart_triggers_state", Context.MODE_PRIVATE)

    fun setActiveTrigger(triggerId: Long) {
        prefs.edit { putLong(ACTIVE_TRIGGER_KEY, triggerId) }
    }

    fun clearActiveTrigger(triggerId: Long) {
        val currentId = getActiveTriggerId()
        if (currentId == triggerId) {
            prefs.edit { remove(ACTIVE_TRIGGER_KEY) }
        }
    }

    fun getActiveTriggerId(): Long? {
        val stored = prefs.getLong(ACTIVE_TRIGGER_KEY, NO_ID)
        return if (stored == NO_ID) null else stored
    }

    fun clearAll() {
        prefs.edit { remove(ACTIVE_TRIGGER_KEY) }
    }

    private companion object {
        const val ACTIVE_TRIGGER_KEY = "active_trigger_id"
        const val NO_ID = -1L
    }
}
