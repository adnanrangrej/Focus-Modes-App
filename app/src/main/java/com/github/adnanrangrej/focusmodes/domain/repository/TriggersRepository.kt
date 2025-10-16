package com.github.adnanrangrej.focusmodes.domain.repository

import com.github.adnanrangrej.focusmodes.domain.model.Trigger
import com.github.adnanrangrej.focusmodes.domain.model.TriggerType
import kotlinx.coroutines.flow.Flow

interface TriggersRepository {

    fun observeTriggers(): Flow<List<Trigger>>

    fun observeTrigger(triggerId: Long): Flow<Trigger?>

    suspend fun getTriggerById(triggerId: Long): Trigger?

    suspend fun insertTrigger(trigger: Trigger): Long

    suspend fun updateTrigger(trigger: Trigger)

    suspend fun deleteTrigger(triggerId: Long)

    suspend fun setTriggerEnabled(triggerId: Long, enabled: Boolean)

    suspend fun getEnabledTriggers(): List<Trigger>

    suspend fun getEnabledTriggersByType(type: TriggerType): List<Trigger>

    suspend fun restoreAllEnabledTriggers()
}
