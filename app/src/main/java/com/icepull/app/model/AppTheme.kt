package com.icepull.app.model

import androidx.compose.ui.graphics.Color

enum class AppTheme(
    val displayName: String,
    val iceColor: Color,
    val accentColor: Color,
    val backgroundColor: Color,
    val snowColor: Color
) {
    STANDARD(
        "Standard Ice",
        Color(0xFFB3E5FC),
        Color(0xFF2196F3),
        Color(0xFFE1F5FE),
        Color(0xFFFFFFFF)
    ),
    NIGHT(
        "Night Ice",
        Color(0xFF7E57C2),
        Color(0xFF9C27B0),
        Color(0xFF4A148C),
        Color(0xFFE1BEE7)
    ),
    AURORA(
        "Aurora",
        Color(0xFF26C6DA),
        Color(0xFF00BFA5),
        Color(0xFF006064),
        Color(0xFF80DEEA)
    ),
    FESTIVE(
        "Festive",
        Color(0xFFFFEBEE),
        Color(0xFFF44336),
        Color(0xFFE8F5E9),
        Color(0xFFFFFFFF)
    )
}

