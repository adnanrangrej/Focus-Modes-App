package com.github.adnanrangrej.focusmodes.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.github.adnanrangrej.focusmodes.data.local.dao.ModesDao
import com.github.adnanrangrej.focusmodes.data.local.mapper.toDomainModel
import com.github.adnanrangrej.focusmodes.data.local.mapper.toEntityModel
import com.github.adnanrangrej.focusmodes.domain.model.FocusMode
import com.github.adnanrangrej.focusmodes.domain.repository.FocusModesRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class FocusModesRepositoryImpl(
    private val dao: ModesDao,
    private val context: Context
) : FocusModesRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "focus_modes_prefs",
        Context.MODE_PRIVATE
    )

    private val isFocusModeEnabledKey = "is_focus_mode_enabled"

    private val focusModeIdKey = "focus_mode_id"

    override val isFocusModeEnabled: Flow<Boolean> = callbackFlow {

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
            if (key == isFocusModeEnabledKey) {
                val newValue = preferences.getBoolean(key, false)
                trySend(newValue)
            }
        }

        val initialValue = prefs.getBoolean(isFocusModeEnabledKey, false)
        trySend(initialValue)
        prefs.registerOnSharedPreferenceChangeListener(listener)

        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }

    }

    override suspend fun insertMode(mode: FocusMode): Long = dao.insertMode(mode.toEntityModel())

    override suspend fun updateMode(mode: FocusMode) = dao.updateMode(mode.toEntityModel())

    override fun getModeById(id: Long): Flow<FocusMode>? = dao.getModeById(id)?.map { mode ->
        mode.toDomainModel()
    }

    override fun getAllModes(): Flow<List<FocusMode>> = dao.getAllModes().map {
        it.map { mode ->
            mode.toDomainModel()
        }
    }

    override fun setFocusModeActive(isActive: Boolean, focusModeId: Long?) {
        prefs.edit {
            putBoolean(isFocusModeEnabledKey, isActive)
            focusModeId?.let {
                putLong(focusModeIdKey, it)
            }
        }
    }

    override fun getFocusModeId(): Long? {
        val id = prefs.getLong(focusModeIdKey, -1)
        return if (id == -1L) null else id
    }
}