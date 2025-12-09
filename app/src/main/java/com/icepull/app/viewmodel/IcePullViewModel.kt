package com.icepull.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.icepull.app.data.PreferencesRepository
import com.icepull.app.model.AppTheme
import com.icepull.app.model.Statistics
import com.icepull.app.model.Task
import com.icepull.app.model.TaskSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class IcePullViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = PreferencesRepository(application.applicationContext)
    
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    
    private val _statistics = MutableStateFlow(Statistics())
    val statistics: StateFlow<Statistics> = _statistics.asStateFlow()
    
    private val _currentTheme = MutableStateFlow(AppTheme.STANDARD)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()
    
    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()
    
    init {
        // Загружаем данные при запуске
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            // Загружаем задачи
            repository.tasksFlow.collect { savedTasks ->
                _tasks.value = savedTasks
            }
        }
        
        viewModelScope.launch {
            // Загружаем статистику
            repository.statisticsFlow.collect { savedStats ->
                // Проверяем, не новый ли день
                val stats = checkAndResetDailyStats(savedStats)
                _statistics.value = stats
                
                // Если статистика изменилась (новый день), сохраняем
                if (stats != savedStats) {
                    repository.saveStatistics(stats)
                }
            }
        }
        
        viewModelScope.launch {
            // Загружаем тему
            repository.themeFlow.collect { savedTheme ->
                _currentTheme.value = savedTheme
            }
        }
    }
    
    private fun checkAndResetDailyStats(stats: Statistics): Statistics {
        if (stats.lastCompletedDate == 0L) return stats
        
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val lastCompletedDate = Calendar.getInstance().apply {
            timeInMillis = stats.lastCompletedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val isToday = lastCompletedDate == today
        val isYesterday = (today - lastCompletedDate) == 86400000L
        
        return if (!isToday) {
            // Новый день - сбрасываем todayCaught
            val newStreak = if (isYesterday) stats.currentStreak else 0
            val newStats = stats.copy(
                todayCaught = 0,
                currentStreak = newStreak
            )
            newStats
        } else {
            stats
        }
    }
    
    fun addTask(title: String, size: TaskSize = TaskSize.MEDIUM) {
        if (title.isBlank()) return
        
        val newTask = Task(
            title = title.trim(),
            size = size
        )
        _tasks.value = _tasks.value + newTask
        
        // Сохраняем в DataStore
        viewModelScope.launch {
            repository.saveTasks(_tasks.value)
        }
    }
    
    fun pullTask(taskId: String) {
        viewModelScope.launch {
            val task = _tasks.value.find { it.id == taskId } ?: return@launch
            
            // Remove from active tasks
            _tasks.value = _tasks.value.filter { it.id != taskId }
            
            // Update statistics
            updateStatisticsOnCompletion()
            
            // Сохраняем задачи
            repository.saveTasks(_tasks.value)
            
            _selectedTask.value = null
        }
    }
    
    fun releaseTask(taskId: String) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.filter { it.id != taskId }
            
            // Сохраняем задачи
            repository.saveTasks(_tasks.value)
            
            _selectedTask.value = null
        }
    }
    
    fun selectTask(task: Task?) {
        _selectedTask.value = task
    }
    
    fun changeTheme(theme: AppTheme) {
        _currentTheme.value = theme
        
        // Сохраняем тему
        viewModelScope.launch {
            repository.saveTheme(theme)
        }
    }
    
    private fun updateStatisticsOnCompletion() {
        val current = _statistics.value
        val now = System.currentTimeMillis()
        
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val lastCompletedDate = if (current.lastCompletedDate > 0) {
            Calendar.getInstance().apply {
                timeInMillis = current.lastCompletedDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        } else {
            0L
        }
        
        val isToday = current.lastCompletedDate > 0 && lastCompletedDate == today
        val isYesterday = current.lastCompletedDate > 0 && 
            (today - lastCompletedDate) == 86400000L
        
        // Обновляем счетчик пойманных сегодня
        val newTodayCaught = if (isToday) current.todayCaught + 1 else 1
        
        // Streak увеличивается только если это первая рыбка за новый день
        val newStreak = when {
            current.lastCompletedDate == 0L -> 1  // Самая первая рыбка
            isToday -> current.currentStreak  // Уже ловили сегодня, streak не меняется
            isYesterday -> current.currentStreak + 1  // Вчера ловили, увеличиваем streak
            else -> 1  // Был перерыв, начинаем заново
        }
        
        val newWeekBest = maxOf(current.weekBest, newTodayCaught)
        
        val newStats = Statistics(
            todayCaught = newTodayCaught,
            weekBest = newWeekBest,
            totalCaught = current.totalCaught + 1,
            currentStreak = newStreak,
            lastCompletedDate = now
        )
        
        _statistics.value = newStats
        
        // Сохраняем статистику
        viewModelScope.launch {
            repository.saveStatistics(newStats)
        }
    }
}

