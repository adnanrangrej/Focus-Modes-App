package com.github.adnanrangrej.focusmodes.domain.usecase.modes

import com.github.adnanrangrej.focusmodes.domain.repository.FocusModesRepository
import javax.inject.Inject

class ObserveFocusModeActiveUseCase @Inject constructor(
    private val focusModesRepository: FocusModesRepository
) {
    operator fun invoke() = focusModesRepository.isFocusModeEnabled
}