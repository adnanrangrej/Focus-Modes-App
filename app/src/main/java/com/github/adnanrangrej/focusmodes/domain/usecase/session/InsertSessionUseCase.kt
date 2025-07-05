package com.github.adnanrangrej.focusmodes.domain.usecase.session

import com.github.adnanrangrej.focusmodes.domain.model.Session
import com.github.adnanrangrej.focusmodes.domain.repository.PomodoroTimerRepository
import javax.inject.Inject

class InsertSessionUseCase @Inject constructor(
    private val pomodoroTimerRepository: PomodoroTimerRepository
) {
    suspend operator fun invoke(session: Session) = pomodoroTimerRepository.insertSession(session)
}