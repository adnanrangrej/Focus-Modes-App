package com.github.adnanrangrej.focusmodes.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Custom shapes for the app
val FocusShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Custom shape variations for specific components
object FocusComponentShapes {
    val timerCard = RoundedCornerShape(20.dp)
    val focusModeCard = RoundedCornerShape(16.dp)
    val statsCard = RoundedCornerShape(14.dp)
    val bottomSheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val button = RoundedCornerShape(12.dp)
    val buttonSmall = RoundedCornerShape(8.dp)
    val chip = RoundedCornerShape(20.dp)
    val overlay = RoundedCornerShape(24.dp)
    val dialog = RoundedCornerShape(20.dp)
    val fab = RoundedCornerShape(16.dp)
}