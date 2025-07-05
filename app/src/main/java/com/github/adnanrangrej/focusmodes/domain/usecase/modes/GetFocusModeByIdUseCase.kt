package com.github.adnanrangrej.focusmodes.domain.usecase.modes

import com.github.adnanrangrej.focusmodes.domain.repository.FocusModesRepository
import javax.inject.Inject

class GetFocusModeByIdUseCase @Inject constructor(
    private val repository: FocusModesRepository
) {
    operator fun invoke(id: Long) = repository.getModeById(id)
}