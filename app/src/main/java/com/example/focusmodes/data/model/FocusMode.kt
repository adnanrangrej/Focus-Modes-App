package com.example.focusmodes.data.model

data class FocusMode(
    val id: Int = 0,
    val name: String,
    val duration: Int, // in minutes
    val blockedApps: List<String>,
    val description: String,
    val isCustom: Boolean = false
) 