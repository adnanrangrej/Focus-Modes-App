package com.github.adnanrangrej.focusmodes.domain.repository

import com.github.adnanrangrej.focusmodes.domain.model.Session

interface PomodoroTimerRepository {

    suspend fun insertSession(session: Session): Long

    suspend fun updateSession(session: Session)
}