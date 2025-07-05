package com.github.adnanrangrej.focusmodes.ui.screens.modes.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.adnanrangrej.focusmodes.domain.model.FocusMode

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

    Box(modifier = modifier) {
        ModesBody(
            modifier = Modifier.fillMaxSize(),
            modes = modes.value,
            onModeClick = {
                val result = viewModel.toggleFocusMode(it)
                onModeToggled(result)
            },
            activeMode = activeMode,
            onEditClick = navigateToEditFocusMode
        )
        FloatingActionButton(
            onClick = navigateToAddFocusMode,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Mode")
        }
    }
}