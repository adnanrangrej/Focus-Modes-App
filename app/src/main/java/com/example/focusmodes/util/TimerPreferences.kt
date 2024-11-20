package com.example.focusmodes.util

import android.content.Context

object TimerPreferences {
    private const val PREF_NAME = "timer_preferences"
    private const val KEY_FOCUS_TIME = "focus_time"
    private const val KEY_BREAK_TIME = "break_time"
    
    // Default times in minutes
    const val DEFAULT_FOCUS_TIME = 25
    const val DEFAULT_BREAK_TIME = 5
    
    fun setTimerSettings(context: Context, focusMinutes: Int, breakMinutes: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_FOCUS_TIME, focusMinutes)
            .putInt(KEY_BREAK_TIME, breakMinutes)
            .apply()
    }
    
    fun getTimerSettings(context: Context): Pair<Int, Int> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val focusTime = prefs.getInt(KEY_FOCUS_TIME, DEFAULT_FOCUS_TIME)
        val breakTime = prefs.getInt(KEY_BREAK_TIME, DEFAULT_BREAK_TIME)
        return Pair(focusTime, breakTime)
    }
    
    fun resetToDefault(context: Context) {
        setTimerSettings(context, DEFAULT_FOCUS_TIME, DEFAULT_BREAK_TIME)
    }
} 