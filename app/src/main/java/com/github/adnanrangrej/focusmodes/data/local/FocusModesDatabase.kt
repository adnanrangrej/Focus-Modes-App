package com.github.adnanrangrej.focusmodes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.adnanrangrej.focusmodes.data.local.dao.ModesDao
import com.github.adnanrangrej.focusmodes.data.local.dao.SessionDao
import com.github.adnanrangrej.focusmodes.data.local.dao.TriggerDao
import com.github.adnanrangrej.focusmodes.data.local.entity.FocusModeEntity
import com.github.adnanrangrej.focusmodes.data.local.entity.SessionEntity
import com.github.adnanrangrej.focusmodes.data.local.entity.TriggerEntity

@Database(
    entities = [
        SessionEntity::class,
        FocusModeEntity::class,
        TriggerEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FocusModesDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao

    abstract fun focusModeDao(): ModesDao

    abstract fun triggerDao(): TriggerDao

    object Migrations {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `smart_trigger` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `focusModeId` INTEGER NOT NULL, `isEnabled` INTEGER NOT NULL, `config` TEXT NOT NULL, FOREIGN KEY(`focusModeId`) REFERENCES `focus_mode`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_smart_trigger_focusModeId` ON `smart_trigger` (`focusModeId`)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_smart_trigger_type` ON `smart_trigger` (`type`)"
                )
            }
        }

        val ALL = arrayOf(MIGRATION_1_2)
    }
}
