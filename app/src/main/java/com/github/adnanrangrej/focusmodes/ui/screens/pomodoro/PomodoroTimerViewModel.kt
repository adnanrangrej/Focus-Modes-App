package com.github.adnanrangrej.focusmodes.ui.screens.pomodoro

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.adnanrangrej.focusmodes.service.PomodoroTimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PomodoroTimerViewModel @Inject constructor(
    private val app: Application
) : ViewModel() {

    private val _uiState = mutableStateOf(PomodoroTimerUiState())
    val uiState: State<PomodoroTimerUiState> = _uiState
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(
            p0: ComponentName?,
            binder: IBinder?
        ) {
            val binder = binder as PomodoroTimerService.LocalBinder
            isBound = true

            viewModelScope.launch {
                binder.getTotalTime().collectLatest {
                    _uiState.value = _uiState.value.copy(totalTime = it)
                }
            }
            viewModelScope.launch {
                binder.getRemainingTime().collectLatest {
                    _uiState.value = _uiState.value.copy(timeRemaining = it)
                }
            }
            viewModelScope.launch {
                binder.getIsRunning().collectLatest {
                    _uiState.value = _uiState.value.copy(isRunning = it)
                }
            }
            viewModelScope.launch {
                binder.getIsPaused().collectLatest {
                    _uiState.value = _uiState.value.copy(isPaused = it)
                }
            }
            viewModelScope.launch {
                binder.getIsFinished().collectLatest {
                    _uiState.value = _uiState.value.copy(isFinished = it)
                }
            }

            viewModelScope.launch {
                binder.getIsWorking().collectLatest {
                    _uiState.value = _uiState.value.copy(isWorking = it)
                }
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }
    }

    init {
        Intent(app, PomodoroTimerService::class.java).also { intent ->
            app.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }


    fun startTimer(
        workDuration: Long = 25 * 60 * 1000L,
        breakDuration: Long = 5 * 60 * 1000L,
        focusModeName: String? = null
    ) {
        val intent = Intent(app, PomodoroTimerService::class.java).apply {
            action = PomodoroTimerService.ACTION_START
            putExtra(PomodoroTimerService.EXTRA_WORK_DURATION, workDuration)
            putExtra(PomodoroTimerService.EXTRA_BREAK_DURATION, breakDuration)
            putExtra(PomodoroTimerService.FOCUS_MODE_NAME, focusModeName)
        }
        app.startForegroundService(intent)
    }

    fun pauseOrResume() {
        val intent = Intent(app, PomodoroTimerService::class.java).apply {
            action = PomodoroTimerService.ACTION_PAUSE_RESUME
        }
        app.startService(intent)
    }

    fun resetTimer() {
        val intent = Intent(app, PomodoroTimerService::class.java).apply {
            action = PomodoroTimerService.ACTION_RESET
        }
        app.startService(intent)
    }

    fun stopTimer() {
        val intent = Intent(app, PomodoroTimerService::class.java).apply {
            action = PomodoroTimerService.ACTION_STOP
        }
        app.startService(intent)
    }

    override fun onCleared() {
        super.onCleared()

        if (isBound) {
            app.unbindService(connection)
            isBound = false
        }
    }
}