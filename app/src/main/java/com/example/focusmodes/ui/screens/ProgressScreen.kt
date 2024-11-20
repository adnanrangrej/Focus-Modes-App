package com.example.focusmodes.ui.screens

import android.app.Application
import android.os.Build
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusmodes.ui.viewmodels.ProgressViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = viewModel(
        factory = ProgressViewModel.Factory(LocalContext.current.applicationContext as Application)
    )
) {
    val scrollState = rememberScrollState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val totalFocusTime by viewModel.totalFocusTime.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val todaySessions by viewModel.todaySessions.collectAsState()
    
    var chartUpdateTrigger by remember { mutableStateOf(0) }
    
    LaunchedEffect(weeklyStats, totalFocusTime) {
        chartUpdateTrigger = chartUpdateTrigger + 1
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Progress Overview",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard(
                title = "Total Focus Time",
                value = viewModel.formatFocusTime(totalFocusTime)
            )
            StatCard(
                title = "Current Streak",
                value = "$currentStreak days"
            )
            StatCard(
                title = "Today's Sessions",
                value = todaySessions.toString()
            )
        }
        
        // Weekly Focus Time Chart
        ChartCard(
            title = "Weekly Focus Time",
            content = { 
                key(chartUpdateTrigger) {
                    WeeklyFocusChart(weeklyStats.groupBy { 
                        it.startTime.truncatedTo(ChronoUnit.DAYS)
                    })
                }
            }
        )
        
        // Daily Progress Chart
        ChartCard(
            title = "Daily Progress",
            content = { 
                key(chartUpdateTrigger) {
                    DailyProgressChart(weeklyStats)
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeeklyFocusChart(
    dailySessions: Map<LocalDateTime, List<com.example.focusmodes.data.model.Session>>
) {
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                setDrawGridBackground(false)
                
                // Get the last 7 days
                val today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)
                val entries = (6 downTo 0).map { daysAgo ->
                    val date = today.minusDays(daysAgo.toLong())
                    // Find sessions for this day
                    val sessionsForDay = dailySessions.entries.filter { entry ->
                        entry.key.truncatedTo(ChronoUnit.DAYS).isEqual(date)
                    }
                    val totalMinutes = sessionsForDay.flatMap { it.value }
                        .sumOf { it.duration }
                    BarEntry((6 - daysAgo).toFloat(), totalMinutes.toFloat())
                }
                
                val dataSet = BarDataSet(entries, "Minutes").apply {
                    color = primaryColor
                    valueTextSize = 12f
                }
                
                data = BarData(dataSet).apply {
                    setValueFormatter(object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.toInt().toString()
                        }
                    })
                }
                
                xAxis.apply {
                    val dateFormatter = DateTimeFormatter.ofPattern("EEE")
                    val labels = (6 downTo 0).map { daysAgo ->
                        today.minusDays(daysAgo.toLong()).format(dateFormatter)
                    }
                    valueFormatter = IndexAxisValueFormatter(labels.toTypedArray())
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                }
                
                axisLeft.apply {
                    setDrawGridLines(false)
                    axisMinimum = 0f
                    granularity = 1f
                }
                axisRight.isEnabled = false
                
                animateY(1000)
                invalidate()
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DailyProgressChart(
    sessions: List<com.example.focusmodes.data.model.Session>
) {
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                setDrawGridBackground(false)
                
                // Sort sessions by start time and get today's sessions
                val today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)
                val todaySessions = sessions.filter {
                    it.startTime.truncatedTo(ChronoUnit.DAYS).isEqual(today)
                }.sortedBy { it.startTime }
                
                val entries = todaySessions.mapIndexed { index, session ->
                    Entry(index.toFloat(), session.duration.toFloat())
                }
                
                val dataSet = LineDataSet(entries, "Minutes").apply {
                    color = primaryColor
                    setDrawFilled(true)
                    fillColor = primaryColor
                    valueTextSize = 12f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                }
                
                data = LineData(dataSet).apply {
                    setValueFormatter(object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.toInt().toString()
                        }
                    })
                }
                
                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(
                        todaySessions.map { 
                            it.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                        }.toTypedArray()
                    )
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    labelRotationAngle = -45f
                    granularity = 1f
                }
                
                axisLeft.apply {
                    setDrawGridLines(false)
                    axisMinimum = 0f
                    granularity = 1f
                }
                axisRight.isEnabled = false
                
                animateXY(1000, 1000)
                invalidate()
            }
        }
    )
}

@Composable
private fun StatCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
} 