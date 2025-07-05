package com.github.adnanrangrej.focusmodes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.adnanrangrej.focusmodes.data.local.entity.FocusModeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMode(mode: FocusModeEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMode(mode: FocusModeEntity)

    @Query("SELECT * FROM FOCUS_MODE WHERE id = :id")
    fun getModeById(id: Long): Flow<FocusModeEntity>?

    @Query("SELECT * FROM FOCUS_MODE")
    fun getAllModes(): Flow<List<FocusModeEntity>>

}