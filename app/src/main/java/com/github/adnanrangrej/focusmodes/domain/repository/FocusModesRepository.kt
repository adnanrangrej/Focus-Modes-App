package com.github.adnanrangrej.focusmodes.domain.repository

import com.github.adnanrangrej.focusmodes.domain.model.FocusMode
import kotlinx.coroutines.flow.Flow

interface FocusModesRepository {

    val isFocusModeEnabled: Flow<Boolean>

    suspend fun insertMode(mode: FocusMode): Long

    suspend fun updateMode(mode: FocusMode)

    fun getModeById(id: Long): Flow<FocusMode>?

    fun getAllModes(): Flow<List<FocusMode>>

    fun setFocusModeActive(isActive: Boolean, focusModeId: Long?)

    fun getFocusModeId(): Long?

}