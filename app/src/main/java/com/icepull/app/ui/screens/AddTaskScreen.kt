package com.icepull.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.icepull.app.model.TaskSize
import com.icepull.app.ui.components.SnowEffect
import kotlinx.coroutines.delay

@Composable
fun AddTaskScreen(
    currentTheme: AppTheme,
    onAddTask: (String, TaskSize) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var taskTitle by remember { mutableStateOf("") }
    var selectedSize by remember { mutableStateOf(TaskSize.MEDIUM) }
    var isAdding by remember { mutableStateOf(false) }

    LaunchedEffect(isAdding) {
        if (isAdding) {
            delay(600)
            if (taskTitle.isNotBlank()) {
                onAddTask(taskTitle, selectedSize)
            }
        }
    }

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
            count = 30
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
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
                text = "Add New Task",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = currentTheme.accentColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cast your line into the ice",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Task input
            OutlinedTextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                label = { Text("Task name") },
                placeholder = { Text("What do you need to do?") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = currentTheme.accentColor,
                    focusedLabelColor = currentTheme.accentColor,
                    cursorColor = currentTheme.accentColor,
                    focusedContainerColor = Color.White.copy(alpha = 0.9f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Size selection
            Text(
                text = "Task Size",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TaskSize.entries.forEach { size ->
                    SizeOption(
                        size = size,
                        isSelected = selectedSize == size,
                        accentColor = currentTheme.accentColor,
                        onClick = { selectedSize = size },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Fish diving animation preview
            AnimatedVisibility(
                visible = isAdding,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🐟",
                        fontSize = 48.sp,
                        modifier = Modifier.offset(y = 50.dp)
                    )
                }
            }

            // Add button
            Button(
                onClick = { 
                    if (taskTitle.isNotBlank()) {
                        isAdding = true
                    }
                },
                enabled = taskTitle.isNotBlank() && !isAdding,
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentTheme.accentColor,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = if (isAdding) "Adding..." else "Add Task",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SizeOption(
    size: TaskSize,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "size_scale"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) accentColor else Color.White.copy(alpha = 0.7f)
            )
            .border(
                width = 2.dp,
                color = if (isSelected) accentColor else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🐟",
            fontSize = (24 * size.scale).sp,
            modifier = Modifier.offset(y = (-scale * 2).dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = size.displayName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

