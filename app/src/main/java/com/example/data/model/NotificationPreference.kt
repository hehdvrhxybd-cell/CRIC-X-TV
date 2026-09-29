package com.example.data.model

data class NotificationPreference(
    val matchStart: Boolean = true,
    val wickets: Boolean = true,
    val boundaries: Boolean = true,
    val milestones: Boolean = true,
    val inningsBreak: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)
