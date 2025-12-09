package com.icepull.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.icepull.app.ui.theme.IcePullTheme
import com.icepull.app.viewmodel.IcePullViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            IcePullApp()
        }
    }
}

@Composable
fun IcePullApp() {
    val context = LocalContext.current
    val viewModel: IcePullViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as android.app.Application
        )
    )
    val tasks by viewModel.tasks.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()
    val selectedTask by viewModel.selectedTask.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.MAIN) }

    IcePullTheme(appTheme = currentTheme) {
        Scaffold(
            containerColor = Color.Transparent
        ) { innerPadding ->
            when (currentScreen) {
                Screen.MAIN -> {
                    com.icepull.app.ui.screens.MainScreen(
                        tasks = tasks,
                        currentTheme = currentTheme,
                        onTaskClick = { task ->
                            viewModel.selectTask(task)
                        },
                        onAddClick = {
                            currentScreen = Screen.ADD_TASK
                        },
                        onStatsClick = {
                            currentScreen = Screen.STATISTICS
                        },
                        onThemeClick = {
                            currentScreen = Screen.THEME
                        },
                        modifier = Modifier.padding(innerPadding)
                    )

                    // Show task action dialog
                    selectedTask?.let { task ->
                        com.icepull.app.ui.components.TaskActionDialog(
                            task = task,
                            accentColor = currentTheme.accentColor,
                            onPull = {
                                viewModel.pullTask(task.id)
                            },
                            onRelease = {
                                viewModel.releaseTask(task.id)
                            },
                            onDismiss = {
                                viewModel.selectTask(null)
                            }
                        )
                    }
                }

                Screen.ADD_TASK -> {
                    com.icepull.app.ui.screens.AddTaskScreen(
                        currentTheme = currentTheme,
                        onAddTask = { title, size ->
                            viewModel.addTask(title, size)
                            currentScreen = Screen.MAIN
                        },
                        onBack = {
                            currentScreen = Screen.MAIN
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                Screen.STATISTICS -> {
                    com.icepull.app.ui.screens.StatisticsScreen(
                        statistics = statistics,
                        currentTheme = currentTheme,
                        onBack = {
                            currentScreen = Screen.MAIN
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                Screen.THEME -> {
                    com.icepull.app.ui.screens.ThemeScreen(
                        currentTheme = currentTheme,
                        onThemeSelect = { theme ->
                            viewModel.changeTheme(theme)
                        },
                        onBack = {
                            currentScreen = Screen.MAIN
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class Screen {
    MAIN,
    ADD_TASK,
    STATISTICS,
    THEME
}

