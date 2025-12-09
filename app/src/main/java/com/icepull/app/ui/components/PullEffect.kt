package com.icepull.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun PullEffect(
    color: Color,
    onComplete: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var sparkles by remember {
        mutableStateOf(
            List(20) {
                Sparkle(
                    Random.nextFloat(),
                    Random.nextFloat(),
                    Random.nextFloat() * 360f,
                    Random.nextFloat() * 2f + 1f
                )
            }
        )
    }

    LaunchedEffect(Unit) {
        // Animate fish pulling out
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        ) { value, _ ->
            progress = value
        }
        delay(300)
        onComplete()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Water splash effect
        Canvas(modifier = Modifier.size(200.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            // Splash circles
            for (i in 0..3) {
                val radius = (progress * 100f) + (i * 30f)
                val alpha = (1f - progress) * (1f - i * 0.2f)
                
                drawCircle(
                    color = Color(0xFF2196F3).copy(alpha = alpha.coerceIn(0f, 1f)),
                    radius = radius,
                    center = Offset(centerX, centerY)
                )
            }

            // Sparkles
            sparkles.forEach { sparkle ->
                val sparkleX = centerX + sin(sparkle.angle) * progress * 150f
                val sparkleY = centerY - progress * 200f + sparkle.offsetY * 50f
                val sparkleAlpha = (1f - progress) * 0.8f

                rotate(sparkle.rotation + progress * 360f, Offset(sparkleX, sparkleY)) {
                    val starPath = Path().apply {
                        val points = 5
                        val outerRadius = sparkle.size * 8f
                        val innerRadius = outerRadius * 0.4f

                        for (i in 0 until points * 2) {
                            val radius = if (i % 2 == 0) outerRadius else innerRadius
                            val angle = (i * Math.PI / points).toFloat()
                            val x = sparkleX + radius * sin(angle)
                            val y = sparkleY - radius * kotlin.math.cos(angle)

                            if (i == 0) moveTo(x, y)
                            else lineTo(x, y)
                        }
                        close()
                    }

                    drawPath(
                        path = starPath,
                        color = Color.White.copy(alpha = sparkleAlpha.coerceIn(0f, 1f))
                    )
                }
            }
        }

        // Fish jumping out
        val fishScale = if (progress < 0.5f) progress * 2f else 2f - progress * 2f
        val fishY = -progress * 150f

        Box(
            modifier = Modifier
                .size((80 * (1f + fishScale * 0.5f)).dp)
                .align(Alignment.Center)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                rotate(-progress * 30f + sin(progress * 10f) * 10f) {
                    // Fish body
                    val fishPath = Path().apply {
                        moveTo(size.width * 0.3f, size.height * 0.5f)
                        cubicTo(
                            size.width * 0.2f, size.height * 0.3f,
                            size.width * 0.5f, size.height * 0.2f,
                            size.width * 0.7f, size.height * 0.5f
                        )
                        cubicTo(
                            size.width * 0.5f, size.height * 0.8f,
                            size.width * 0.2f, size.height * 0.7f,
                            size.width * 0.3f, size.height * 0.5f
                        )
                    }

                    drawPath(
                        path = fishPath,
                        color = color
                    )

                    // Fish tail
                    val tailPath = Path().apply {
                        moveTo(size.width * 0.3f, size.height * 0.5f)
                        lineTo(size.width * 0.1f, size.height * 0.3f)
                        lineTo(size.width * 0.15f, size.height * 0.5f)
                        lineTo(size.width * 0.1f, size.height * 0.7f)
                        close()
                    }

                    drawPath(
                        path = tailPath,
                        color = color.copy(alpha = 0.8f)
                    )

                    // Fish eye
                    drawCircle(
                        color = Color.White,
                        radius = 4f,
                        center = Offset(size.width * 0.6f, size.height * 0.45f)
                    )

                    // Fish pupil
                    drawCircle(
                        color = Color.Black,
                        radius = 2f,
                        center = Offset(size.width * 0.6f, size.height * 0.45f)
                    )
                }
            }
        }
    }
}

data class Sparkle(
    val angle: Float,
    val offsetY: Float,
    val rotation: Float,
    val size: Float
)

