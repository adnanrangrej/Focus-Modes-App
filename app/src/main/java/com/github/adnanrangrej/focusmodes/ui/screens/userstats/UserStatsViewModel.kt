package com.github.adnanrangrej.focusmodes.ui.screens.userstats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.adnanrangrej.focusmodes.domain.model.Session
import com.github.adnanrangrej.focusmodes.domain.model.SessionOutcome
import com.github.adnanrangrej.focusmodes.domain.usecase.session.InsertSessionUseCase
import com.github.adnanrangrej.focusmodes.domain.usecase.userstats.GetAllSessionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class UserStatsViewModel @Inject constructor(
    private val getAllSessionsUseCase: GetAllSessionsUseCase,
    private val insertSessionUseCase: InsertSessionUseCase
) : ViewModel() {

    val uiState: StateFlow<UserStatsUiState> =
        getAllSessionsUseCase().map { sessions ->
            val today = LocalDate.now()
            val startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toInstant()
            val startOfLast7Days =
                today.minusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant()

            // Filter for sessions that ended today
            val sessionsToday = sessions.filter {
                it.endTime != null && Instant.ofEpochSecond(it.endTime) >= startOfToday
            }

            // Filter for sessions in the last 7 days for the chart
            val sessionsLast7Days = sessions.filter {
                it.endTime != null && Instant.ofEpochSecond(it.endTime) >= startOfLast7Days
            }

            UserStatsUiState(
                isLoading = false,
                totalFocusTimeToday = calculateTotalFocusTime(sessionsToday),
                sessionsCompletedToday = calculateSessionsCompleted(sessionsToday),
                longestStreak = calculateLongestStreak(sessions),
                weeklyFocusMinutes = calculateWeeklyFocus(sessionsLast7Days),
                recentSessions = sessions.sortedByDescending { it.endTime }.take(20)
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStatsUiState(isLoading = true) // Start with a loading state
        )

    private fun calculateTotalFocusTime(sessions: List<Session>): Duration {
        val totalSeconds = sessions.sumOf { it.effectiveWorkDurationInSeconds.inWholeSeconds }
        return totalSeconds.seconds
    }

    private fun calculateSessionsCompleted(sessions: List<Session>): Int {
        return sessions.count { it.sessionOutcome == SessionOutcome.COMPLETED }
    }

    private fun calculateWeeklyFocus(sessions: List<Session>): List<Float> {
        val today = LocalDate.now()
        // Create a map to hold total minutes for each of the last 7 days
        val dailyTotals = (0..6).associate {
            today.minusDays(it.toLong()) to 0f
        }.toMutableMap()

        for (session in sessions) {
            session.endTime?.let {
                val sessionDate =
                    Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).toLocalDate()
                if (dailyTotals.containsKey(sessionDate)) {
                    val currentMinutes = dailyTotals[sessionDate] ?: 0f
                    dailyTotals[sessionDate] =
                        currentMinutes + session.effectiveWorkDurationInSeconds.inWholeMinutes.toFloat()
                }
            }
        }
        // Return the values sorted from 7 days ago to today
        return dailyTotals.keys.sorted().map { dailyTotals[it] ?: 0f }
    }

    fun insertDummyData() {
        viewModelScope.launch {
            val now = LocalDate.now()
            val dummySessions = mutableListOf<Session>()

            // --- A 7-Day Streak ---
            for (i in 1..7) {
                dummySessions.add(
                    createDummySession(
                        now.minusDays(i.toLong()),
                        50.minutes,
                        50.minutes,
                        "Daily Review",
                        SessionOutcome.COMPLETED
                    )
                )
                if (i % 2 == 0) { // Add a second session on some days
                    dummySessions.add(
                        createDummySession(
                            now.minusDays(i.toLong()),
                            25.minutes,
                            25.minutes,
                            "Language Study",
                            SessionOutcome.COMPLETED
                        )
                    )
                }
            }

            // --- Sessions from last week with a gap ---
            dummySessions.add(
                createDummySession(
                    now.minusDays(9),
                    25.minutes,
                    10.minutes,
                    "Reading",
                    SessionOutcome.CANCELLED
                )
            )
            dummySessions.add(
                createDummySession(
                    now.minusDays(10),
                    50.minutes,
                    50.minutes,
                    "Side Project",
                    SessionOutcome.COMPLETED
                )
            )
            dummySessions.add(
                createDummySession(
                    now.minusDays(11),
                    50.minutes,
                    45.minutes,
                    "Side Project",
                    SessionOutcome.COMPLETED
                )
            )

            // --- Sessions from this week ---
            dummySessions.add(
                createDummySession(
                    now,
                    25.minutes,
                    25.minutes,
                    "Email Cleanup",
                    SessionOutcome.COMPLETED
                )
            )
            dummySessions.add(
                createDummySession(
                    now,
                    50.minutes,
                    20.minutes,
                    "App Development",
                    SessionOutcome.CANCELLED
                )
            )
            dummySessions.add(
                createDummySession(
                    now,
                    50.minutes,
                    50.minutes,
                    "App Development",
                    SessionOutcome.COMPLETED
                )
            )

            // --- A few more scattered sessions ---
            dummySessions.add(
                createDummySession(
                    now.minusDays(15),
                    90.minutes,
                    90.minutes,
                    "Deep Work Session",
                    SessionOutcome.COMPLETED
                )
            )
            dummySessions.add(
                createDummySession(
                    now.minusDays(16),
                    25.minutes,
                    5.minutes,
                    "Planning",
                    SessionOutcome.CANCELLED
                )
            )
            dummySessions.add(
                createDummySession(
                    now.minusDays(20),
                    50.minutes,
                    50.minutes,
                    "Refactoring",
                    SessionOutcome.COMPLETED
                )
            )
            dummySessions.add(
                createDummySession(
                    now.minusDays(21),
                    50.minutes,
                    50.minutes,
                    "Refactoring",
                    SessionOutcome.COMPLETED
                )
            )
            dummySessions.add(
                createDummySession(
                    now.minusDays(22),
                    50.minutes,
                    50.minutes,
                    "Refactoring",
                    SessionOutcome.COMPLETED
                )
            )


            dummySessions.forEach { insertSessionUseCase(it) }
        }
    }

    private fun createDummySession(
        date: LocalDate,
        plannedWork: Duration,
        effectiveWork: Duration,
        modeName: String,
        outcome: SessionOutcome
    ): Session {
        val startTime = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        val plannedBreak = (plannedWork.inWholeMinutes / 5).minutes.coerceAtLeast(5.minutes)
        return Session(
            startTime = startTime,
            endTime = startTime + effectiveWork.inWholeSeconds,
            plannedWorkDurationInSeconds = plannedWork,
            plannedBreakDurationInSeconds = plannedBreak,
            effectiveBreakDurationInSeconds = effectiveWork,
            effectiveWorkDurationInSeconds = if (outcome == SessionOutcome.COMPLETED) plannedBreak else 0.minutes,
            focusModeName = modeName,
            sessionOutcome = outcome
        )
    }

    private fun calculateLongestStreak(sessions: List<Session>): Int {
        if (sessions.isEmpty()) return 0

        val sessionDates = sessions
            .filter { it.sessionOutcome == SessionOutcome.COMPLETED }
            .mapNotNull {
                it.endTime?.let {
                    Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).toLocalDate()
                }
            }
            .distinct()
            .sorted()

        if (sessionDates.isEmpty()) return 0

        var longestStreak = 0
        var currentStreak = 0
        var lastDate: LocalDate? = null

        for (date in sessionDates) {
            if (lastDate != null && date.isEqual(lastDate.plusDays(1))) {
                currentStreak++
            } else {
                currentStreak = 1 // Start a new streak
            }
            if (currentStreak > longestStreak) {
                longestStreak = currentStreak
            }
            lastDate = date
        }
        return longestStreak
    }
}