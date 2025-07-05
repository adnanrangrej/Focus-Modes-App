package com.github.adnanrangrej.focusmodes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.adnanrangrej.focusmodes.domain.model.SessionOutcome
import java.time.LocalDateTime

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    // Start and end time of the session
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,

    // Total duration of the session
    val plannedWorkDurationInSeconds: Long,
    val plannedBreakDurationInSeconds: Long,

    // Effective duration of the session
    val effectiveWorkDurationInSeconds: Long,
    val effectiveBreakDurationInSeconds: Long,

    // Mode of the session
    val focusModeName: String?,

    // Outcome of the session
    val sessionOutcome: SessionOutcome,
)