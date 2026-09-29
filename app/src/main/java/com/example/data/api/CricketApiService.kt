package com.example.data.api

import com.example.data.model.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface CricketApiService {
    suspend fun getMatchGranularStats(matchId: String): MatchGranularStats
}

class CricketApiServiceImpl : CricketApiService {

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override suspend fun getMatchGranularStats(matchId: String): MatchGranularStats {
        // Simulate realistic network round-trip latency (between 120ms and 240ms)
        val startTime = System.currentTimeMillis()
        delay(180)
        val latency = System.currentTimeMillis() - startTime

        val timestamp = timeFormat.format(Date())

        return when (matchId) {
            "m_live_1" -> buildAusVsIndGranularStats(matchId, latency, timestamp)
            "m_live_2" -> buildEngVsSaGranularStats(matchId, latency, timestamp)
            "m_comp_1" -> buildCompIndVsAusGranularStats(matchId, latency, timestamp)
            else -> buildDefaultGranularStats(matchId, latency, timestamp)
        }
    }

    private fun buildAusVsIndGranularStats(
        matchId: String,
        latency: Long,
        timestamp: String
    ): MatchGranularStats {
        // Innings 1: Australia 186/6 (20.0 ov)
        val ausBatters = listOf(
            GranularBatterStats(
                playerId = "b1",
                name = "Travis Head",
                runs = 64,
                balls = 38,
                fours = 8,
                sixes = 3,
                isStriker = false,
                isOut = true,
                dismissalText = "c Suryakumar b Bumrah",
                dotBalls = 11,
                powerplayRuns = 42,
                powerplayBalls = 22,
                middleRuns = 22,
                middleBalls = 16,
                deathRuns = 0,
                deathBalls = 0,
                runsVsPace = 44,
                ballsVsPace = 24,
                runsVsSpin = 20,
                ballsVsSpin = 14
            ),
            GranularBatterStats(
                playerId = "b2",
                name = "David Warner",
                runs = 22,
                balls = 14,
                fours = 3,
                sixes = 1,
                isStriker = false,
                isOut = true,
                dismissalText = "b Arshdeep",
                dotBalls = 5,
                powerplayRuns = 22,
                powerplayBalls = 14,
                middleRuns = 0,
                middleBalls = 0,
                deathRuns = 0,
                deathBalls = 0,
                runsVsPace = 18,
                ballsVsPace = 10,
                runsVsSpin = 4,
                ballsVsSpin = 4
            ),
            GranularBatterStats(
                playerId = "b3",
                name = "Mitchell Marsh",
                runs = 37,
                balls = 26,
                fours = 4,
                sixes = 2,
                isStriker = false,
                isOut = true,
                dismissalText = "c Pant b Kuldeep",
                dotBalls = 8,
                powerplayRuns = 6,
                powerplayBalls = 4,
                middleRuns = 31,
                middleBalls = 22,
                deathRuns = 0,
                deathBalls = 0,
                runsVsPace = 21,
                ballsVsPace = 14,
                runsVsSpin = 16,
                ballsVsSpin = 12
            ),
            GranularBatterStats(
                playerId = "b4",
                name = "Glenn Maxwell",
                runs = 19,
                balls = 12,
                fours = 1,
                sixes = 1,
                isStriker = false,
                isOut = true,
                dismissalText = "b Bumrah",
                dotBalls = 4,
                powerplayRuns = 0,
                powerplayBalls = 0,
                middleRuns = 19,
                middleBalls = 12,
                deathRuns = 0,
                deathBalls = 0,
                runsVsPace = 7,
                ballsVsPace = 5,
                runsVsSpin = 12,
                ballsVsSpin = 7
            ),
            GranularBatterStats(
                playerId = "b5",
                name = "Marcus Stoinis",
                runs = 28,
                balls = 17,
                fours = 2,
                sixes = 2,
                isStriker = false,
                isOut = true,
                dismissalText = "c Jadeja b Hardik",
                dotBalls = 3,
                powerplayRuns = 0,
                powerplayBalls = 0,
                middleRuns = 11,
                middleBalls = 7,
                deathRuns = 17,
                deathBalls = 10,
                runsVsPace = 24,
                ballsVsPace = 13,
                runsVsSpin = 4,
                ballsVsSpin = 4
            ),
            GranularBatterStats(
                playerId = "b6",
                name = "Tim David",
                runs = 10,
                balls = 9,
                fours = 1,
                sixes = 0,
                isStriker = false,
                isOut = false,
                dismissalText = "not out",
                dotBalls = 3,
                powerplayRuns = 0,
                powerplayBalls = 0,
                middleRuns = 0,
                middleBalls = 0,
                deathRuns = 10,
                deathBalls = 9,
                runsVsPace = 10,
                ballsVsPace = 9,
                runsVsSpin = 0,
                ballsVsSpin = 0
            ),
            GranularBatterStats(
                playerId = "b7",
                name = "Pat Cummins",
                runs = 4,
                balls = 4,
                fours = 0,
                sixes = 0,
                isStriker = false,
                isOut = false,
                dismissalText = "not out",
                dotBalls = 1,
                powerplayRuns = 0,
                powerplayBalls = 0,
                middleRuns = 0,
                middleBalls = 0,
                deathRuns = 4,
                deathBalls = 4,
                runsVsPace = 4,
                ballsVsPace = 4,
                runsVsSpin = 0,
                ballsVsSpin = 0
            )
        )

        val indBowlers = listOf(
            GranularBowlerStats(
                playerId = "bw1",
                name = "Arshdeep Singh",
                overs = 4.0f,
                maidens = 0,
                runsConceded = 37,
                wickets = 1,
                dotBalls = 12,
                foursConceded = 4,
                sixesConceded = 2,
                powerplayOvers = 2.0f,
                powerplayRuns = 18,
                powerplayWickets = 1,
                middleOvers = 0f,
                middleRuns = 0,
                middleWickets = 0,
                deathOvers = 2.0f,
                deathRuns = 19,
                deathWickets = 0,
                runsVsRHB = 16,
                ballsVsRHB = 11,
                runsVsLHB = 21,
                ballsVsLHB = 13,
                overDetails = listOf(
                    BowlerOverDetail(1, 8, 0, 3, 0),
                    BowlerOverDetail(3, 10, 1, 3, 0),
                    BowlerOverDetail(17, 11, 0, 3, 0),
                    BowlerOverDetail(19, 8, 0, 3, 0)
                )
            ),
            GranularBowlerStats(
                playerId = "bw2",
                name = "Jasprit Bumrah",
                overs = 4.0f,
                maidens = 0,
                runsConceded = 24,
                wickets = 2,
                dotBalls = 14,
                foursConceded = 2,
                sixesConceded = 0,
                powerplayOvers = 2.0f,
                powerplayRuns = 11,
                powerplayWickets = 0,
                middleOvers = 1.0f,
                middleRuns = 4,
                middleWickets = 1,
                deathOvers = 1.0f,
                deathRuns = 9,
                deathWickets = 1,
                runsVsRHB = 10,
                ballsVsRHB = 12,
                runsVsLHB = 14,
                ballsVsLHB = 12,
                overDetails = listOf(
                    BowlerOverDetail(2, 5, 0, 4, 0),
                    BowlerOverDetail(5, 6, 0, 3, 0),
                    BowlerOverDetail(13, 4, 1, 4, 0),
                    BowlerOverDetail(18, 9, 1, 3, 0)
                )
            ),
            GranularBowlerStats(
                playerId = "bw3",
                name = "Hardik Pandya",
                overs = 4.0f,
                maidens = 0,
                runsConceded = 42,
                wickets = 1,
                dotBalls = 8,
                foursConceded = 4,
                sixesConceded = 2,
                powerplayOvers = 1.0f,
                powerplayRuns = 12,
                powerplayWickets = 0,
                middleOvers = 2.0f,
                middleRuns = 16,
                middleWickets = 0,
                deathOvers = 1.0f,
                deathRuns = 14,
                deathWickets = 1,
                runsVsRHB = 26,
                ballsVsRHB = 14,
                runsVsLHB = 16,
                ballsVsLHB = 10,
                overDetails = listOf(
                    BowlerOverDetail(6, 12, 0, 1, 0),
                    BowlerOverDetail(9, 9, 0, 2, 0),
                    BowlerOverDetail(11, 7, 0, 3, 0),
                    BowlerOverDetail(20, 14, 1, 2, 0)
                )
            ),
            GranularBowlerStats(
                playerId = "bw4",
                name = "Kuldeep Yadav",
                overs = 4.0f,
                maidens = 0,
                runsConceded = 34,
                wickets = 1,
                dotBalls = 9,
                foursConceded = 3,
                sixesConceded = 1,
                powerplayOvers = 0f,
                powerplayRuns = 0,
                powerplayWickets = 0,
                middleOvers = 4.0f,
                middleRuns = 34,
                middleWickets = 1,
                deathOvers = 0f,
                deathRuns = 0,
                deathWickets = 0,
                runsVsRHB = 18,
                ballsVsRHB = 13,
                runsVsLHB = 16,
                ballsVsLHB = 11,
                overDetails = listOf(
                    BowlerOverDetail(7, 8, 0, 2, 0),
                    BowlerOverDetail(10, 11, 0, 1, 0),
                    BowlerOverDetail(12, 6, 1, 4, 0),
                    BowlerOverDetail(15, 9, 0, 2, 0)
                )
            ),
            GranularBowlerStats(
                playerId = "bw5",
                name = "Axar Patel",
                overs = 4.0f,
                maidens = 0,
                runsConceded = 45,
                wickets = 0,
                dotBalls = 6,
                foursConceded = 5,
                sixesConceded = 2,
                powerplayOvers = 1.0f,
                powerplayRuns = 13,
                powerplayWickets = 0,
                middleOvers = 3.0f,
                middleRuns = 32,
                middleWickets = 0,
                deathOvers = 0f,
                deathRuns = 0,
                deathWickets = 0,
                runsVsRHB = 27,
                ballsVsRHB = 15,
                runsVsLHB = 18,
                ballsVsLHB = 9,
                overDetails = listOf(
                    BowlerOverDetail(4, 13, 0, 1, 0),
                    BowlerOverDetail(8, 12, 0, 2, 0),
                    BowlerOverDetail(14, 11, 0, 2, 0),
                    BowlerOverDetail(16, 9, 0, 1, 0)
                )
            )
        )

        val ausPartnerships = listOf(
            DetailedPartnership(
                wicketNumber = 1,
                totalRuns = 38,
                totalBalls = 25,
                batter1Name = "Travis Head",
                batter1Runs = 15,
                batter1Balls = 11,
                batter1Fours = 2,
                batter1Sixes = 1,
                batter2Name = "David Warner",
                batter2Runs = 22,
                batter2Balls = 14,
                batter2Fours = 3,
                batter2Sixes = 1,
                extras = 1,
                oversSpan = "Overs 0.1 – 4.1"
            ),
            DetailedPartnership(
                wicketNumber = 2,
                totalRuns = 68,
                totalBalls = 42,
                batter1Name = "Travis Head",
                batter1Runs = 44,
                batter1Balls = 24,
                batter1Fours = 6,
                batter1Sixes = 2,
                batter2Name = "Mitchell Marsh",
                batter2Runs = 23,
                batter2Balls = 18,
                batter2Fours = 2,
                batter2Sixes = 1,
                extras = 1,
                oversSpan = "Overs 4.2 – 11.1"
            ),
            DetailedPartnership(
                wicketNumber = 3,
                totalRuns = 21,
                totalBalls = 13,
                batter1Name = "Mitchell Marsh",
                batter1Runs = 14,
                batter1Balls = 8,
                batter1Fours = 2,
                batter1Sixes = 1,
                batter2Name = "Glenn Maxwell",
                batter2Runs = 6,
                batter2Balls = 5,
                batter2Fours = 1,
                batter2Sixes = 0,
                extras = 1,
                oversSpan = "Overs 11.2 – 13.2"
            ),
            DetailedPartnership(
                wicketNumber = 4,
                totalRuns = 29,
                totalBalls = 18,
                batter1Name = "Glenn Maxwell",
                batter1Runs = 13,
                batter1Balls = 7,
                batter1Fours = 0,
                batter1Sixes = 1,
                batter2Name = "Marcus Stoinis",
                batter2Runs = 15,
                batter2Balls = 11,
                batter2Fours = 1,
                batter2Sixes = 1,
                extras = 1,
                oversSpan = "Overs 13.3 – 16.2"
            ),
            DetailedPartnership(
                wicketNumber = 5,
                totalRuns = 16,
                totalBalls = 12,
                batter1Name = "Marcus Stoinis",
                batter1Runs = 13,
                batter1Balls = 6,
                batter1Fours = 1,
                batter1Sixes = 1,
                batter2Name = "Tim David",
                batter2Runs = 3,
                batter2Balls = 6,
                batter2Fours = 0,
                batter2Sixes = 0,
                extras = 0,
                oversSpan = "Overs 16.3 – 18.2"
            ),
            DetailedPartnership(
                wicketNumber = 6,
                totalRuns = 14,
                totalBalls = 10,
                batter1Name = "Tim David",
                batter1Runs = 7,
                batter1Balls = 3,
                batter1Fours = 1,
                batter1Sixes = 0,
                batter2Name = "Pat Cummins",
                batter2Runs = 4,
                batter2Balls = 4,
                batter2Fours = 0,
                batter2Sixes = 0,
                extras = 3,
                oversSpan = "Overs 18.3 – 20.0"
            )
        )

        val ausPhases = MatchPhaseSummary(
            powerplay = PhaseStats("Powerplay", "1 - 6", 54, 1, 36),
            middleOvers = PhaseStats("Middle Overs", "7 - 15", 81, 2, 54),
            deathOvers = PhaseStats("Death Overs", "16 - 20", 51, 3, 30)
        )

        val innings1 = InningsGranularStats(
            teamId = "aus",
            teamName = "Australia",
            inningsNumber = 1,
            totalRuns = 186,
            wickets = 6,
            overs = 20.0f,
            runRate = 9.30f,
            batters = ausBatters,
            bowlers = indBowlers,
            partnerships = ausPartnerships,
            phaseSummary = ausPhases
        )

        // Innings 2: India 162/3 (17.2 ov) - Chasing 187
        val indBatters = listOf(
            GranularBatterStats(
                playerId = "b21",
                name = "Rohit Sharma",
                runs = 52,
                balls = 29,
                fours = 6,
                sixes = 3,
                isStriker = false,
                isOut = true,
                dismissalText = "c Head b Starc",
                dotBalls = 8,
                powerplayRuns = 38,
                powerplayBalls = 20,
                middleRuns = 14,
                middleBalls = 9,
                deathRuns = 0,
                deathBalls = 0,
                runsVsPace = 36,
                ballsVsPace = 18,
                runsVsSpin = 16,
                ballsVsSpin = 11
            ),
            GranularBatterStats(
                playerId = "b22",
                name = "Virat Kohli",
                runs = 24,
                balls = 16,
                fours = 2,
                sixes = 1,
                isStriker = false,
                isOut = true,
                dismissalText = "c Warner b Hazlewood",
                dotBalls = 5,
                powerplayRuns = 24,
                powerplayBalls = 16,
                middleRuns = 0,
                middleBalls = 0,
                deathRuns = 0,
                deathBalls = 0,
                runsVsPace = 20,
                ballsVsPace = 13,
                runsVsSpin = 4,
                ballsVsSpin = 3
            ),
            GranularBatterStats(
                playerId = "b23",
                name = "Rishabh Pant",
                runs = 15,
                balls = 11,
                fours = 2,
                sixes = 0,
                isStriker = false,
                isOut = true,
                dismissalText = "c Wade b Zampa",
                dotBalls = 3,
                powerplayRuns = 0,
                powerplayBalls = 0,
                middleRuns = 15,
                middleBalls = 11,
                deathRuns = 0,
                deathBalls = 0,
                runsVsPace = 8,
                ballsVsPace = 5,
                runsVsSpin = 7,
                ballsVsSpin = 6
            ),
            GranularBatterStats(
                playerId = "b24",
                name = "Suryakumar Yadav",
                runs = 45,
                balls = 28,
                fours = 4,
                sixes = 2,
                isStriker = true,
                isOut = false,
                dismissalText = "not out",
                dotBalls = 6,
                powerplayRuns = 0,
                powerplayBalls = 0,
                middleRuns = 32,
                middleBalls = 20,
                deathRuns = 13,
                deathBalls = 8,
                runsVsPace = 27,
                ballsVsPace = 16,
                runsVsSpin = 18,
                ballsVsSpin = 12
            ),
            GranularBatterStats(
                playerId = "b25",
                name = "Hardik Pandya",
                runs = 22,
                balls = 14,
                fours = 1,
                sixes = 2,
                isStriker = false,
                isOut = false,
                dismissalText = "not out",
                dotBalls = 3,
                powerplayRuns = 0,
                powerplayBalls = 0,
                middleRuns = 10,
                middleBalls = 6,
                deathRuns = 12,
                deathBalls = 8,
                runsVsPace = 18,
                ballsVsPace = 11,
                runsVsSpin = 4,
                ballsVsSpin = 3
            )
        )

        val ausBowlers = listOf(
            GranularBowlerStats(
                playerId = "bw21",
                name = "Mitchell Starc",
                overs = 3.2f,
                maidens = 0,
                runsConceded = 34,
                wickets = 1,
                dotBalls = 8,
                foursConceded = 4,
                sixesConceded = 1,
                powerplayOvers = 2.0f,
                powerplayRuns = 18,
                powerplayWickets = 0,
                middleOvers = 1.0f,
                middleRuns = 9,
                middleWickets = 1,
                deathOvers = 0.2f,
                deathRuns = 7,
                deathWickets = 0,
                runsVsRHB = 26,
                ballsVsRHB = 14,
                runsVsLHB = 8,
                ballsVsLHB = 6,
                overDetails = listOf(
                    BowlerOverDetail(1, 9, 0, 3, 0),
                    BowlerOverDetail(3, 9, 0, 3, 0),
                    BowlerOverDetail(11, 9, 1, 2, 0),
                    BowlerOverDetail(18, 7, 0, 0, 0)
                )
            ),
            GranularBowlerStats(
                playerId = "bw22",
                name = "Josh Hazlewood",
                overs = 4.0f,
                maidens = 0,
                runsConceded = 31,
                wickets = 1,
                dotBalls = 11,
                foursConceded = 3,
                sixesConceded = 1,
                powerplayOvers = 2.0f,
                powerplayRuns = 14,
                powerplayWickets = 1,
                middleOvers = 2.0f,
                middleRuns = 17,
                middleWickets = 0,
                deathOvers = 0f,
                deathRuns = 0,
                deathWickets = 0,
                runsVsRHB = 22,
                ballsVsRHB = 17,
                runsVsLHB = 9,
                ballsVsLHB = 7,
                overDetails = listOf(
                    BowlerOverDetail(2, 6, 0, 3, 0),
                    BowlerOverDetail(5, 8, 1, 3, 0),
                    BowlerOverDetail(9, 7, 0, 3, 0),
                    BowlerOverDetail(14, 10, 0, 2, 0)
                )
            ),
            GranularBowlerStats(
                playerId = "bw23",
                name = "Pat Cummins",
                overs = 4.0f,
                maidens = 0,
                runsConceded = 38,
                wickets = 0,
                dotBalls = 9,
                foursConceded = 3,
                sixesConceded = 2,
                powerplayOvers = 2.0f,
                powerplayRuns = 18,
                powerplayWickets = 0,
                middleOvers = 1.0f,
                middleRuns = 8,
                middleWickets = 0,
                deathOvers = 1.0f,
                deathRuns = 12,
                deathWickets = 0,
                runsVsRHB = 28,
                ballsVsRHB = 16,
                runsVsLHB = 10,
                ballsVsLHB = 8,
                overDetails = listOf(
                    BowlerOverDetail(4, 10, 0, 2, 0),
                    BowlerOverDetail(6, 8, 0, 3, 0),
                    BowlerOverDetail(15, 8, 0, 2, 0),
                    BowlerOverDetail(17, 12, 0, 2, 0)
                )
            ),
            GranularBowlerStats(
                playerId = "bw24",
                name = "Adam Zampa",
                overs = 4.0f,
                maidens = 0,
                runsConceded = 36,
                wickets = 1,
                dotBalls = 10,
                foursConceded = 3,
                sixesConceded = 2,
                powerplayOvers = 0f,
                powerplayRuns = 0,
                powerplayWickets = 0,
                middleOvers = 3.0f,
                middleRuns = 28,
                middleWickets = 1,
                deathOvers = 1.0f,
                deathRuns = 8,
                deathWickets = 0,
                runsVsRHB = 24,
                ballsVsRHB = 15,
                runsVsLHB = 12,
                ballsVsLHB = 9,
                overDetails = listOf(
                    BowlerOverDetail(8, 9, 0, 2, 0),
                    BowlerOverDetail(10, 11, 0, 2, 0),
                    BowlerOverDetail(13, 8, 1, 3, 0),
                    BowlerOverDetail(16, 8, 0, 3, 0)
                )
            ),
            GranularBowlerStats(
                playerId = "bw25",
                name = "Glenn Maxwell",
                overs = 2.0f,
                maidens = 0,
                runsConceded = 21,
                wickets = 0,
                dotBalls = 3,
                foursConceded = 2,
                sixesConceded = 1,
                powerplayOvers = 0f,
                powerplayRuns = 0,
                powerplayWickets = 0,
                middleOvers = 2.0f,
                middleRuns = 21,
                middleWickets = 0,
                deathOvers = 0f,
                deathRuns = 0,
                deathWickets = 0,
                runsVsRHB = 14,
                ballsVsRHB = 8,
                runsVsLHB = 7,
                ballsVsLHB = 4,
                overDetails = listOf(
                    BowlerOverDetail(7, 10, 0, 1, 0),
                    BowlerOverDetail(12, 11, 0, 2, 0)
                )
            )
        )

        val indPartnerships = listOf(
            DetailedPartnership(
                wicketNumber = 1,
                totalRuns = 38,
                totalBalls = 25,
                batter1Name = "Rohit Sharma",
                batter1Runs = 14,
                batter1Balls = 9,
                batter1Fours = 2,
                batter1Sixes = 0,
                batter2Name = "Virat Kohli",
                batter2Runs = 24,
                batter2Balls = 16,
                batter2Fours = 2,
                batter2Sixes = 1,
                extras = 0,
                oversSpan = "Overs 0.1 – 4.1"
            ),
            DetailedPartnership(
                wicketNumber = 2,
                totalRuns = 41,
                totalBalls = 27,
                batter1Name = "Rohit Sharma",
                batter1Runs = 32,
                batter1Balls = 17,
                batter1Fours = 4,
                batter1Sixes = 2,
                batter2Name = "Rishabh Pant",
                batter2Runs = 8,
                batter2Balls = 10,
                batter2Fours = 1,
                batter2Sixes = 0,
                extras = 1,
                oversSpan = "Overs 4.2 – 8.4"
            ),
            DetailedPartnership(
                wicketNumber = 3,
                totalRuns = 35,
                totalBalls = 23,
                batter1Name = "Rishabh Pant",
                batter1Runs = 7,
                batter1Balls = 5,
                batter1Fours = 1,
                batter1Sixes = 0,
                batter2Name = "Suryakumar Yadav",
                batter2Runs = 27,
                batter2Balls = 18,
                batter2Fours = 3,
                batter2Sixes = 1,
                extras = 1,
                oversSpan = "Overs 8.5 – 12.3"
            ),
            DetailedPartnership(
                wicketNumber = 4,
                totalRuns = 48,
                totalBalls = 29,
                batter1Name = "Suryakumar Yadav",
                batter1Runs = 26,
                batter1Balls = 15,
                batter1Fours = 2,
                batter1Sixes = 1,
                batter2Name = "Hardik Pandya",
                batter2Runs = 22,
                batter2Balls = 14,
                batter2Fours = 1,
                batter2Sixes = 2,
                extras = 0,
                oversSpan = "Overs 12.4 – 17.2",
                isCurrentStand = true
            )
        )

        val indPhases = MatchPhaseSummary(
            powerplay = PhaseStats("Powerplay", "1 - 6", 52, 1, 36),
            middleOvers = PhaseStats("Middle Overs", "7 - 15", 85, 2, 54),
            deathOvers = PhaseStats("Death Overs", "16 - 20 (ongoing)", 25, 0, 14)
        )

        val innings2 = InningsGranularStats(
            teamId = "ind",
            teamName = "India",
            inningsNumber = 2,
            totalRuns = 162,
            wickets = 3,
            overs = 17.2f,
            runRate = 9.35f,
            batters = indBatters,
            bowlers = ausBowlers,
            partnerships = indPartnerships,
            phaseSummary = indPhases
        )

        return MatchGranularStats(
            matchId = matchId,
            sourceApi = "Cricket FastFeed Telemetry API v2",
            latencyMs = latency,
            lastUpdated = timestamp,
            inningsList = listOf(innings1, innings2)
        )
    }

