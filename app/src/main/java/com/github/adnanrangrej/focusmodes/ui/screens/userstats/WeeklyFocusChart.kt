package com.github.adnanrangrej.focusmodes.ui.screens.userstats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.adnanrangrej.focusmodes.ui.theme.FocusComponentShapes
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTheme

@Composable
fun WeeklyFocusChart(
    modifier: Modifier = Modifier,
    dailyFocusMinutes: List<Float>
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = FocusComponentShapes.statsCard
    ) {
        Column(modifier = Modifier.padding(FocusTheme.spacing.medium)) {
            Text(
                text = "Last 7 Days",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(FocusTheme.spacing.medium))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val maxVal = dailyFocusMinutes.maxOrNull()?.coerceAtLeast(1f) ?: 1f
                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                dailyFocusMinutes.forEachIndexed { index, value ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .width(24.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(value / maxVal)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        shape = FocusComponentShapes.chip
                                    )
                            )
                        }
                        Text(
                            text = days.getOrElse(index) { "" },
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = FocusTheme.spacing.extraSmall)
                        )
                    }
                }
            }
        }
    }
}