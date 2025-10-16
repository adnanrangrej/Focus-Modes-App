package com.github.adnanrangrej.focusmodes.data.repository

import com.github.adnanrangrej.focusmodes.data.local.dao.TriggerDao
import com.github.adnanrangrej.focusmodes.data.local.mapper.toDomainModel
import com.github.adnanrangrej.focusmodes.data.local.mapper.toEntityModel
import com.github.adnanrangrej.focusmodes.data.triggers.SmartTriggerScheduler
import com.github.adnanrangrej.focusmodes.domain.model.Trigger
import com.github.adnanrangrej.focusmodes.domain.model.TriggerType
import com.github.adnanrangrej.focusmodes.domain.repository.TriggersRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class TriggersRepositoryImpl @Inject constructor(
    private val triggerDao: TriggerDao,
    private val smartTriggerScheduler: SmartTriggerScheduler,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : TriggersRepository {

    override fun observeTriggers(): Flow<List<Trigger>> =
        triggerDao.getAllTriggers().map { entities ->
            entities.map { it.toDomainModel() }
        }

    override fun observeTrigger(triggerId: Long): Flow<Trigger?> =
        triggerDao.observeTriggerById(triggerId).map { it?.toDomainModel() }

    override suspend fun getTriggerById(triggerId: Long): Trigger? = withContext(dispatcher) {
        triggerDao.getTriggerById(triggerId)?.toDomainModel()
    }

    override suspend fun insertTrigger(trigger: Trigger): Long = withContext(dispatcher) {
        val id = triggerDao.insertTrigger(trigger.toEntityModel())
        val saved = trigger.copy(id = id)
        if (saved.isEnabled) {
            smartTriggerScheduler.schedule(saved)
        }
        id
    }

    override suspend fun updateTrigger(trigger: Trigger) = withContext(dispatcher) {
        val existing = triggerDao.getTriggerById(trigger.id)
        triggerDao.updateTrigger(trigger.toEntityModel())
        existing?.let { smartTriggerScheduler.cancel(it.toDomainModel()) }
        if (trigger.isEnabled) {
            smartTriggerScheduler.schedule(trigger)
        }
    }

    override suspend fun deleteTrigger(triggerId: Long) = withContext(dispatcher) {
        val existing = triggerDao.getTriggerById(triggerId) ?: return@withContext
        triggerDao.deleteTrigger(existing)
        smartTriggerScheduler.cancel(existing.toDomainModel())
    }

    override suspend fun setTriggerEnabled(triggerId: Long, enabled: Boolean) = withContext(dispatcher) {
        val existingEntity = triggerDao.getTriggerById(triggerId) ?: return@withContext
        triggerDao.updateTriggerEnabled(triggerId, enabled)
        val updated = existingEntity.toDomainModel().copy(isEnabled = enabled)
        if (enabled) {
            smartTriggerScheduler.schedule(updated)
        } else {
            smartTriggerScheduler.cancel(updated)
        }
    }

    override suspend fun getEnabledTriggers(): List<Trigger> = withContext(dispatcher) {
        triggerDao.getEnabledTriggers().map { it.toDomainModel() }
    }

    override suspend fun getEnabledTriggersByType(type: TriggerType): List<Trigger> = withContext(dispatcher) {
        triggerDao.getEnabledTriggersByType(type).map { it.toDomainModel() }
    }

    suspend fun restoreAllEnabledTriggers() = withContext(dispatcher) {
        val enabled = triggerDao.getEnabledTriggers().map { it.toDomainModel() }
        smartTriggerScheduler.restore(enabled)
    }
}