    private fun buildEngVsSaGranularStats(
        matchId: String,
        latency: Long,
        timestamp: String
    ): MatchGranularStats {
        val saBatters = listOf(
            GranularBatterStats("sa_b1", "Heinrich Klaasen", 34, 19, 2, 3, isStriker = true, dotBalls = 4, powerplayRuns = 0, powerplayBalls = 0, middleRuns = 34, middleBalls = 19, runsVsPace = 20, ballsVsPace = 11, runsVsSpin = 14, ballsVsSpin = 8),
            GranularBatterStats("sa_b2", "David Miller", 12, 8, 1, 0, isStriker = false, dotBalls = 2, powerplayRuns = 0, powerplayBalls = 0, middleRuns = 12, middleBalls = 8, runsVsPace = 8, ballsVsPace = 5, runsVsSpin = 4, ballsVsSpin = 3),
            GranularBatterStats("sa_b3", "Quinton de Kock", 18, 14, 3, 0, isOut = true, dismissalText = "c Buttler b Archer", dotBalls = 6, powerplayRuns = 18, powerplayBalls = 14, runsVsPace = 14, ballsVsPace = 10, runsVsSpin = 4, ballsVsSpin = 4),
            GranularBatterStats("sa_b4", "Aiden Markram", 31, 23, 4, 1, isOut = true, dismissalText = "b Rashid", dotBalls = 7, powerplayRuns = 15, powerplayBalls = 10, middleRuns = 16, middleBalls = 13, runsVsPace = 18, ballsVsPace = 12, runsVsSpin = 13, ballsVsSpin = 11)
        )

        val engBowlers = listOf(
            GranularBowlerStats("eng_bw1", "Sam Curran", 2.5f, 0, 26, 2, dotBalls = 6, foursConceded = 2, sixesConceded = 1, powerplayOvers = 1.0f, powerplayRuns = 8, powerplayWickets = 0, middleOvers = 1.5f, middleRuns = 18, middleWickets = 2),
            GranularBowlerStats("eng_bw2", "Jofra Archer", 3.0f, 0, 22, 1, dotBalls = 10, foursConceded = 2, sixesConceded = 0, powerplayOvers = 2.0f, powerplayRuns = 13, powerplayWickets = 1, middleOvers = 1.0f, middleRuns = 9, middleWickets = 0),
            GranularBowlerStats("eng_bw3", "Adil Rashid", 3.0f, 0, 24, 1, dotBalls = 8, foursConceded = 2, sixesConceded = 1, powerplayOvers = 0f, powerplayRuns = 0, powerplayWickets = 0, middleOvers = 3.0f, middleRuns = 24, middleWickets = 1)
        )

        val saPartnerships = listOf(
            DetailedPartnership(1, 24, 16, "Quinton de Kock", 14, 10, 2, 0, "Aiden Markram", 10, 6, 2, 0, 0, "Overs 0.1 - 2.3"),
            DetailedPartnership(2, 34, 25, "Aiden Markram", 21, 17, 2, 1, "Tristan Stubbs", 13, 8, 2, 0, 0, "Overs 2.4 - 6.4"),
            DetailedPartnership(3, 27, 21, "Tristan Stubbs", 9, 7, 1, 0, "Ryan Rickelton", 18, 14, 2, 0, 0, "Overs 6.5 - 10.1"),
            DetailedPartnership(4, 17, 15, "Ryan Rickelton", 8, 7, 1, 0, "Heinrich Klaasen", 9, 8, 1, 0, 0, "Overs 10.2 - 14.4"),
            DetailedPartnership(5, 22, 11, "Heinrich Klaasen", 14, 6, 1, 1, "David Miller", 8, 5, 1, 0, 0, "Overs 14.5 - 14.5", isCurrentStand = true)
        )

        val saPhases = MatchPhaseSummary(
            powerplay = PhaseStats("Powerplay", "1 - 6", 48, 1, 36),
            middleOvers = PhaseStats("Middle Overs", "7 - 15 (ongoing)", 76, 3, 53),
            deathOvers = PhaseStats("Death Overs", "16 - 20", 0, 0, 0)
        )

        val innings = InningsGranularStats(
            teamId = "sa",
            teamName = "South Africa",
            inningsNumber = 1,
            totalRuns = 124,
            wickets = 4,
            overs = 14.5f,
            runRate = 8.35f,
            batters = saBatters,
            bowlers = engBowlers,
            partnerships = saPartnerships,
            phaseSummary = saPhases
        )

        return MatchGranularStats(
            matchId = matchId,
            sourceApi = "Cricket FastFeed Telemetry API v2",
            latencyMs = latency,
            lastUpdated = timestamp,
            inningsList = listOf(innings)
        )
    }

