package com.github.adnanrangrej.focusmodes.domain.usecase.triggers

import com.github.adnanrangrej.focusmodes.domain.model.Trigger
import com.github.adnanrangrej.focusmodes.domain.repository.TriggersRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTriggerUseCase @Inject constructor(
    private val repository: TriggersRepository
) {
    operator fun invoke(triggerId: Long): Flow<Trigger?> = repository.observeTrigger(triggerId)
}
