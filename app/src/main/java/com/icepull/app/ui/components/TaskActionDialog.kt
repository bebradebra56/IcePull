package com.icepull.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.icepull.app.model.Task
import kotlinx.coroutines.delay

@Composable
fun TaskActionDialog(
    task: Task,
    accentColor: Color,
    onPull: () -> Unit,
    onRelease: () -> Unit,
    onDismiss: () -> Unit
) {
    var isPulling by remember { mutableStateOf(false) }
    var isReleasing by remember { mutableStateOf(false) }
    var showPullEffect by remember { mutableStateOf(false) }
    var showReleaseEffect by remember { mutableStateOf(false) }
    
    val pullScale by animateFloatAsState(
        targetValue = if (isPulling && !showPullEffect) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pull_scale"
    )
    
    val releaseAlpha by animateFloatAsState(
        targetValue = if (isReleasing && !showReleaseEffect) 0.5f else 1f,
        animationSpec = tween(300),
        label = "release_alpha"
    )

    LaunchedEffect(isPulling) {
        if (isPulling) {
            delay(300)
            showPullEffect = true
        }
    }
    
    LaunchedEffect(isReleasing) {
        if (isReleasing) {
            delay(200)
            showReleaseEffect = true
        }
    }

    if (showPullEffect) {
        Dialog(onDismissRequest = {}) {
            Box(modifier = Modifier.fillMaxSize()) {
                PullEffect(
                    color = accentColor,
                    onComplete = onPull
                )
            }
        }
        return
    }

    if (showReleaseEffect) {
        Dialog(onDismissRequest = {}) {
            Box(modifier = Modifier.fillMaxSize()) {
                ReleaseEffect(
                    color = accentColor,
                    onComplete = onRelease
                )
            }
        }
        return
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .scale(if (isReleasing) 0.8f else pullScale)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White,
                                Color(0xFFF5F5F5)
                            )
                        )
                    )
                    .padding(24.dp)
                    .clickable(enabled = false) {},
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Fish icon with animation
                FishIcon(
                    accentColor = accentColor,
                    size = task.size.scale,
                    modifier = Modifier.size(80.dp * task.size.scale)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = task.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Size: ${task.size.displayName}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Pull button
                Button(
                    onClick = { isPulling = true },
                    enabled = !isPulling && !isReleasing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (isPulling) "Pulling..." else "Pull (Complete)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Release button
                OutlinedButton(
                    onClick = { isReleasing = true },
                    enabled = !isPulling && !isReleasing,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (isReleasing) "Releasing..." else "Release (Delete)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun FishIcon(
    accentColor: Color,
    size: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fish_bounce")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Box(
        modifier = modifier.offset(y = (-bounce).dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🐟",
            fontSize = (48 * size).sp
        )
    }
}

