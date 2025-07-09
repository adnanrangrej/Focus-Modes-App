package com.github.adnanrangrej.focusmodes.ui.screens.modes.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.adnanrangrej.focusmodes.domain.model.FocusMode
import com.github.adnanrangrej.focusmodes.ui.theme.FocusComponentShapes
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTheme

@Composable
fun ModesScreen(
    modifier: Modifier = Modifier,
    navigateToAddFocusMode: () -> Unit,
    navigateToEditFocusMode: (Long) -> Unit,
    onModeToggled: (FocusMode?) -> Unit,
    viewModel: ModesScreenViewModel
) {

    val modes = viewModel.focusModes.collectAsState(initial = emptyList())
    val activeMode by viewModel.activeMode

    Box(modifier = modifier.fillMaxSize()) {
        ModesBody(
            modes = modes.value,
            onModeClick = { mode ->
                val result = viewModel.toggleFocusMode(mode)
                onModeToggled(result)
            },
            activeMode = activeMode,
            onEditClick = navigateToEditFocusMode
        )
        FloatingActionButton(
            onClick = navigateToAddFocusMode,
            shape = FocusComponentShapes.fab,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = FocusTheme.elevation.large
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = FocusTheme.spacing.medium,
                    bottom = FocusTheme.spacing.medium
                )
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Mode")
        }
    }
}