package com.icepull.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.icepull.app.model.AppTheme
import com.icepull.app.model.Task
import com.icepull.app.ui.components.FishHole
import com.icepull.app.ui.components.SnowEffect
import com.icepull.app.ui.components.TaskActionDialog

@Composable
fun MainScreen(
    tasks: List<Task>,
    currentTheme: AppTheme,
    onTaskClick: (Task) -> Unit,
    onAddClick: () -> Unit,
    onStatsClick: () -> Unit,
    onThemeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAllTasksSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Фон за системными барами
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            currentTheme.backgroundColor,
                            currentTheme.iceColor
                        )
                    )
                )
        )
        
        // Ice background texture
        com.icepull.app.ui.components.IceBackground(
            iceColor = currentTheme.iceColor,
            backgroundColor = currentTheme.backgroundColor
        )

        // Snow effect
        SnowEffect(
            snowColor = currentTheme.snowColor,
            count = 50
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar
            TopBar(
                onStatsClick = onStatsClick,
                onThemeClick = onThemeClick,
                accentColor = currentTheme.accentColor,
                onPolicyClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://iccepull.com/privacy-policy.html"))
                    context.startActivity(intent)
                }
            )

            // Main content
            if (tasks.isEmpty()) {
                EmptyState(
                    onAddClick = onAddClick,
                    accentColor = currentTheme.accentColor,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.weight(1f)) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(tasks.take(6), key = { it.id }) { task ->
                            FishHole(
                                task = task,
                                iceColor = currentTheme.iceColor,
                                accentColor = currentTheme.accentColor,
                                onClick = { onTaskClick(task) }
                            )
                        }
                    }
                    
                    // Show task count if more than 6
                    if (tasks.size > 6) {
                        val scale by rememberInfiniteTransition(label = "more_tasks_pulse")
                            .animateFloat(
                                initialValue = 1f,
                                targetValue = 1.05f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1500, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "scale"
                            )
                        
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                                .scale(scale)
                                .shadow(8.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(currentTheme.accentColor)
                                .clickable { showAllTasksSheet = true }
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🐟",
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "+${tasks.size - 6} more tasks below ice",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "👆",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Add Button
        com.icepull.app.ui.components.FloatingAddButton(
            onClick = onAddClick,
            color = currentTheme.accentColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
        
        // All tasks sheet with animation
        AnimatedVisibility(
            visible = showAllTasksSheet,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showAllTasksSheet = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                com.icepull.app.ui.components.TaskListSheet(
                    tasks = tasks,
                    accentColor = currentTheme.accentColor,
                    onTaskClick = { task ->
                        showAllTasksSheet = false
                        onTaskClick(task)
                    },
                    onClose = { showAllTasksSheet = false },
                    modifier = Modifier.clickable(enabled = false) {}
                )
            }
        }
    }
}

@Composable
fun TopBar(
    onStatsClick: () -> Unit,
    onThemeClick: () -> Unit,
    onPolicyClick: () -> Unit,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App title
        Text(
            text = "Ice Pull",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stats button
            IconButton(
                onClick = onStatsClick,
                modifier = Modifier
                    .size(48.dp)
                    .shadow(4.dp, CircleShape)
                    .background(Color.White, CircleShape)
            ) {
                Text(text = "📊", fontSize = 20.sp)
            }

            // Theme button
            IconButton(
                onClick = onThemeClick,
                modifier = Modifier
                    .size(48.dp)
                    .shadow(4.dp, CircleShape)
                    .background(Color.White, CircleShape)
            ) {
                Text(text = "🎨", fontSize = 20.sp)
            }

            IconButton(
                onClick = onPolicyClick,
                modifier = Modifier
                    .size(48.dp)
                    .shadow(4.dp, CircleShape)
                    .background(Color.White, CircleShape)
            ) {
                Text(text = "\uD83D\uDCC4", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun EmptyState(
    onAddClick: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "❄️",
            fontSize = 80.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No tasks yet",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tap + to add your first task",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = accentColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .height(56.dp)
        ) {
            Text(
                text = "Add First Task",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

