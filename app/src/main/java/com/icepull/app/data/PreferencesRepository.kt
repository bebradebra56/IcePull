package com.icepull.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.icepull.app.model.AppTheme
import com.icepull.app.model.Statistics
import com.icepull.app.model.Task
import com.icepull.app.model.TaskSize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ice_pull_preferences")

class PreferencesRepository(private val context: Context) {
    
    private val dataStore = context.dataStore
    
    companion object {
        private val TASKS_KEY = stringPreferencesKey("tasks")
        private val THEME_KEY = stringPreferencesKey("theme")
        
        // Statistics keys
        private val STATS_TODAY_CAUGHT = intPreferencesKey("stats_today_caught")
        private val STATS_WEEK_BEST = intPreferencesKey("stats_week_best")
        private val STATS_TOTAL_CAUGHT = intPreferencesKey("stats_total_caught")
        private val STATS_CURRENT_STREAK = intPreferencesKey("stats_current_streak")
        private val STATS_LAST_COMPLETED_DATE = longPreferencesKey("stats_last_completed_date")
    }
    
    // Tasks
    suspend fun saveTasks(tasks: List<Task>) {
        dataStore.edit { preferences ->
            val jsonArray = JSONArray()
            tasks.forEach { task ->
                val jsonObject = JSONObject().apply {
                    put("id", task.id)
                    put("title", task.title)
                    put("size", task.size.name)
                    put("createdAt", task.createdAt)
                    put("isCompleted", task.isCompleted)
                }
                jsonArray.put(jsonObject)
            }
            preferences[TASKS_KEY] = jsonArray.toString()
        }
    }
    
    val tasksFlow: Flow<List<Task>> = dataStore.data.map { preferences ->
        val tasksJson = preferences[TASKS_KEY] ?: return@map emptyList()
        try {
            val jsonArray = JSONArray(tasksJson)
            val tasks = mutableListOf<Task>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                tasks.add(
                    Task(
                        id = jsonObject.getString("id"),
                        title = jsonObject.getString("title"),
                        size = TaskSize.valueOf(jsonObject.getString("size")),
                        createdAt = jsonObject.getLong("createdAt"),
                        isCompleted = jsonObject.getBoolean("isCompleted")
                    )
                )
            }
            tasks
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Statistics
    suspend fun saveStatistics(stats: Statistics) {
        dataStore.edit { preferences ->
            preferences[STATS_TODAY_CAUGHT] = stats.todayCaught
            preferences[STATS_WEEK_BEST] = stats.weekBest
            preferences[STATS_TOTAL_CAUGHT] = stats.totalCaught
            preferences[STATS_CURRENT_STREAK] = stats.currentStreak
            preferences[STATS_LAST_COMPLETED_DATE] = stats.lastCompletedDate
        }
    }
    
    val statisticsFlow: Flow<Statistics> = dataStore.data.map { preferences ->
        Statistics(
            todayCaught = preferences[STATS_TODAY_CAUGHT] ?: 0,
            weekBest = preferences[STATS_WEEK_BEST] ?: 0,
            totalCaught = preferences[STATS_TOTAL_CAUGHT] ?: 0,
            currentStreak = preferences[STATS_CURRENT_STREAK] ?: 0,
            lastCompletedDate = preferences[STATS_LAST_COMPLETED_DATE] ?: 0L
        )
    }
    
    // Theme
    suspend fun saveTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
    
    val themeFlow: Flow<AppTheme> = dataStore.data.map { preferences ->
        val themeName = preferences[THEME_KEY] ?: AppTheme.STANDARD.name
        try {
            AppTheme.valueOf(themeName)
        } catch (e: Exception) {
            AppTheme.STANDARD
        }
    }
}

