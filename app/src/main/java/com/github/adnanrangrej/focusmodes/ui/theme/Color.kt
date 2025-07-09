package com.github.adnanrangrej.focusmodes.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Colors - Deep Focus Blue
val Primary40 = Color(0xFF1565C0) // Main brand color
val Primary80 = Color(0xFF90CAF9)
val Primary90 = Color(0xFFE3F2FD)
val Primary10 = Color(0xFF0D47A1)
val Primary20 = Color(0xFF1976D2)
val Primary30 = Color(0xFF1E88E5)

// Secondary Colors - Productivity Green
val Secondary40 = Color(0xFF2E7D32)
val Secondary80 = Color(0xFFA5D6A7)
val Secondary90 = Color(0xFFE8F5E8)
val Secondary10 = Color(0xFF1B5E20)
val Secondary20 = Color(0xFF388E3C)
val Secondary30 = Color(0xFF43A047)

// Tertiary Colors - Accent Orange for timers
val Tertiary40 = Color(0xFFE65100)
val Tertiary80 = Color(0xFFFFCC80)
val Tertiary90 = Color(0xFFFFF3E0)
val Tertiary10 = Color(0xFFBF360C)
val Tertiary20 = Color(0xFFFF5722)
val Tertiary30 = Color(0xFFFF9800)

// Neutral Colors
val Neutral10 = Color(0xFF191C20)
val Neutral20 = Color(0xFF2D3135)
val Neutral30 = Color(0xFF43474E)
val Neutral40 = Color(0xFF5B5F66)
val Neutral50 = Color(0xFF74777F)
val Neutral60 = Color(0xFF8E9099)
val Neutral70 = Color(0xFFA9ABB2)
val Neutral80 = Color(0xFFC4C6CC)
val Neutral90 = Color(0xFFE0E2E8)
val Neutral95 = Color(0xFFEFF0F6)
val Neutral99 = Color(0xFFFCFDF3)

// Neutral Variant Colors
val NeutralVariant30 = Color(0xFF44474F)
val NeutralVariant50 = Color(0xFF74777F)
val NeutralVariant60 = Color(0xFF8E9099)
val NeutralVariant80 = Color(0xFFC4C6CC)
val NeutralVariant90 = Color(0xFFE0E2E8)

// Error Colors
val Error40 = Color(0xFFBA1A1A)
val Error80 = Color(0xFFFFB4AB)
val Error90 = Color(0xFFFFEDEA)

// Custom Colors for Focus Features
val FocusActive = Color(0xFF4CAF50) // Green for active focus
val FocusInactive = Color(0xFF9E9E9E) // Gray for inactive
val BlockedApp = Color(0xFFFF5252) // Red for blocked apps
val TimerActive = Color(0xFFFF9800) // Orange for active timer
val StatsPositive = Color(0xFF4CAF50) // Green for positive stats
val StatsNeutral = Color(0xFF2196F3) // Blue for neutral stats

// Extended Colors for Focus Features
object FocusColors {
    val focusActive = FocusActive
    val focusInactive = FocusInactive
    val blockedApp = BlockedApp
    val timerActive = TimerActive
    val statsPositive = StatsPositive
    val statsNeutral = StatsNeutral

    // Timer specific colors
    val pomodoroWork = Color(0xFFE53935)
    val pomodoroBreak = Color(0xFF43A047)
    val pomodoroLongBreak = Color(0xFF1E88E5)

    // Focus mode specific colors
    val deepWork = Color(0xFF3F51B5)
    val study = Color(0xFF009688)
    val creative = Color(0xFF9C27B0)
    val meeting = Color(0xFFFF9800)
}