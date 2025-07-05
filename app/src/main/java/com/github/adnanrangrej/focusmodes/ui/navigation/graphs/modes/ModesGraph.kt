package com.github.adnanrangrej.focusmodes.ui.navigation.graphs.modes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import com.github.adnanrangrej.focusmodes.domain.model.FocusMode
import com.github.adnanrangrej.focusmodes.ui.navigation.destination.modes.ModesScreen
import com.github.adnanrangrej.focusmodes.ui.screens.modes.add.AddModeScreen
import com.github.adnanrangrej.focusmodes.ui.screens.modes.add.AddModeScreenViewModel
import com.github.adnanrangrej.focusmodes.ui.screens.modes.edit.EditModeScreen
import com.github.adnanrangrej.focusmodes.ui.screens.modes.edit.EditModeScreenViewModel
import com.github.adnanrangrej.focusmodes.ui.screens.modes.list.ModesScreen
import com.github.adnanrangrej.focusmodes.ui.screens.modes.list.ModesScreenViewModel

fun EntryProviderBuilder<Any>.modesGraph(
    navigateInCurrentTab: (Any) -> Unit,
    navigateUp: () -> Unit,
    onModeToggled: (FocusMode?) -> Unit
) {

    entry<ModesScreen.ModesMain> {

        val viewModel: ModesScreenViewModel = hiltViewModel()
        ModesScreen(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel,
            navigateToAddFocusMode = { navigateInCurrentTab(ModesScreen.CreateMode) },
            navigateToEditFocusMode = { navigateInCurrentTab(ModesScreen.EditMode(it)) },
            onModeToggled = onModeToggled
        )
    }

    entry<ModesScreen.CreateMode> {
        val viewModel: AddModeScreenViewModel = hiltViewModel()
        AddModeScreen(
            modifier = Modifier.fillMaxSize(),
            navigateBack = navigateUp,
            viewModel = viewModel
        )
    }

    entry<ModesScreen.EditMode> {
        val viewModel = hiltViewModel<EditModeScreenViewModel, EditModeScreenViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(it.modeId)
            }
        )

        EditModeScreen(
            modifier = Modifier.fillMaxSize(),
            navigateBack = navigateUp,
            viewModel = viewModel
        )
    }
}