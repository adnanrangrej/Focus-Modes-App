package com.github.adnanrangrej.focusmodes.domain.usecase.triggers

import com.github.adnanrangrej.focusmodes.domain.repository.TriggersRepository
import javax.inject.Inject

class DeleteTriggerUseCase @Inject constructor(
    private val repository: TriggersRepository
) {
    suspend operator fun invoke(triggerId: Long) = repository.deleteTrigger(triggerId)
}
