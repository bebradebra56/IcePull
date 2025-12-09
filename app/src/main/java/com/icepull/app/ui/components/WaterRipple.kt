package com.icepull.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WaterRipple(
    color: Color = Color(0xFF2196F3),
    onComplete: () -> Unit = {}
) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(1000, easing = LinearOutSlowInEasing)
        ) { value, _ ->
            progress = value
        }
        onComplete()
    }

    Canvas(modifier = Modifier.size(120.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        for (i in 0..2) {
            val delay = i * 0.2f
            val adjustedProgress = ((progress - delay) / (1f - delay)).coerceIn(0f, 1f)
            val radius = adjustedProgress * 60f
            val alpha = (1f - adjustedProgress) * 0.4f
            
            drawCircle(
                color = color.copy(alpha = alpha.coerceIn(0f, 1f)),
                radius = radius,
                center = Offset(centerX, centerY)
            )
        }
    }
}

