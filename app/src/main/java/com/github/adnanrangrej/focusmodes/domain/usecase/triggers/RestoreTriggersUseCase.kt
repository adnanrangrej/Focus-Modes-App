package com.github.adnanrangrej.focusmodes.domain.usecase.triggers

import com.github.adnanrangrej.focusmodes.domain.repository.TriggersRepository
import javax.inject.Inject

class RestoreTriggersUseCase @Inject constructor(
    private val repository: TriggersRepository
) {
    suspend operator fun invoke() {
        repository.restoreAllEnabledTriggers()
    }
}
