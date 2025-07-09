package com.github.adnanrangrej.focusmodes.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// Light Color Scheme
val LightColors = lightColorScheme(
    primary = Primary40,
    onPrimary = Color.White,
    primaryContainer = Primary90,
    onPrimaryContainer = Primary10,

    secondary = Secondary40,
    onSecondary = Color.White,
    secondaryContainer = Secondary90,
    onSecondaryContainer = Secondary10,

    tertiary = Tertiary40,
    onTertiary = Color.White,
    tertiaryContainer = Tertiary90,
    onTertiaryContainer = Tertiary10,

    error = Error40,
    onError = Color.White,
    errorContainer = Error90,
    onErrorContainer = Color(0xFF410E0B),

    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,

    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,

    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,

    scrim = Color.Black,
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    inversePrimary = Primary80,

    surfaceDim = Color(0xFFD9D9E0),
    surfaceBright = Neutral99,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF3F3FA),
    surfaceContainer = Color(0xFFEDEDF4),
    surfaceContainerHigh = Color(0xFFE7E8EE),
    surfaceContainerHighest = Color(0xFFE2E2E9)
)

// Dark Color Scheme
val DarkColors = darkColorScheme(
    primary = Primary80,
    onPrimary = Primary20,
    primaryContainer = Primary30,
    onPrimaryContainer = Primary90,

    secondary = Secondary80,
    onSecondary = Secondary20,
    secondaryContainer = Secondary30,
    onSecondaryContainer = Secondary90,

    tertiary = Tertiary80,
    onTertiary = Tertiary20,
    tertiaryContainer = Tertiary30,
    onTertiaryContainer = Tertiary90,

    error = Error80,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Error90,

    background = Neutral10,
    onBackground = Neutral90,
    surface = Neutral10,
    onSurface = Neutral90,

    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,

    outline = NeutralVariant60,
    outlineVariant = NeutralVariant30,

    scrim = Color.Black,
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral20,
    inversePrimary = Primary40,

    surfaceDim = Neutral10,
    surfaceBright = Color(0xFF3A3E42),
    surfaceContainerLowest = Color(0xFF0E1014),
    surfaceContainerLow = Color(0xFF191C20),
    surfaceContainer = Color(0xFF1D2024),
    surfaceContainerHigh = Color(0xFF272A2F),
    surfaceContainerHighest = Color(0xFF32353A)
)

@Composable
fun FocusModesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = FocusShapes,
        content = content
    )
}

// Theme extensions for easy access to custom colors
@Composable
fun MaterialTheme.focusColors(): FocusColorsScheme {
    return if (isSystemInDarkTheme()) {
        FocusColorsScheme(
            focusActive = FocusColors.focusActive,
            focusInactive = Color(0xFF616161),
            blockedApp = Color(0xFFEF5350),
            timerActive = FocusColors.timerActive,
            statsPositive = FocusColors.statsPositive,
            statsNeutral = FocusColors.statsNeutral,
            pomodoroWork = Color(0xFFEF5350),
            pomodoroBreak = Color(0xFF66BB6A),
            pomodoroLongBreak = Color(0xFF42A5F5),
            deepWork = Color(0xFF5C6BC0),
            study = Color(0xFF26A69A),
            creative = Color(0xFFAB47BC),
            meeting = Color(0xFFFFB74D)
        )
    } else {
        FocusColorsScheme(
            focusActive = FocusColors.focusActive,
            focusInactive = FocusColors.focusInactive,
            blockedApp = FocusColors.blockedApp,
            timerActive = FocusColors.timerActive,
            statsPositive = FocusColors.statsPositive,
            statsNeutral = FocusColors.statsNeutral,
            pomodoroWork = FocusColors.pomodoroWork,
            pomodoroBreak = FocusColors.pomodoroBreak,
            pomodoroLongBreak = FocusColors.pomodoroLongBreak,
            deepWork = FocusColors.deepWork,
            study = FocusColors.study,
            creative = FocusColors.creative,
            meeting = FocusColors.meeting
        )
    }
}

// Custom color scheme for focus-specific colors
data class FocusColorsScheme(
    val focusActive: Color,
    val focusInactive: Color,
    val blockedApp: Color,
    val timerActive: Color,
    val statsPositive: Color,
    val statsNeutral: Color,
    val pomodoroWork: Color,
    val pomodoroBreak: Color,
    val pomodoroLongBreak: Color,
    val deepWork: Color,
    val study: Color,
    val creative: Color,
    val meeting: Color
)

// Preview themes for development
@Composable
fun FocusModesPreviewTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = FocusShapes,
        content = content
    )
}

// Utility composables for theme switching
@Composable
fun ThemedPreview(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    FocusModesPreviewTheme(darkTheme = darkTheme) {
        content()
    }
}

// Custom theme properties
object FocusTheme {
    val elevation = FocusElevation()
    val spacing = FocusSpacing()
    val animation = FocusAnimationSpecs()
}

// Elevation values
class FocusElevation {
    val small = 2.dp
    val medium = 4.dp
    val large = 8.dp
    val extraLarge = 12.dp
}

// Spacing values
class FocusSpacing {
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
    val huge = 48.dp
}

// Animation specifications
class FocusAnimationSpecs {
    val fast = 200
    val medium = 300
    val slow = 500
    val veryFast = 100
}

// Screen-specific theme configurations
object ScreenThemes {
    // Timer screen colors
    @Composable
    fun timerColors() = mapOf(
        "work" to MaterialTheme.focusColors().pomodoroWork,
        "shortBreak" to MaterialTheme.focusColors().pomodoroBreak,
        "longBreak" to MaterialTheme.focusColors().pomodoroLongBreak
    )

    // Focus mode colors
    @Composable
    fun focusModeColors() = mapOf(
        "Deep Work" to MaterialTheme.focusColors().deepWork,
        "Study" to MaterialTheme.focusColors().study,
        "Creative" to MaterialTheme.focusColors().creative,
        "Meeting" to MaterialTheme.focusColors().meeting
    )

    // Stats screen colors
    @Composable
    fun statsColors() = mapOf(
        "positive" to MaterialTheme.focusColors().statsPositive,
        "neutral" to MaterialTheme.focusColors().statsNeutral,
        "active" to MaterialTheme.focusColors().focusActive
    )
}