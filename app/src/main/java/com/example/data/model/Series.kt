package com.example.data.model

data class SeriesTournament(
    val id: String,
    val name: String,
    val category: String, // "International", "T20 Leagues", "Women's"
    val dates: String,
    val totalMatches: Int,
    val completedMatches: Int,
    val currentLeader: String,
    val format: MatchFormat,
    val teamsCount: Int,
    val bannerUrl: String? = null
)
