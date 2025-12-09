package com.icepull.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.icepull.app.model.AppTheme

@Composable
fun IcePullTheme(
    appTheme: AppTheme = AppTheme.STANDARD,
    content: @Composable () -> Unit
) {
    val colorScheme = lightColorScheme(
        primary = appTheme.accentColor,
        secondary = appTheme.iceColor,
        background = appTheme.backgroundColor,
        surface = appTheme.iceColor,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

