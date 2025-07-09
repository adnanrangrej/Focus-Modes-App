package com.github.adnanrangrej.focusmodes.ui.screens.modes.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.adnanrangrej.focusmodes.domain.model.FocusMode
import com.github.adnanrangrej.focusmodes.ui.theme.FocusComponentShapes
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTheme
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTypography

@Composable
fun ModeItem(
    modifier: Modifier = Modifier,
    mode: FocusMode,
    isEnabled: Boolean,
    onEditClick: (Long) -> Unit,
    onCheckedChange: (FocusMode) -> Unit
) {

    // Animate the card's color based on whether it's the active mode
    val cardContainerColor by animateColorAsState(
        targetValue = if (isEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        label = "CardContainerColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = FocusComponentShapes.focusModeCard,
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = FocusTheme.elevation.small
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(FocusTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(FocusTheme.spacing.small)
        ) {

            Text(
                text = mode.name,
                style = FocusTypography.focusModeTitle
            )

            Text(
                text = mode.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "${mode.blockedAppPackages.size} Apps Blocked",
                    style = FocusTypography.cardSubtitle, //
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(FocusTheme.spacing.extraSmall)
                ) {
                    IconButton(onClick = { onEditClick(mode.id) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Mode"
                        )
                    }

                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { onCheckedChange(mode) }
                    )
                }
            }
        }
    }
}