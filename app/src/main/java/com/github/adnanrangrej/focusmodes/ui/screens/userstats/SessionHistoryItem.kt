package com.github.adnanrangrej.focusmodes.ui.screens.userstats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.adnanrangrej.focusmodes.domain.model.Session
import com.github.adnanrangrej.focusmodes.domain.model.SessionOutcome
import com.github.adnanrangrej.focusmodes.ui.theme.FocusComponentShapes
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTheme
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTypography
import com.github.adnanrangrej.focusmodes.ui.theme.focusColors
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun SessionHistoryItem(
    modifier: Modifier = Modifier,
    session: Session
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d") }
    val focusColors = MaterialTheme.focusColors()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = FocusComponentShapes.focusModeCard
    ) {
        Row(
            modifier = Modifier.padding(FocusTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val outcomeIcon =
                if (session.sessionOutcome == SessionOutcome.COMPLETED) Icons.Default.CheckCircle else Icons.Default.HighlightOff
            val outcomeColor =
                if (session.sessionOutcome == SessionOutcome.COMPLETED) focusColors.statsPositive else focusColors.blockedApp

            Icon(
                imageVector = outcomeIcon,
                contentDescription = session.sessionOutcome.name,
                tint = outcomeColor,
                modifier = Modifier.size(40.dp)
            )

            Spacer(Modifier.width(FocusTheme.spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.focusModeName ?: "Quick Timer",
                    style = FocusTypography.focusModeTitle
                )
                Text(
                    text = "Work: ${session.effectiveWorkDurationInSeconds.inWholeMinutes}m / ${session.plannedWorkDurationInSeconds.inWholeMinutes}m",
                    style = FocusTypography.cardSubtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Break: ${session.effectiveBreakDurationInSeconds.inWholeMinutes}m / ${session.plannedBreakDurationInSeconds.inWholeMinutes}m",
                    style = FocusTypography.cardSubtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(FocusTheme.spacing.medium))

            Text(
                text = session.endTime?.let {
                    Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).format(dateFormatter)
                } ?: "",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}