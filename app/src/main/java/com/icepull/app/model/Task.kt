package com.icepull.app.model

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val size: TaskSize = TaskSize.MEDIUM,
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)

enum class TaskSize(val displayName: String, val scale: Float) {
    SMALL("Small", 0.7f),
    MEDIUM("Medium", 1.0f),
    LARGE("Large", 1.3f)
}

