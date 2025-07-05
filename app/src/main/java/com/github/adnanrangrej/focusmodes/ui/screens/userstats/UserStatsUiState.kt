package com.github.adnanrangrej.focusmodes.ui.screens.userstats

import com.github.adnanrangrej.focusmodes.domain.model.Session
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class UserStatsUiState(
    val isLoading: Boolean = true,
    val totalFocusTimeToday: Duration = 0.seconds,
    val sessionsCompletedToday: Int = 0,
    val longestStreak: Int = 0,
    val weeklyFocusMinutes: List<Float> = List(7) { 0f },
    val recentSessions: List<Session> = emptyList()
)