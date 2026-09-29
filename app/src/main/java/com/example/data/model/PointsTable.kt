package com.example.data.model

data class PointsTableEntry(
    val rank: Int,
    val team: Team,
    val played: Int,
    val won: Int,
    val lost: Int,
    val tied: Int = 0,
    val noResult: Int = 0,
    val points: Int,
    val netRunRate: String,
    val recentForm: List<String> // ["W", "W", "L", "W", "L"]
)

data class TournamentPointsTable(
    val tournamentId: String,
    val tournamentName: String,
    val groupName: String,
    val entries: List<PointsTableEntry>
)
