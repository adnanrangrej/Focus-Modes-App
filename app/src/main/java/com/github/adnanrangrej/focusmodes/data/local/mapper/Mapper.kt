package com.github.adnanrangrej.focusmodes.data.local.mapper

import com.github.adnanrangrej.focusmodes.data.local.entity.FocusModeEntity
import com.github.adnanrangrej.focusmodes.data.local.entity.SessionEntity
import com.github.adnanrangrej.focusmodes.domain.model.FocusMode
import com.github.adnanrangrej.focusmodes.domain.model.Session
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.seconds

fun SessionEntity.toDomainModel(): Session {

    return Session(
        id = id,
        startTime = startTime.toEpochSecond(ZoneOffset.UTC),
        endTime = endTime?.toEpochSecond(ZoneOffset.UTC),
        plannedWorkDurationInSeconds = plannedWorkDurationInSeconds.seconds,
        plannedBreakDurationInSeconds = plannedBreakDurationInSeconds.seconds,
        effectiveWorkDurationInSeconds = effectiveWorkDurationInSeconds.seconds,
        effectiveBreakDurationInSeconds = effectiveBreakDurationInSeconds.seconds,
        focusModeName = focusModeName,
        sessionOutcome = sessionOutcome
    )
}

fun Session.toEntityModel(): SessionEntity {
    return SessionEntity(
        id = id,
        startTime = LocalDateTime.ofEpochSecond(startTime, 0, ZoneOffset.UTC),
        endTime = endTime?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) },
        plannedWorkDurationInSeconds = plannedWorkDurationInSeconds.inWholeSeconds,
        plannedBreakDurationInSeconds = plannedBreakDurationInSeconds.inWholeSeconds,
        effectiveWorkDurationInSeconds = effectiveWorkDurationInSeconds.inWholeSeconds,
        effectiveBreakDurationInSeconds = effectiveBreakDurationInSeconds.inWholeSeconds,
        focusModeName = focusModeName,
        sessionOutcome = sessionOutcome
    )
}

fun FocusModeEntity.toDomainModel(): FocusMode {
    return FocusMode(
        id = id,
        name = name,
        workDuration = workDuration,
        breakDuration = breakDuration,
        description = description,
        blockedAppPackages = blockedAppPackages
    )
}

fun FocusMode.toEntityModel(): FocusModeEntity {
    return FocusModeEntity(
        id = id,
        name = name,
        workDuration = workDuration,
        breakDuration = breakDuration,
        description = description,
        blockedAppPackages = blockedAppPackages
    )
}