    private fun buildCompIndVsAusGranularStats(
        matchId: String,
        latency: Long,
        timestamp: String
    ): MatchGranularStats {
        val indBatters = listOf(
            GranularBatterStats("ind_c1", "Rohit Sharma", 92, 41, 7, 8, dotBalls = 10, powerplayRuns = 50, powerplayBalls = 19, middleRuns = 42, middleBalls = 22, runsVsPace = 58, ballsVsPace = 24, runsVsSpin = 34, ballsVsSpin = 17, isOut = true, dismissalText = "b Starc"),
            GranularBatterStats("ind_c2", "Suryakumar Yadav", 31, 16, 3, 2, dotBalls = 3, powerplayRuns = 0, powerplayBalls = 0, middleRuns = 31, middleBalls = 16, runsVsPace = 20, ballsVsPace = 10, runsVsSpin = 11, ballsVsSpin = 6, isOut = true, dismissalText = "c Wade b Starc"),
            GranularBatterStats("ind_c3", "Shivam Dube", 28, 22, 2, 1, dotBalls = 6, middleRuns = 20, middleBalls = 15, deathRuns = 8, deathBalls = 7, runsVsPace = 14, ballsVsPace = 11, runsVsSpin = 14, ballsVsSpin = 11, isOut = true, dismissalText = "c Warner b Cummins"),
            GranularBatterStats("ind_c4", "Hardik Pandya", 27, 17, 1, 2, dotBalls = 4, deathRuns = 27, deathBalls = 17, runsVsPace = 27, ballsVsPace = 17, isOut = false, dismissalText = "not out")
        )

        val ausBowlers = listOf(
            GranularBowlerStats("aus_c_bw1", "Mitchell Starc", 4.0f, 0, 45, 2, dotBalls = 9, foursConceded = 4, sixesConceded = 3, powerplayOvers = 2.0f, powerplayRuns = 28, powerplayWickets = 0, deathOvers = 2.0f, deathRuns = 17, deathWickets = 2),
            GranularBowlerStats("aus_c_bw2", "Josh Hazlewood", 4.0f, 0, 25, 1, dotBalls = 13, foursConceded = 2, sixesConceded = 1, powerplayOvers = 2.0f, powerplayRuns = 12, powerplayWickets = 1, middleOvers = 2.0f, middleRuns = 13, middleWickets = 0),
            GranularBowlerStats("aus_c_bw3", "Pat Cummins", 4.0f, 0, 48, 1, dotBalls = 7, foursConceded = 3, sixesConceded = 4, powerplayOvers = 1.0f, powerplayRuns = 15, powerplayWickets = 0, middleOvers = 2.0f, middleRuns = 19, middleWickets = 1, deathOvers = 1.0f, deathRuns = 14, deathWickets = 0)
        )

        val partnerships = listOf(
            DetailedPartnership(1, 98, 48, "Rohit Sharma", 68, 28, 5, 6, "Rishabh Pant", 15, 14, 1, 1, 15, "Overs 1.2 - 9.2"),
            DetailedPartnership(2, 45, 23, "Rohit Sharma", 24, 13, 2, 2, "Suryakumar Yadav", 21, 10, 2, 1, 0, "Overs 9.3 - 13.1"),
            DetailedPartnership(3, 38, 24, "Shivam Dube", 19, 14, 1, 1, "Hardik Pandya", 19, 10, 1, 1, 0, "Overs 13.2 - 17.2")
        )

        val phases = MatchPhaseSummary(
            powerplay = PhaseStats("Powerplay", "1 - 6", 62, 1, 36),
            middleOvers = PhaseStats("Middle Overs", "7 - 15", 88, 2, 54),
            deathOvers = PhaseStats("Death Overs", "16 - 20", 55, 2, 30)
        )

        val innings = InningsGranularStats(
            teamId = "ind",
            teamName = "India",
            inningsNumber = 1,
            totalRuns = 205,
            wickets = 5,
            overs = 20.0f,
            runRate = 10.25f,
            batters = indBatters,
            bowlers = ausBowlers,
            partnerships = partnerships,
            phaseSummary = phases
        )

        return MatchGranularStats(
            matchId = matchId,
            sourceApi = "Cricket FastFeed Telemetry API v2",
            latencyMs = latency,
            lastUpdated = timestamp,
            inningsList = listOf(innings)
        )
    }

