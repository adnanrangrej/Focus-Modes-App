package com.github.adnanrangrej.focusmodes.domain.usecase.triggers

import com.github.adnanrangrej.focusmodes.domain.repository.TriggersRepository
import javax.inject.Inject

class SetTriggerEnabledUseCase @Inject constructor(
    private val repository: TriggersRepository
) {
    suspend operator fun invoke(triggerId: Long, enabled: Boolean) {
        repository.setTriggerEnabled(triggerId, enabled)
    }
}
