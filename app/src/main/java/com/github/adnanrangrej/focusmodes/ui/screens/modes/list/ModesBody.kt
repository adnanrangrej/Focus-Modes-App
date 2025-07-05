package com.github.adnanrangrej.focusmodes.ui.screens.modes.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.adnanrangrej.focusmodes.domain.model.FocusMode

@Composable
fun ModesBody(
    modifier: Modifier = Modifier,
    modes: List<FocusMode> = emptyList(),
    onModeClick: (FocusMode) -> Unit = {},
    activeMode: FocusMode? = null,
    onEditClick: (Long) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(modes) { mode ->
            ModeItem(
                mode = mode,
                isEnabled = mode == activeMode,
                onEditClick = onEditClick,
                onCheckedChange = onModeClick
            )
        }
    }
}