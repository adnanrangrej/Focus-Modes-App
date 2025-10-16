package com.github.adnanrangrej.focusmodes.domain.usecase.triggers

import com.github.adnanrangrej.focusmodes.domain.model.Trigger
import com.github.adnanrangrej.focusmodes.domain.repository.TriggersRepository
import javax.inject.Inject

class InsertTriggerUseCase @Inject constructor(
    private val repository: TriggersRepository
) {
    suspend operator fun invoke(trigger: Trigger): Long = repository.insertTrigger(trigger)
}
