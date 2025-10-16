package com.github.adnanrangrej.focusmodes.domain.usecase.modes

import com.github.adnanrangrej.focusmodes.domain.model.FocusMode
import com.github.adnanrangrej.focusmodes.domain.repository.FocusModesRepository
import javax.inject.Inject

class GetFocusModeSnapshotUseCase @Inject constructor(
    private val repository: FocusModesRepository
) {
    suspend operator fun invoke(id: Long): FocusMode? = repository.getModeSnapshot(id)
}
