package com.github.adnanrangrej.focusmodes.domain.usecase.triggers

import com.github.adnanrangrej.focusmodes.domain.model.Trigger
import com.github.adnanrangrej.focusmodes.domain.repository.TriggersRepository
import javax.inject.Inject

class GetTriggerSnapshotUseCase @Inject constructor(
    private val repository: TriggersRepository
) {
    suspend operator fun invoke(triggerId: Long): Trigger? = repository.getTriggerById(triggerId)
}
