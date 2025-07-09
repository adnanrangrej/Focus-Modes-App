package com.github.adnanrangrej.focusmodes.ui.screens.userstats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.adnanrangrej.focusmodes.ui.theme.FocusComponentShapes
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTheme
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTypography

@Composable
fun UserStatsScreen(
    modifier: Modifier = Modifier,
    viewModel: UserStatsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(FocusTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(FocusTheme.spacing.large)
        ) {
            item {
                Text(
                    text = "Your Focus Stats",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(FocusTheme.spacing.medium)
                ) {
                    val focusHours = uiState.totalFocusTimeToday.inWholeHours
                    val focusMinutes = uiState.totalFocusTimeToday.inWholeMinutes % 60

                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.AutoMirrored.Filled.ShowChart,
                        label = "Today's Focus",
                        value = "${focusHours}h ${focusMinutes}m"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.QueryStats,
                        label = "Completed",
                        value = "${uiState.sessionsCompletedToday}"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Star,
                        label = "Best Streak",
                        value = "${uiState.longestStreak} days"
                    )
                }
            }

            item {
                WeeklyFocusChart(dailyFocusMinutes = uiState.weeklyFocusMinutes)
            }

            item {
                Text(
                    text = "Recent Sessions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.recentSessions.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(vertical = FocusTheme.spacing.huge)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(FocusTheme.spacing.medium)
                    ) {
                        Text(
                            text = "No sessions recorded yet. Start a timer to see your stats!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            onClick = { viewModel.insertDummyData() },
                            shape = FocusComponentShapes.button
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(
                                "Generate Sample Data",
                                style = FocusTypography.primaryButton
                            )
                        }
                    }
                }
            } else {
                items(uiState.recentSessions, key = { it.id }) { session ->
                    SessionHistoryItem(session = session)
                }
            }
        }
    }
}