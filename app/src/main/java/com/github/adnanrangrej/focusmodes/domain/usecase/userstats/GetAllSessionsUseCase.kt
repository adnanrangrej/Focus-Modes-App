package com.github.adnanrangrej.focusmodes.domain.usecase.userstats

import com.github.adnanrangrej.focusmodes.domain.repository.UserStatsRepository
import javax.inject.Inject

class GetAllSessionsUseCase @Inject constructor(
    private val userStatsRepository: UserStatsRepository
) {
    operator fun invoke() = userStatsRepository.getAllSession()
}