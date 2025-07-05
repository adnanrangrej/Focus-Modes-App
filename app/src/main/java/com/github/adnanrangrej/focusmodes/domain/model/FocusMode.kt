package com.github.adnanrangrej.focusmodes.domain.model

data class FocusMode(
    val id: Long = 0,
    val name: String,
    val workDuration: Long,
    val breakDuration: Long,
    val description: String?,
    val blockedAppPackages: List<String>
)
