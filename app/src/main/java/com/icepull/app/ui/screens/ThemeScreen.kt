package com.icepull.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.icepull.app.ui.components.SnowEffect

@Composable
fun ThemeScreen(
    currentTheme: AppTheme,
    onThemeSelect: (AppTheme) -> Unit,
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
                text = "Themes",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = currentTheme.accentColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose your ice fishing experience",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Theme options
            AppTheme.entries.forEach { theme ->
                ThemeOption(
                    theme = theme,
                    isSelected = currentTheme == theme,
                    onClick = { onThemeSelect(theme) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ThemeOption(
    theme: AppTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "theme_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isSelected) 12.dp else 4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = theme.accentColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Color preview
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                theme.backgroundColor,
                                theme.iceColor,
                                theme.accentColor
                            )
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.Center)
                        .background(theme.snowColor, RoundedCornerShape(10.dp))
                )
            }

            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = theme.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.accentColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = getThemeDescription(theme),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Selected indicator
            if (isSelected) {
                Text(
                    text = "✓",
                    fontSize = 28.sp,
                    color = theme.accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun getThemeDescription(theme: AppTheme): String {
    return when (theme) {
        AppTheme.STANDARD -> "Classic blue ice fishing"
        AppTheme.NIGHT -> "Mysterious purple night"
        AppTheme.AURORA -> "Northern lights magic"
        AppTheme.FESTIVE -> "Festive winter celebration"
    }
}

