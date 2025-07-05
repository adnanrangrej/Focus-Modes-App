package com.github.adnanrangrej.focusmodes.ui.screens.modes.list

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.adnanrangrej.focusmodes.domain.model.FocusMode

@Composable
fun ModeItem(
    modifier: Modifier = Modifier,
    mode: FocusMode,
    isEnabled: Boolean,
    onEditClick: (Long) -> Unit,
    onCheckedChange: (FocusMode) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = MaterialTheme.shapes.large
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = mode.name,
                style = MaterialTheme.typography.titleLarge
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
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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