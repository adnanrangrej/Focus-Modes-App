package com.github.adnanrangrej.focusmodes.domain.usecase.modes

import com.github.adnanrangrej.focusmodes.domain.model.FocusMode
import com.github.adnanrangrej.focusmodes.domain.repository.FocusModesRepository
import javax.inject.Inject

class UpdateFocusModeUseCase @Inject constructor(
    private val focusModeRepository: FocusModesRepository
) {
    suspend operator fun invoke(mode: FocusMode) = focusModeRepository.updateMode(mode)
}