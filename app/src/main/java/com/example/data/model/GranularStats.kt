package com.example.data.model

import java.util.Locale

data class MatchGranularStats(
    val matchId: String,
    val sourceApi: String = "Cricket Data FastFeed API v2",
    val latencyMs: Long = 42,
    val lastUpdated: String = "Just now",
    val inningsList: List<InningsGranularStats> = emptyList()
)

data class InningsGranularStats(
    val teamId: String,
    val teamName: String,
    val inningsNumber: Int,
    val totalRuns: Int,
    val wickets: Int,
    val overs: Float,
    val runRate: Float,
    val batters: List<GranularBatterStats>,
    val bowlers: List<GranularBowlerStats>,
    val partnerships: List<DetailedPartnership>,
    val phaseSummary: MatchPhaseSummary
)

data class GranularBatterStats(
    val playerId: String,
    val name: String,
    val runs: Int,
    val balls: Int,
    val fours: Int,
    val sixes: Int,
    val isStriker: Boolean = false,
    val isOut: Boolean = false,
    val dismissalText: String = "not out",
    val dotBalls: Int = 0,
    val powerplayRuns: Int = 0,
    val powerplayBalls: Int = 0,
    val middleRuns: Int = 0,
    val middleBalls: Int = 0,
    val deathRuns: Int = 0,
    val deathBalls: Int = 0,
    val runsVsPace: Int = 0,
    val ballsVsPace: Int = 0,
    val runsVsSpin: Int = 0,
    val ballsVsSpin: Int = 0
) {
    val overallStrikeRate: Float
        get() = if (balls > 0) (runs.toFloat() / balls) * 100f else 0f

    val dotBallPercent: Float
        get() = if (balls > 0) (dotBalls.toFloat() / balls) * 100f else 0f

    val boundaryRuns: Int
        get() = (fours * 4) + (sixes * 6)

    val boundaryRunPercent: Float
        get() = if (runs > 0) (boundaryRuns.toFloat() / runs) * 100f else 0f

    val powerplayStrikeRate: Float?
        get() = if (powerplayBalls > 0) (powerplayRuns.toFloat() / powerplayBalls) * 100f else null

    val middleStrikeRate: Float?
        get() = if (middleBalls > 0) (middleRuns.toFloat() / middleBalls) * 100f else null

    val deathStrikeRate: Float?
        get() = if (deathBalls > 0) (deathRuns.toFloat() / deathBalls) * 100f else null

    val strikeRateVsPace: Float
        get() = if (ballsVsPace > 0) (runsVsPace.toFloat() / ballsVsPace) * 100f else 0f

    val strikeRateVsSpin: Float
        get() = if (ballsVsSpin > 0) (runsVsSpin.toFloat() / ballsVsSpin) * 100f else 0f
}

data class BowlerOverDetail(
    val overNumber: Int,
    val runsConceded: Int,
    val wicketsTaken: Int = 0,
    val dotBalls: Int = 0,
    val extras: Int = 0
)

data class GranularBowlerStats(
    val playerId: String,
    val name: String,
    val overs: Float,
    val maidens: Int,
    val runsConceded: Int,
    val wickets: Int,
    val dotBalls: Int = 0,
    val foursConceded: Int = 0,
    val sixesConceded: Int = 0,
    val powerplayOvers: Float = 0f,
    val powerplayRuns: Int = 0,
    val powerplayWickets: Int = 0,
    val middleOvers: Float = 0f,
    val middleRuns: Int = 0,
    val middleWickets: Int = 0,
    val deathOvers: Float = 0f,
    val deathRuns: Int = 0,
    val deathWickets: Int = 0,
    val runsVsRHB: Int = 0,
    val ballsVsRHB: Int = 0,
    val runsVsLHB: Int = 0,
    val ballsVsLHB: Int = 0,
    val overDetails: List<BowlerOverDetail> = emptyList()
) {
    val totalBalls: Int
        get() {
            val comp = overs.toInt()
            val b = ((overs - comp) * 10).toInt()
            return (comp * 6) + b
        }

    val overallEconomy: Float
        get() = if (totalBalls > 0) (runsConceded.toFloat() / totalBalls) * 6f else 0f

    val dotBallPercent: Float
        get() = if (totalBalls > 0) (dotBalls.toFloat() / totalBalls) * 100f else 0f

    fun calculateEconomy(runs: Int, ov: Float): Float? {
        val comp = ov.toInt()
        val b = ((ov - comp) * 10).toInt()
        val balls = (comp * 6) + b
        return if (balls > 0) (runs.toFloat() / balls) * 6f else null
    }

    val powerplayEconomy: Float?
        get() = calculateEconomy(powerplayRuns, powerplayOvers)

    val middleEconomy: Float?
        get() = calculateEconomy(middleRuns, middleOvers)

    val deathEconomy: Float?
        get() = calculateEconomy(deathRuns, deathOvers)

    val economyVsRHB: Float
        get() = if (ballsVsRHB > 0) (runsVsRHB.toFloat() / ballsVsRHB) * 6f else 0f

    val economyVsLHB: Float
        get() = if (ballsVsLHB > 0) (runsVsLHB.toFloat() / ballsVsLHB) * 6f else 0f
}

data class DetailedPartnership(
    val wicketNumber: Int,
    val totalRuns: Int,
    val totalBalls: Int,
    val batter1Name: String,
    val batter1Runs: Int,
    val batter1Balls: Int,
    val batter1Fours: Int = 0,
    val batter1Sixes: Int = 0,
    val batter2Name: String,
    val batter2Runs: Int,
    val batter2Balls: Int,
    val batter2Fours: Int = 0,
    val batter2Sixes: Int = 0,
    val extras: Int = 0,
    val oversSpan: String = "",
    val isCurrentStand: Boolean = false
) {
    val runRate: Float
        get() = if (totalBalls > 0) (totalRuns.toFloat() / totalBalls) * 6f else 0f

    val batter1StrikeRate: Float
        get() = if (batter1Balls > 0) (batter1Runs.toFloat() / batter1Balls) * 100f else 0f

    val batter2StrikeRate: Float
        get() = if (batter2Balls > 0) (batter2Runs.toFloat() / batter2Balls) * 100f else 0f

    val batter1SharePercent: Float
        get() = if (totalRuns > 0) ((batter1Runs.toFloat() / totalRuns) * 100f).coerceIn(0f, 100f) else 50f

    val batter2SharePercent: Float
        get() = if (totalRuns > 0) ((batter2Runs.toFloat() / totalRuns) * 100f).coerceIn(0f, 100f) else 50f
}

data class PhaseStats(
    val name: String,
    val oversRange: String,
    val runs: Int,
    val wickets: Int,
    val balls: Int
) {
    val runRate: Float
        get() = if (balls > 0) (runs.toFloat() / balls) * 6f else 0f
}

data class MatchPhaseSummary(
    val powerplay: PhaseStats,
    val middleOvers: PhaseStats,
    val deathOvers: PhaseStats
)
