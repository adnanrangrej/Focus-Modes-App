package com.github.adnanrangrej.focusmodes.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.github.adnanrangrej.focusmodes.R
import com.github.adnanrangrej.focusmodes.domain.model.Session
import com.github.adnanrangrej.focusmodes.domain.model.SessionOutcome
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.SetFocusModeActiveUseCase
import com.github.adnanrangrej.focusmodes.domain.usecase.session.InsertSessionUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class PomodoroTimerService : Service() {


    @Inject
    lateinit var insertSession: InsertSessionUseCase

    @Inject
    lateinit var setFocusModeActive: SetFocusModeActiveUseCase

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)
    private var cycleJob: Job? = null

    // Public state flows for UI
    val totalTime = MutableStateFlow(0L)
    val remainingTime = MutableStateFlow(0L)
    val isRunning = MutableStateFlow(false)
    val isWorking = MutableStateFlow(false)
    val isPaused = MutableStateFlow(false)
    val isFinished = MutableStateFlow(false)

    // Internal state for the service
    private var workDuration = 0L
    private var breakDuration = 0L
    private var focusModeName: String? = null
    private var sessionStartTime: LocalDateTime? = null


    inner class LocalBinder : Binder() {
        fun getTotalTime() = this@PomodoroTimerService.totalTime.asStateFlow()
        fun getRemainingTime() = this@PomodoroTimerService.remainingTime.asStateFlow()
        fun getIsRunning() = this@PomodoroTimerService.isRunning.asStateFlow()
        fun getIsPaused() = this@PomodoroTimerService.isPaused.asStateFlow()
        fun getIsWorking() = this@PomodoroTimerService.isWorking.asStateFlow()
        fun getIsFinished() = this@PomodoroTimerService.isFinished.asStateFlow()
    }

    private val binder = LocalBinder()

    override fun onBind(p0: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val action = intent?.action

        when (action) {

            ACTION_START -> {
                workDuration = intent.getLongExtra(EXTRA_WORK_DURATION, 25 * 60 * 1000L)
                breakDuration = intent.getLongExtra(EXTRA_BREAK_DURATION, 5 * 60 * 1000L)
                focusModeName = intent.getStringExtra(FOCUS_MODE_NAME)
                sessionStartTime = LocalDateTime.now()

                val initialNotification = createNotification(formatTime(workDuration), false)
                startForeground(1, initialNotification)
                startPomodoroCycle(workDuration, breakDuration)
            }

            ACTION_PAUSE_RESUME -> pauseResumeTimer()

            ACTION_STOP -> stopCycleAndSave(SessionOutcome.CANCELLED)

            ACTION_RESET -> startPomodoroCycle(workDuration, breakDuration)
        }

        return START_STICKY
    }

    private fun stopCycleAndSave(outcome: SessionOutcome) {
        val workTimeCompleted =
            if (isWorking.value) totalTime.value - remainingTime.value else workDuration
        val breakTimeCompleted = if (!isWorking.value) totalTime.value - remainingTime.value else 0L

        saveSession(workTimeCompleted, breakTimeCompleted, outcome)
        stopService()
    }

    private fun saveSession(
        effectiveWork: Long,
        effectiveBreak: Long,
        outcome: SessionOutcome
    ) {
        scope.launch {
            val sessionToSave = Session(
                startTime = sessionStartTime?.toEpochSecond(ZoneOffset.UTC) ?: 0L,
                endTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                plannedWorkDurationInSeconds = (workDuration / 1000L).seconds,
                plannedBreakDurationInSeconds = (breakDuration / 1000L).seconds,
                effectiveWorkDurationInSeconds = (effectiveWork / 1000L).seconds,
                effectiveBreakDurationInSeconds = (effectiveBreak / 1000L).seconds,
                focusModeName = focusModeName,
                sessionOutcome = outcome
            )
            insertSession(sessionToSave)
        }
    }

    private fun stopService() {
        if (focusModeName != null) {
            setFocusModeActive(false, null)
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        resetState()
    }

    private fun createNotification(timerLeft: String, isPaused: Boolean): Notification {


        return NotificationCompat.Builder(this, "TIMER_CHANNEL_ID")
            .setContentTitle("Pomodoro Timer")
            .setContentText("Timer remaining: $timerLeft")
            .setSmallIcon(R.drawable.ic_shield_notification)
            .setOngoing(true)
            .addAction(
                if (isPaused) R.drawable.ic_play else R.drawable.ic_pause,
                if (isPaused) "Resume" else "Pause",
                createPauseResumePendingIntent()
            )
            .addAction(R.drawable.ic_reset, "Reset", createResetPendingIntent())
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Stop",
                createStopPendingIntent()
            )
            .build()

    }

    private fun updateNotification(timerLeft: String, isPaused: Boolean) {
        val notification = createNotification(timerLeft, isPaused)
        startForeground(1, notification)
    }

    private fun startPomodoroCycle(
        currentWorkDuration: Long,
        currentBreakDuration: Long,
        isWorkPhase: Boolean = true
    ) {
        cycleJob?.cancel()
        cycleJob = scope.launch {

            if (isWorkPhase) {
                startTimer(currentWorkDuration, true)
                if (!isActive) return@launch

                startTimer(currentBreakDuration, false)
                if (!isActive) return@launch
            } else {
                startTimer(currentBreakDuration, false)
                if (!isActive) return@launch
            }

            // Cycle completed successfully
            isFinished.value = true
            stopCycleAndSave(SessionOutcome.COMPLETED)
        }
    }

    private suspend fun startTimer(duration: Long, isWork: Boolean) {
        totalTime.value = if (isWork) workDuration else breakDuration
        remainingTime.value = duration
        isRunning.value = true
        isPaused.value = false
        isWorking.value = isWork

        while (remainingTime.value > 0) {
            updateNotification(formatTime(remainingTime.value), false)
            delay(1000)
            remainingTime.value -= 1000
        }
        isRunning.value = false
    }

    private fun pauseTimer() {
        cycleJob?.cancel()
        isRunning.value = false
        isPaused.value = true
        updateNotification(formatTime(remainingTime.value), true)
    }

    private fun pauseResumeTimer() {
        if (isRunning.value) {
            pauseTimer()
        } else if (!cycleJob?.isActive.orFalse()) {
            if (isWorking.value) {
                startPomodoroCycle(remainingTime.value, breakDuration, true)
            } else {
                startPomodoroCycle(0, remainingTime.value, false)
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val seconds = millis / 1000
        return "%02d:%02d".format(seconds / 60, seconds % 60)
    }

    private fun createPauseResumePendingIntent(): PendingIntent {
        val intent = Intent(this, PomodoroTimerService::class.java).apply {
            action = ACTION_PAUSE_RESUME
        }

        return PendingIntent.getService(
            this, 1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createStopPendingIntent(): PendingIntent {
        val intent = Intent(this, PomodoroTimerService::class.java).apply {
            action = ACTION_STOP
        }

        return PendingIntent.getService(
            this, 2, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createResetPendingIntent(): PendingIntent {
        val intent = Intent(this, PomodoroTimerService::class.java).apply {
            action = ACTION_RESET
        }
        return PendingIntent.getService(
            this, 3, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onUnbind(intent: Intent?): Boolean {
        resetState() // custom method to clear all flows
        return super.onUnbind(intent)
    }

    private fun resetState() {
        cycleJob?.cancel()
        isRunning.value = false
        isPaused.value = false
        isWorking.value = false
        isFinished.value = false
        remainingTime.value = workDuration
        totalTime.value = workDuration
    }

    override fun onDestroy() {
        setFocusModeActive(false, null)
        if (isRunning.value || isPaused.value) {
            val workTimeCompleted =
                if (isWorking.value) totalTime.value - remainingTime.value else workDuration
            val breakTimeCompleted =
                if (!isWorking.value) totalTime.value - remainingTime.value else 0L
            saveSession(
                effectiveWork = workTimeCompleted,
                effectiveBreak = breakTimeCompleted,
                outcome = SessionOutcome.CANCELLED
            )
        }
        super.onDestroy()
    }


    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE_RESUME = "ACTION_PAUSE_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_RESET = "ACTION_RESET"

        const val EXTRA_WORK_DURATION = "EXTRA_WORK_DURATION"
        const val EXTRA_BREAK_DURATION = "EXTRA_BREAK_DURATION"
        const val FOCUS_MODE_NAME = "FOCUS_MODE_NAME"

    }

    private fun Boolean?.orFalse() = this ?: false
}