package com.example.data.model

data class MatchReminder(
    val matchId: String,
    val matchTitle: String,
    val tournamentName: String,
    val venue: String,
    val scheduledDisplayTime: String,
    val leadMinutes: Int, // e.g. 15, 30, 60, or 0 (test 10s)
    val leadDescription: String,
    val createdAtEpoch: Long = System.currentTimeMillis()
)
