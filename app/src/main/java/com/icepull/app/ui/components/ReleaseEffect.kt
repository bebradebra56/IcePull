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

@Composable
fun ReleaseEffect(
    color: Color,
    onComplete: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        // Animate fish sinking
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(600, easing = FastOutLinearInEasing)
        ) { value, _ ->
            progress = value
        }
        delay(100)
        onComplete()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Water ripples
        Canvas(modifier = Modifier.size(200.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            for (i in 0..2) {
                val radius = progress * (80f + i * 40f)
                val alpha = (1f - progress) * 0.5f
                
                drawCircle(
                    color = Color(0xFF2196F3).copy(alpha = alpha.coerceIn(0f, 1f)),
                    radius = radius,
                    center = Offset(centerX, centerY)
                )
            }
        }

        // Fish sinking down
        val fishY = progress * 150f
        val fishAlpha = 1f - progress
        val fishRotation = progress * 45f

        Box(
            modifier = Modifier
                .size((80 * (1f - progress * 0.5f)).dp)
                .align(Alignment.Center)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                rotate(fishRotation) {
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
                        color = color.copy(alpha = fishAlpha)
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
                        color = color.copy(alpha = fishAlpha * 0.8f)
                    )

                    // Fish eye
                    drawCircle(
                        color = Color.White.copy(alpha = fishAlpha),
                        radius = 4f,
                        center = Offset(size.width * 0.6f, size.height * 0.45f)
                    )
                }
            }
        }
    }
}

