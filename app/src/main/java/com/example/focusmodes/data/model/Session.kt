package com.example.focusmodes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val duration: Int, // in minutes
    val focusModeId: Int,
    val focusModeName: String,
    val completed: Boolean
) 