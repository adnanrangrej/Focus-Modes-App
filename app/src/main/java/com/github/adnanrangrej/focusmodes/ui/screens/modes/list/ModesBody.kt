package com.github.adnanrangrej.focusmodes.ui.screens.modes.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.adnanrangrej.focusmodes.domain.model.FocusMode
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTheme

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
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(FocusTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(FocusTheme.spacing.medium),
        contentPadding = PaddingValues(FocusTheme.spacing.medium)
    ) {
        items(modes, key = { it.id }) { mode ->
            ModeItem(
                mode = mode,
                isEnabled = mode == activeMode,
                onEditClick = onEditClick,
                onCheckedChange = onModeClick
            )
        }
    }
}