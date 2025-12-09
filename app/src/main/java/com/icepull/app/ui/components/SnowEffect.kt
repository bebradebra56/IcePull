package com.icepull.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

data class Snowflake(
    var x: Float,
    var y: Float,
    val speed: Float,
    val size: Float,
    val amplitude: Float,
    val frequency: Float
)

@Composable
fun SnowEffect(
    modifier: Modifier = Modifier,
    snowColor: Color = Color.White,
    count: Int = 50
) {
    var snowflakes by remember {
        mutableStateOf(
            List(count) {
                Snowflake(
                    x = Random.nextFloat(),
                    y = Random.nextFloat(),
                    speed = Random.nextFloat() * 0.3f + 0.1f,
                    size = Random.nextFloat() * 4f + 2f,
                    amplitude = Random.nextFloat() * 20f + 10f,
                    frequency = Random.nextFloat() * 0.02f + 0.01f
                )
            }
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // ~60 FPS
            snowflakes = snowflakes.map { flake ->
                var newY = flake.y + flake.speed * 0.01f
                if (newY > 1f) {
                    newY = -0.05f
                }
                flake.copy(y = newY)
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        snowflakes.forEach { flake ->
            val xOffset = sin(flake.y * 100 * flake.frequency) * flake.amplitude
            drawCircle(
                color = snowColor,
                radius = flake.size,
                center = Offset(
                    x = flake.x * size.width + xOffset,
                    y = flake.y * size.height
                ),
                alpha = 0.8f
            )
        }
    }
}

