package com.example.data.model

enum class MatchStatus {
    LIVE,
    INNINGS_BREAK,
    UPCOMING,
    COMPLETED
}

enum class MatchFormat {
    T20,
    ODI,
    TEST
}

data class Team(
    val id: String,
    val name: String,
    val shortName: String,
    val primaryColorHex: String,
    val secondaryColorHex: String,
    val flagCode: String,
    val logoRes: Int? = null
)

data class InningsScore(
    val teamId: String,
    val runs: Int,
    val wickets: Int,
    val overs: Float, // e.g. 17.4f means 17 overs and 4 balls
    val isCompleted: Boolean = false,
    val isDeclared: Boolean = false
) {
    fun formattedOvers(): String {
        val completedOvers = overs.toInt()
        val balls = Math.round((overs - completedOvers) * 10f)
        return "$completedOvers.$balls"
    }

    fun ballsBowled(): Int {
        val completedOvers = overs.toInt()
        val balls = Math.round((overs - completedOvers) * 10f)
        return (completedOvers * 6) + balls
    }

    fun runRate(): Float {
        val totalBalls = ballsBowled()
        return if (totalBalls > 0) (runs.toFloat() / totalBalls) * 6f else 0f
    }
}

data class DeliveryBall(
    val display: String,      // "0", "1", "2", "3", "4", "6", "W", "1lb", "Wd"
    val runs: Int,
    val isWicket: Boolean = false,
    val isFour: Boolean = false,
    val isSix: Boolean = false,
    val isExtra: Boolean = false
)

data class BatsmanStats(
    val id: String,
    val name: String,
    val runs: Int,
    val balls: Int,
    val fours: Int,
    val sixes: Int,
    val isStriker: Boolean = false,
    val isOut: Boolean = false,
    val dismissalText: String = "not out"
) {
    val strikeRate: Float
        get() = if (balls > 0) (runs.toFloat() / balls) * 100f else 0f
}

data class BowlerStats(
    val id: String,
    val name: String,
    val overs: Float,
    val maidens: Int,
    val runs: Int,
    val wickets: Int,
    val dots: Int = 0,
    val isCurrentBowler: Boolean = false
) {
    fun formattedOvers(): String {
        val comp = overs.toInt()
        val b = Math.round((overs - comp) * 10f)
        return "$comp.$b"
    }

    val economy: Float
        get() {
            val comp = overs.toInt()
            val b = Math.round((overs - comp) * 10f)
            val totalBalls = (comp * 6) + b
            return if (totalBalls > 0) (runs.toFloat() / totalBalls) * 6f else 0f
        }
}

data class Partnership(
    val runs: Int,
    val balls: Int,
    val batter1Name: String,
    val batter1Runs: Int,
    val batter2Name: String,
    val batter2Runs: Int
) {
    val runRate: Float
        get() = if (balls > 0) (runs.toFloat() / balls) * 6f else 0f
}

data class FallOfWicket(
    val wicketNumber: Int,
    val score: Int,
    val over: Float,
    val batterName: String
)

data class OverSummary(
    val overNumber: Int,
    val bowlerName: String,
    val totalRuns: Int,
    val balls: List<DeliveryBall>
)

data class ExtrasSummary(
    val total: Int,
    val wides: Int,
    val noBalls: Int,
    val legByes: Int,
    val byes: Int
)

data class InningsScorecard(
    val teamId: String,
    val teamName: String,
    val runs: Int,
    val wickets: Int,
    val overs: Float,
    val maxOvers: Int,
    val batters: List<BatsmanStats>,
    val bowlers: List<BowlerStats>,
    val extras: ExtrasSummary,
    val didNotBat: List<String>
)

data class FullScorecard(
    val innings1: InningsScorecard,
    val innings2: InningsScorecard?
)

data class MatchOfficials(
    val umpires: String,
    val thirdUmpire: String,
    val matchReferee: String
)

data class WinPrediction(
    val team1Percent: Int,
    val team2Percent: Int
)

data class CricketMatch(
    val id: String,
    val tournamentName: String,
    val matchNumberDesc: String,
    val venue: String,
    val city: String,
    val format: MatchFormat,
    val maxOvers: Int,
    val status: MatchStatus,
    val statusNote: String,       // e.g. "IND need 28 runs in 16 balls", "AUS won by 14 runs"
    val team1: Team,
    val team2: Team,
    val team1Score: InningsScore,
    val team2Score: InningsScore?,
    val currentBattingTeamId: String,
    val target: Int? = null,
    val tossResult: String,
    val currentOverBalls: List<DeliveryBall> = emptyList(),
    val recentOvers: List<OverSummary> = emptyList(),
    val currentBatters: List<BatsmanStats> = emptyList(),
    val currentBowler: BowlerStats? = null,
    val currentPartnership: Partnership? = null,
    val fallOfWickets: List<FallOfWicket> = emptyList(),
    val scorecard: FullScorecard? = null,
    val winPrediction: WinPrediction = WinPrediction(50, 50),
    val officials: MatchOfficials? = null,
    val startTimeFormatted: String = "",
    val isFavorite: Boolean = false
) {
    val currentInningsScore: InningsScore
        get() = if (currentBattingTeamId == team1.id) team1Score else (team2Score ?: team1Score)

    val currentBowlingTeam: Team
        get() = if (currentBattingTeamId == team1.id) team2 else team1

    val currentBattingTeam: Team
        get() = if (currentBattingTeamId == team1.id) team1 else team2

    val requiredRunRate: Float?
        get() {
            if (target == null || team2Score == null || status != MatchStatus.LIVE) return null
            val runsNeeded = target - team2Score.runs
            val totalMaxBalls = maxOvers * 6
            val ballsBowled = team2Score.ballsBowled()
            val ballsRemaining = totalMaxBalls - ballsBowled
            if (ballsRemaining <= 0) return 0f
            return (runsNeeded.toFloat() / ballsRemaining) * 6f
        }

    val ballsRemaining: Int?
        get() {
            if (target == null || team2Score == null) return null
            val totalMaxBalls = maxOvers * 6
            return totalMaxBalls - team2Score.ballsBowled()
        }

    val runsRemaining: Int?
        get() {
            if (target == null || team2Score == null) return null
            return target - team2Score.runs
        }
}
