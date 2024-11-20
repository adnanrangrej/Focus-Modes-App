package com.example.focusmodes.ui.viewmodels

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.focusmodes.data.db.AppDatabase
import com.example.focusmodes.data.model.Session
import com.example.focusmodes.data.repository.SessionRepository
import com.example.focusmodes.util.TimerPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
class PomodoroViewModel(application: Application) : AndroidViewModel(application) {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var timerJob: Job? = null
    private val repository: SessionRepository
    private var currentSession: Session? = null
    
    private val sharedPreferences = application.getSharedPreferences(
        "pomodoro_prefs",
        Context.MODE_PRIVATE
    )

    private var _currentFocusTime: Int = FOCUS_TIME
    val currentFocusTime: Int get() = _currentFocusTime
    private var _currentBreakTime: Int = BREAK_TIME
    val currentBreakTime: Int get() = _currentBreakTime



    private fun getTimerDurations(): Pair<Int, Int> {
        val (focusMinutes, breakMinutes) = TimerPreferences.getTimerSettings(getApplication())
        return Pair(
            focusMinutes * 60, // Convert to seconds
            breakMinutes * 60  // Convert to seconds
        )
    }

    init {
        val sessionDao = AppDatabase.getDatabase(application).sessionDao()
        repository = SessionRepository(sessionDao)
        
        // Update FOCUS_TIME and BREAK_TIME based on settings
        val (focusTime, breakTime) = getTimerDurations()
        _currentFocusTime = focusTime * 60
        _currentBreakTime = breakTime * 60
        
        // Initialize timer state with current focus time
        _timerState.value = TimerState(
            remainingSeconds = currentFocusTime,
            isRunning = false,
            isBreak = false
        )
        
        restoreTimerState()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun restoreTimerState() {
        // Only restore if there was an active session and timer wasn't reset
        if (sharedPreferences.contains("session_start_time") && 
            sharedPreferences.contains("remaining_seconds")) {
                
            val remainingSeconds = sharedPreferences.getInt("remaining_seconds", FOCUS_TIME)
            val isRunning = sharedPreferences.getBoolean("is_running", false)
            val isBreak = sharedPreferences.getBoolean("is_break", false)
            val sessionStartTime = sharedPreferences.getLong("session_start_time", -1L)

            if (sessionStartTime != -1L) {
                val startTime = LocalDateTime.ofEpochSecond(sessionStartTime, 0, ZoneOffset.UTC)
                currentSession = Session(
                    startTime = startTime,
                    endTime = LocalDateTime.now().plusSeconds(remainingSeconds.toLong()),
                    duration = 25,
                    focusModeId = -1,
                    focusModeName = "Pomodoro",
                    completed = false
                )

                _timerState.value = TimerState(
                    remainingSeconds = remainingSeconds,
                    isRunning = false,
                    isBreak = isBreak
                )
            }
        } else {
            // If no saved state or timer was reset, start fresh with current settings
            val (focusTime, breakTime) = getTimerDurations()
            _currentFocusTime = focusTime
            _currentBreakTime = breakTime
            
            _timerState.value = TimerState(
                remainingSeconds = currentFocusTime,
                isRunning = false,
                isBreak = false
            )
        }
    }

    private fun saveTimerState() {
        sharedPreferences.edit().apply {
            putInt("remaining_seconds", _timerState.value.remainingSeconds)
            putBoolean("is_running", _timerState.value.isRunning)
            putBoolean("is_break", _timerState.value.isBreak)
            currentSession?.startTime?.toEpochSecond(ZoneOffset.UTC)?.let {
                putLong("session_start_time", it)
            } ?: remove("session_start_time")
            apply()
        }
    }

    fun startTimer(initialSeconds: Int = _timerState.value.remainingSeconds) {
        if (timerJob?.isActive == true) return

        // Create new session when timer starts
        if (currentSession == null && !_timerState.value.isBreak) {
            val startTime = LocalDateTime.now()
            currentSession = Session(
                startTime = startTime,
                endTime = startTime.plusSeconds(initialSeconds.toLong()),
                duration = 0, // Will be updated when session ends
                focusModeId = -1,
                focusModeName = "Pomodoro",
                completed = false
            )
            // Save session start time
            sharedPreferences.edit()
                .putLong("session_start_time", startTime.toEpochSecond(ZoneOffset.UTC))
                .apply()
            
            Log.d("PomodoroViewModel", "Started new session at: $startTime")
        }

        timerJob = viewModelScope.launch {
            var seconds = initialSeconds
            while (seconds > 0 && isActive) {
                delay(1000)
                seconds--
                _timerState.value = _timerState.value.copy(
                    remainingSeconds = seconds,
                    isRunning = true
                )
                saveTimerState()
            }
            if (seconds <= 0) {
                onTimerComplete()
            }
        }
        _timerState.value = _timerState.value.copy(isRunning = true)
        saveTimerState()
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(isRunning = false)
        saveTimerState()

        // Save partial session when paused
        viewModelScope.launch {
            if (!_timerState.value.isBreak) { // Only save focus sessions, not breaks
                saveCurrentSession()
            }
        }
    }

    private suspend fun saveCurrentSession() {
        currentSession?.let { session ->
            val endTime = LocalDateTime.now()
            val actualDuration = ChronoUnit.MINUTES.between(
                session.startTime,
                endTime
            ).toInt()
            
            Log.d("PomodoroViewModel", "Session duration: $actualDuration minutes")
            
            // Save session even if less than a minute
            val completedSession = session.copy(
                endTime = endTime,
                duration = maxOf(1, actualDuration), // Minimum 1 minute
                completed = true
            )
            repository.addSession(completedSession)
            Log.d("PomodoroViewModel", "Saved session: $completedSession")
        }
        currentSession = null
        sharedPreferences.edit().remove("session_start_time").apply()
    }

    fun resetTimer() {
        // Save current session if it's a focus session
        if (!_timerState.value.isBreak) {
            viewModelScope.launch {
                saveCurrentSession()
            }
        }

        timerJob?.cancel()
        
        // Update times from preferences
        val (focusTime, breakTime) = getTimerDurations()
        _currentFocusTime = focusTime
        _currentBreakTime = breakTime
        
        // Reset state with current times
        _timerState.value = TimerState(
            remainingSeconds = currentFocusTime,
            isRunning = false,
            isBreak = false
        )
        
        // Clear all saved states
        currentSession = null
        sharedPreferences.edit()
            .remove("remaining_seconds")
            .remove("is_running")
            .remove("is_break")
            .remove("session_start_time")
            .apply()
    }

    fun skipPhase() {
        timerJob?.cancel()
        currentSession = null
        
        // Toggle break state and set appropriate time
        val isBreak = !_timerState.value.isBreak
        val remainingSeconds = if (isBreak) currentBreakTime else currentFocusTime
        
        _timerState.value = _timerState.value.copy(
            isBreak = isBreak,
            remainingSeconds = remainingSeconds,
            isRunning = false
        )
        saveTimerState()
        sharedPreferences.edit().remove("session_start_time").apply()
    }

    private fun onTimerComplete() {
        viewModelScope.launch {
            if (!_timerState.value.isBreak) { // Only save focus sessions, not breaks
                saveCurrentSession()
            }
        }

        // Toggle break state and set appropriate time
        val isBreak = !_timerState.value.isBreak
        val remainingSeconds = if (isBreak) currentBreakTime else currentFocusTime
        
        _timerState.value = TimerState(
            remainingSeconds = remainingSeconds,
            isRunning = false,
            isBreak = isBreak
        )
        saveTimerState()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            if (!_timerState.value.isBreak && timerState.value.isRunning) {
                saveCurrentSession()
            }
        }
        timerJob?.cancel()
        saveTimerState()
    }

    companion object {
        const val FOCUS_TIME = 25 * 60 // Default 25 minutes in seconds
        const val BREAK_TIME = 5 * 60  // Default 5 minutes in seconds
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PomodoroViewModel::class.java)) {
                return PomodoroViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
data class TimerState(
    val remainingSeconds: Int = PomodoroViewModel.FOCUS_TIME,
    val isRunning: Boolean = false,
    val isBreak: Boolean = false
) 