package com.github.adnanrangrej.focusmodes.data.repository

import com.github.adnanrangrej.focusmodes.data.local.dao.SessionDao
import com.github.adnanrangrej.focusmodes.data.local.mapper.toEntityModel
import com.github.adnanrangrej.focusmodes.domain.model.Session
import com.github.adnanrangrej.focusmodes.domain.repository.PomodoroTimerRepository
import javax.inject.Inject

class PomodoroTimerRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : PomodoroTimerRepository {
    override suspend fun insertSession(session: Session) = sessionDao.insertSession(session.toEntityModel())

    override suspend fun updateSession(session: Session) = sessionDao.updateSession(session.toEntityModel())
}