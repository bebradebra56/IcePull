package com.icepull.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin
import kotlin.random.Random

data class IceCrack(
    val startX: Float,
    val startY: Float,
    val segments: List<Offset>
)

@Composable
fun IceBackground(
    modifier: Modifier = Modifier,
    iceColor: Color,
    backgroundColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ice_shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val iceCracks = remember {
        List(8) {
            val startX = Random.nextFloat()
            val startY = Random.nextFloat()
            val segments = mutableListOf<Offset>()
            
            var currentX = startX
            var currentY = startY
            
            repeat(Random.nextInt(3, 7)) {
                currentX += (Random.nextFloat() - 0.5f) * 0.2f
                currentY += (Random.nextFloat() - 0.5f) * 0.2f
                segments.add(Offset(currentX, currentY))
            }
            
            IceCrack(startX, startY, segments)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        // Ice cracks
        iceCracks.forEach { crack ->
            val path = Path().apply {
                moveTo(crack.startX * size.width, crack.startY * size.height)
                crack.segments.forEach { segment ->
                    lineTo(segment.x * size.width, segment.y * size.height)
                }
            }
            
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.2f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )
        }

        // Ice shimmer effect
        for (i in 0..5) {
            val shimmerX = (shimmerOffset + i * 0.2f) % 1f
            val shimmerAlpha = (1f - kotlin.math.abs(shimmerX - 0.5f) * 2f) * 0.3f
            
            drawCircle(
                color = Color.White.copy(alpha = shimmerAlpha),
                radius = 150f,
                center = Offset(
                    x = shimmerX * size.width,
                    y = (sin(shimmerX * 3.14f) * 0.3f + 0.3f) * size.height
                )
            )
        }
    }
}

