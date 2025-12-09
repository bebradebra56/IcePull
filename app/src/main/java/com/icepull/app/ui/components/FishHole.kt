package com.icepull.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.icepull.app.model.Task

@Composable
fun FishHole(
    task: Task,
    iceColor: Color,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRipple by remember { mutableStateOf(false) }
    
    // Animation for fish movement
    val infiniteTransition = rememberInfiniteTransition(label = "fish_swim")
    val fishOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fish_offset"
    )
    
    val fishRotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fish_rotation"
    )

    Box(
        modifier = modifier
            .size(120.dp * task.size.scale)
            .clickable(onClick = { 
                showRipple = true
                onClick()
            }),
        contentAlignment = Alignment.Center
    ) {
        // Ripple effect on tap
        if (showRipple) {
            WaterRipple(
                color = accentColor,
                onComplete = { showRipple = false }
            )
        }
        // Ice hole background
        Box(
            modifier = Modifier
                .size(110.dp * task.size.scale)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.9f),
                            iceColor.copy(alpha = 0.7f),
                            Color(0xFF1976D2).copy(alpha = 0.4f)
                        )
                    )
                )
        )

        // Fish swimming under ice
        Canvas(
            modifier = Modifier
                .size(80.dp * task.size.scale)
                .offset(y = fishOffset.dp)
        ) {
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
                    color = accentColor.copy(alpha = 0.7f)
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
                    color = accentColor.copy(alpha = 0.6f)
                )
                
                // Fish eye
                drawCircle(
                    color = Color.White,
                    radius = 3f,
                    center = Offset(size.width * 0.6f, size.height * 0.45f)
                )
            }
        }

        // Task title below hole
        Text(
            text = task.title,
            fontSize = (12 * task.size.scale).sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black.copy(alpha = 0.7f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp)
                .offset(y = 30.dp)
        )
    }
}

