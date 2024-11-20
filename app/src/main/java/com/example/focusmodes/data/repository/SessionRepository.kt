package com.example.focusmodes.data.repository

import com.example.focusmodes.data.db.SessionDao
import com.example.focusmodes.data.model.Session
import kotlinx.coroutines.flow.Flow

class SessionRepository(private val sessionDao: SessionDao) {
    fun getAllSessions(): Flow<List<Session>> = sessionDao.getAllSessions()

    fun getWeeklyStats(): Flow<List<Session>> = sessionDao.getRecentSessions()

    fun getTodayCompletedSessions(): Flow<Int> = sessionDao.getTodayCompletedSessions()

    fun getWeeklyFocusTime(): Flow<Int?> = sessionDao.getWeeklyFocusTime()

    fun getCurrentStreak(): Flow<Int> = sessionDao.getStreakDays()

    suspend fun addSession(session: Session) {
        sessionDao.insertSession(session)
    }

    suspend fun updateSession(session: Session) {
        sessionDao.updateSession(session)
    }
} 