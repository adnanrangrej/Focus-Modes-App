package com.example.focusmodes.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.focusmodes.data.db.AppDatabase
import com.example.focusmodes.data.model.Session
import com.example.focusmodes.data.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ProgressViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SessionRepository
    
    private val _weeklyStats = MutableStateFlow<List<Session>>(emptyList())
    val weeklyStats: StateFlow<List<Session>> = _weeklyStats.asStateFlow()
    
    private val _totalFocusTime = MutableStateFlow(0)
    val totalFocusTime: StateFlow<Int> = _totalFocusTime.asStateFlow()
    
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()
    
    private val _todaySessions = MutableStateFlow(0)
    val todaySessions: StateFlow<Int> = _todaySessions.asStateFlow()

    init {
        val sessionDao = AppDatabase.getDatabase(application).sessionDao()
        repository = SessionRepository(sessionDao)
        
        // Collect data in separate coroutines
        viewModelScope.launch {
            repository.getWeeklyStats()
                .distinctUntilChanged()
                .collect { sessions ->
                    _weeklyStats.value = sessions
                    Log.d("ProgressViewModel", "Updated weekly stats: ${sessions.size} sessions")
                }
        }

        viewModelScope.launch {
            repository.getWeeklyFocusTime()
                .distinctUntilChanged()
                .collect { time ->
                    if (time != null) {
                        _totalFocusTime.value = time
                    }
                    Log.d("ProgressViewModel", "Updated total focus time: $time minutes")
                }
        }

        viewModelScope.launch {
            repository.getTodayCompletedSessions()
                .distinctUntilChanged()
                .collect { count ->
                    _todaySessions.value = count
                    Log.d("ProgressViewModel", "Updated today's sessions: $count")
                }
        }

        viewModelScope.launch {
            repository.getCurrentStreak()
                .distinctUntilChanged()
                .collect { streak ->
                    _currentStreak.value = streak
                    Log.d("ProgressViewModel", "Updated streak: $streak days")
                }
        }
    }

    fun formatFocusTime(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return if (hours > 0) {
            "${hours}h ${remainingMinutes}m"
        } else {
            "${remainingMinutes}m"
        }
    }

    // Add Factory class
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
                return ProgressViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 