    private fun buildDefaultGranularStats(
        matchId: String,
        latency: Long,
        timestamp: String
    ): MatchGranularStats {
        val sampleBatters = listOf(
            GranularBatterStats("db1", "Opening Batter 1", 38, 26, 4, 1, dotBalls = 7, powerplayRuns = 28, powerplayBalls = 18, middleRuns = 10, middleBalls = 8, runsVsPace = 24, ballsVsPace = 16, runsVsSpin = 14, ballsVsSpin = 10),
            GranularBatterStats("db2", "Opening Batter 2", 45, 32, 5, 2, dotBalls = 9, powerplayRuns = 22, powerplayBalls = 18, middleRuns = 23, middleBalls = 14, runsVsPace = 25, ballsVsPace = 18, runsVsSpin = 20, ballsVsSpin = 14)
        )
        val sampleBowlers = listOf(
            GranularBowlerStats("dbw1", "Strike Bowler", 4.0f, 0, 32, 2, dotBalls = 12, foursConceded = 3, sixesConceded = 1, powerplayOvers = 2.0f, powerplayRuns = 14, powerplayWickets = 1, deathOvers = 2.0f, deathRuns = 18, deathWickets = 1)
        )
        val samplePartnership = listOf(
            DetailedPartnership(1, 65, 46, "Opening Batter 1", 28, 22, 3, 1, "Opening Batter 2", 35, 24, 4, 1, 2, "Overs 0.1 - 7.4")
        )
        val samplePhases = MatchPhaseSummary(
            powerplay = PhaseStats("Powerplay", "1 - 6", 48, 0, 36),
            middleOvers = PhaseStats("Middle Overs", "7 - 15", 72, 2, 54),
            deathOvers = PhaseStats("Death Overs", "16 - 20", 42, 2, 30)
        )
        return MatchGranularStats(
            matchId = matchId,
            sourceApi = "Cricket FastFeed Telemetry API v2",
            latencyMs = latency,
            lastUpdated = timestamp,
            inningsList = listOf(
                InningsGranularStats("team_a", "Team A", 1, 162, 4, 20.0f, 8.10f, sampleBatters, sampleBowlers, samplePartnership, samplePhases)
            )
        )
    }
}
