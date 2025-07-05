package com.github.adnanrangrej.focusmodes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.adnanrangrej.focusmodes.data.local.dao.ModesDao
import com.github.adnanrangrej.focusmodes.data.local.dao.SessionDao
import com.github.adnanrangrej.focusmodes.data.local.entity.FocusModeEntity
import com.github.adnanrangrej.focusmodes.data.local.entity.SessionEntity

@Database(
    entities = [
        SessionEntity::class,
        FocusModeEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FocusModesDatabase : RoomDatabase() {
    // DAOs
    abstract fun sessionDao(): SessionDao

    abstract fun focusModeDao(): ModesDao
}