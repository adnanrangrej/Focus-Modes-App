package com.github.adnanrangrej.focusmodes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.adnanrangrej.focusmodes.data.local.entity.TriggerEntity
import com.github.adnanrangrej.focusmodes.domain.model.TriggerType
import kotlinx.coroutines.flow.Flow

@Dao
interface TriggerDao {

    @Query("SELECT * FROM smart_trigger ORDER BY name COLLATE NOCASE")
    fun getAllTriggers(): Flow<List<TriggerEntity>>

    @Query("SELECT * FROM smart_trigger WHERE id = :id")
    fun observeTriggerById(id: Long): Flow<TriggerEntity?>

    @Query("SELECT * FROM smart_trigger WHERE id = :id")
    suspend fun getTriggerById(id: Long): TriggerEntity?

    @Query("SELECT * FROM smart_trigger WHERE isEnabled = 1")
    suspend fun getEnabledTriggers(): List<TriggerEntity>

    @Query("SELECT * FROM smart_trigger WHERE isEnabled = 1 AND type = :type")
    suspend fun getEnabledTriggersByType(type: TriggerType): List<TriggerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrigger(trigger: TriggerEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTrigger(trigger: TriggerEntity)

    @Delete
    suspend fun deleteTrigger(trigger: TriggerEntity)

    @Query("DELETE FROM smart_trigger WHERE id = :id")
    suspend fun deleteTriggerById(id: Long)

    @Query("UPDATE smart_trigger SET isEnabled = :enabled WHERE id = :id")
    suspend fun updateTriggerEnabled(id: Long, enabled: Boolean)
}
