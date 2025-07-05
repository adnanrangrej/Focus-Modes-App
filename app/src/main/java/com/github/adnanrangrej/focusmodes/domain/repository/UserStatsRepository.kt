package com.github.adnanrangrej.focusmodes.domain.repository

import com.github.adnanrangrej.focusmodes.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface UserStatsRepository {

    fun getAllSession(): Flow<List<Session>>
}