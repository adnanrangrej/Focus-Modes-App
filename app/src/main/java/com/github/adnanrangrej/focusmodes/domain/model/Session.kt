package com.github.adnanrangrej.focusmodes.domain.model

import kotlin.time.Duration

data class Session(
    val id: Long = 0L,

    // Start and end time of the session
    val startTime: Long,
    val endTime: Long?,

    // Total duration of the session
    val plannedWorkDurationInSeconds: Duration,
    val plannedBreakDurationInSeconds: Duration,

    // Effective duration of the session
    val effectiveWorkDurationInSeconds: Duration,
    val effectiveBreakDurationInSeconds: Duration,

    // Mode of the session
    val focusModeName: String?,

    // Outcome of the session
    val sessionOutcome: SessionOutcome,
)