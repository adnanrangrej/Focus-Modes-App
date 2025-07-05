package com.github.adnanrangrej.focusmodes.ui.screens.modes.list

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.adnanrangrej.focusmodes.domain.model.FocusMode
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.GetAllFocusModesUseCase
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.GetFocusModeByIdUseCase
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.GetFocusModeIdUseCase
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.ObserveFocusModeActiveUseCase
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.SetFocusModeActiveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModesScreenViewModel @Inject constructor(
    private val getAllFocusModesUseCase: GetAllFocusModesUseCase,
    private val setFocusModeActiveUseCase: SetFocusModeActiveUseCase,
    private val observeFocusModeActiveUseCase: ObserveFocusModeActiveUseCase,
    private val getFocusModeIdUseCase: GetFocusModeIdUseCase,
    private val getFocusModeByIdUseCase: GetFocusModeByIdUseCase
) : ViewModel() {

    private val _focusModes = getAllFocusModesUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
    val focusModes: StateFlow<List<FocusMode>> = _focusModes

    val activeMode = mutableStateOf<FocusMode?>(null)


    init {
        observeFocusState()
    }

    fun toggleFocusMode(focusMode: FocusMode): FocusMode? {
        if (activeMode.value == focusMode) {
            setFocusModeActiveUseCase(false, null)
            return null
        } else {
            setFocusModeActiveUseCase(true, focusMode.id)
            return focusMode
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeFocusState() {
        viewModelScope.launch {
            observeFocusModeActiveUseCase().flatMapLatest { isActive ->
                if (isActive) {
                    val modeId = getFocusModeIdUseCase()
                    if (modeId != null) {
                        getFocusModeByIdUseCase(modeId) ?: flowOf(null)
                    } else {
                        flowOf(null)
                    }
                } else {
                    flowOf(null)
                }
            }.collect { mode ->
                activeMode.value = mode
            }
        }
    }
}