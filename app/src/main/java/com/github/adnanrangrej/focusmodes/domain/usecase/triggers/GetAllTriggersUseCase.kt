package com.github.adnanrangrej.focusmodes.domain.usecase.triggers

import com.github.adnanrangrej.focusmodes.domain.model.Trigger
import com.github.adnanrangrej.focusmodes.domain.repository.TriggersRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAllTriggersUseCase @Inject constructor(
    private val repository: TriggersRepository
) {
    operator fun invoke(): Flow<List<Trigger>> = repository.observeTriggers()
}
