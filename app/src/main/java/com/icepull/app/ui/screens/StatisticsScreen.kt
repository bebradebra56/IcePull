package com.icepull.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.icepull.app.model.AppTheme
import com.icepull.app.model.Statistics
import com.icepull.app.ui.components.SnowEffect

@Composable
fun StatisticsScreen(
    statistics: Statistics,
    currentTheme: AppTheme,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        
        SnowEffect(
            snowColor = currentTheme.snowColor,
            count = 40
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
            ) {
                Text(text = "←", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Statistics",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = currentTheme.accentColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your fishing achievements",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Main stats
            StatCard(
                icon = "🎣",
                title = "Today's Catch",
                value = statistics.todayCaught.toString(),
                subtitle = "tasks completed today",
                accentColor = currentTheme.accentColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatCard(
                icon = "🏆",
                title = "Week's Best",
                value = statistics.weekBest.toString(),
                subtitle = "most tasks in a day",
                accentColor = currentTheme.accentColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatCard(
                icon = "⭐",
                title = "Total Caught",
                value = statistics.totalCaught.toString(),
                subtitle = "all time completions",
                accentColor = currentTheme.accentColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatCard(
                icon = "🔥",
                title = "Current Streak",
                value = "${statistics.currentStreak} ${if (statistics.currentStreak == 1) "day" else "days"}",
                subtitle = "keep it going!",
                accentColor = currentTheme.accentColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Motivational message
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                currentTheme.accentColor.copy(alpha = 0.2f),
                                currentTheme.iceColor.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getMotivationalMessage(statistics),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentTheme.accentColor,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    icon: String,
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "stat_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 32.sp
                )
            }

            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

fun getMotivationalMessage(stats: Statistics): String {
    return when {
        stats.todayCaught == 0 -> "Cast your line and catch your first task today!"
        stats.todayCaught < 3 -> "Great start! Keep fishing!"
        stats.todayCaught < 5 -> "You're on fire! 🔥"
        stats.todayCaught < 10 -> "Amazing productivity! Keep it up!"
        else -> "Legendary catch! You're a master angler! 🎣"
    }
}

