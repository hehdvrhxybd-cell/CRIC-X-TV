package com.example.data.model

enum class PlayerRole {
    BATSMAN,
    BOWLER,
    ALL_ROUNDER,
    WICKET_KEEPER
}

data class BattingStatsSummary(
    val matches: Int,
    val innings: Int,
    val runs: Int,
    val average: Float,
    val strikeRate: Float,
    val highestScore: String,
    val hundreds: Int,
    val fifties: Int,
    val fours: Int,
    val sixes: Int
)

data class BowlingStatsSummary(
    val matches: Int,
    val innings: Int,
    val wickets: Int,
    val average: Float,
    val economy: Float,
    val strikeRate: Float,
    val bestBowling: String,
    val fourWickets: Int,
    val fiveWickets: Int
)

data class CricketPlayer(
    val id: String,
    val name: String,
    val teamName: String,
    val teamShort: String,
    val role: PlayerRole,
    val battingStyle: String,
    val bowlingStyle: String,
    val jerseyNumber: Int,
    val isCaptain: Boolean = false,
    val isWicketKeeper: Boolean = false,
    val t20StatsBatting: BattingStatsSummary,
    val t20StatsBowling: BowlingStatsSummary? = null,
    val odiStatsBatting: BattingStatsSummary,
    val testStatsBatting: BattingStatsSummary
)

data class LeaderboardPlayer(
    val rank: Int,
    val player: CricketPlayer,
    val primaryStatLabel: String,
    val primaryStatValue: String,
    val secondaryStatLabel: String,
    val secondaryStatValue: String
)
