package com.icepull.app.model

data class Statistics(
    val todayCaught: Int = 0,
    val weekBest: Int = 0,
    val totalCaught: Int = 0,
    val currentStreak: Int = 0,
    val lastCompletedDate: Long = 0L
)

