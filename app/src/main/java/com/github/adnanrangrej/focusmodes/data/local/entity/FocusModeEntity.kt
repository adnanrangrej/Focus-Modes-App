package com.github.adnanrangrej.focusmodes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_mode")
data class FocusModeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val workDuration: Long,
    val breakDuration: Long,
    val description: String?,
    val blockedAppPackages: List<String>
)
