package com.github.adnanrangrej.focusmodes.data.repository

import com.github.adnanrangrej.focusmodes.data.local.dao.SessionDao
import com.github.adnanrangrej.focusmodes.data.local.mapper.toDomainModel
import com.github.adnanrangrej.focusmodes.domain.model.Session
import com.github.adnanrangrej.focusmodes.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserStatsRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : UserStatsRepository {
    override fun getAllSession(): Flow<List<Session>> = sessionDao.getAllSessions().map {
        it.map { sessionEntity ->
            sessionEntity.toDomainModel()
        }
    }
}