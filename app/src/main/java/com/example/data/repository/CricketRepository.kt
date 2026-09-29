package com.example.data.repository

import android.content.Context
import com.example.data.api.CricketApiService
import com.example.data.api.CricketApiServiceImpl
import com.example.data.model.*
import com.example.worker.LiveMatchNotificationScheduler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

data class LiveEventAlert(
    val title: String,
    val subtitle: String,
    val isWicket: Boolean = false,
    val isBoundary: Boolean = false
)

class CricketRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var appContext: Context? = null

    fun attachContext(context: Context) {
        this.appContext = context.applicationContext
    }

    // State flows
    private val _matches = MutableStateFlow<List<CricketMatch>>(emptyList())
    val matches: StateFlow<List<CricketMatch>> = _matches.asStateFlow()

    private val _seriesList = MutableStateFlow<List<SeriesTournament>>(emptyList())
    val seriesList: StateFlow<List<SeriesTournament>> = _seriesList.asStateFlow()

    private val _pointsTables = MutableStateFlow<List<TournamentPointsTable>>(emptyList())
    val pointsTables: StateFlow<List<TournamentPointsTable>> = _pointsTables.asStateFlow()

    private val _players = MutableStateFlow<List<CricketPlayer>>(emptyList())
    val players: StateFlow<List<CricketPlayer>> = _players.asStateFlow()

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    private val _favoriteTeamIds = MutableStateFlow<Set<String>>(setOf("ind", "csk"))
    val favoriteTeamIds: StateFlow<Set<String>> = _favoriteTeamIds.asStateFlow()

    private val _notificationPreference = MutableStateFlow(NotificationPreference())
    val notificationPreference: StateFlow<NotificationPreference> = _notificationPreference.asStateFlow()

    private val _matchReminders = MutableStateFlow<Map<String, MatchReminder>>(emptyMap())
    val matchReminders: StateFlow<Map<String, MatchReminder>> = _matchReminders.asStateFlow()

    private val _liveAlerts = MutableSharedFlow<LiveEventAlert>(extraBufferCapacity = 5)
    val liveAlerts: SharedFlow<LiveEventAlert> = _liveAlerts.asSharedFlow()

    private val _isLiveSimulationRunning = MutableStateFlow(true)
    val isLiveSimulationRunning: StateFlow<Boolean> = _isLiveSimulationRunning.asStateFlow()

    private val apiService: CricketApiService = CricketApiServiceImpl()

    private val _granularStatsMap = MutableStateFlow<Map<String, MatchGranularStats>>(emptyMap())
    val granularStatsMap: StateFlow<Map<String, MatchGranularStats>> = _granularStatsMap.asStateFlow()

    private val _isStatsLoading = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val isStatsLoading: StateFlow<Map<String, Boolean>> = _isStatsLoading.asStateFlow()

    private var simulationJob: Job? = null

    init {
        loadInitialData()
        startLiveScoreEngine()
        // Pre-fetch live match granular statistics from API
        repositoryScope.launch {
            fetchGranularStats("m_live_1")
        }
    }

    private fun loadInitialData() {
        // Teams
        val teamInd = Team("ind", "India", "IND", "#0078FF", "#FF9933", "IN")
        val teamAus = Team("aus", "Australia", "AUS", "#FFD700", "#006400", "AU")
        val teamEng = Team("eng", "England", "ENG", "#C8102E", "#012169", "GB")
        val teamSa = Team("sa", "South Africa", "SA", "#007A3D", "#FFB81C", "ZA")
        val teamPak = Team("pak", "Pakistan", "PAK", "#115740", "#99CC00", "PK")
        val teamNz = Team("nz", "New Zealand", "NZ", "#000000", "#40C4FF", "NZ")
        val teamCsk = Team("csk", "Chennai Super Kings", "CSK", "#FDB913", "#005BAA", "IN")
        val teamMi = Team("mi", "Mumbai Indians", "MI", "#004BA0", "#D1AB3E", "IN")
        val teamRcb = Team("rcb", "Royal Challengers Bengaluru", "RCB", "#EC1C24", "#000000", "IN")
        val teamKkr = Team("kkr", "Kolkata Knight Riders", "KKR", "#3A225D", "#F0CA00", "IN")

        _teams.value = listOf(teamInd, teamAus, teamEng, teamSa, teamPak, teamNz, teamCsk, teamMi, teamRcb, teamKkr)

        // Seed Players
        val kohli = CricketPlayer(
            id = "p_kohli",
            name = "Virat Kohli",
            teamName = "India",
            teamShort = "IND",
            role = PlayerRole.BATSMAN,
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm medium",
            jerseyNumber = 18,
            t20StatsBatting = BattingStatsSummary(125, 117, 4188, 48.7f, 137.0f, "122*", 1, 38, 361, 124),
            odiStatsBatting = BattingStatsSummary(295, 283, 13906, 58.2f, 93.5f, "183", 50, 72, 1302, 151),
            testStatsBatting = BattingStatsSummary(115, 195, 8947, 48.9f, 55.6f, "254*", 29, 30, 998, 26)
        )

        val bumrah = CricketPlayer(
            id = "p_bumrah",
            name = "Jasprit Bumrah",
            teamName = "India",
            teamShort = "IND",
            role = PlayerRole.BOWLER,
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm fast",
            jerseyNumber = 93,
            t20StatsBatting = BattingStatsSummary(70, 15, 68, 8.5f, 94.2f, "14", 0, 0, 5, 2),
            t20StatsBowling = BowlingStatsSummary(70, 69, 89, 17.7f, 6.27f, 16.9f, "3/7", 0, 0),
            odiStatsBatting = BattingStatsSummary(89, 45, 120, 6.3f, 62.1f, "16", 0, 0, 8, 1),
            testStatsBatting = BattingStatsSummary(38, 62, 280, 7.8f, 48.5f, "34*", 0, 0, 31, 8)
        )

        val head = CricketPlayer(
            id = "p_head",
            name = "Travis Head",
            teamName = "Australia",
            teamShort = "AUS",
            role = PlayerRole.BATSMAN,
            battingStyle = "Left-hand bat",
            bowlingStyle = "Right-arm offbreak",
            jerseyNumber = 62,
            t20StatsBatting = BattingStatsSummary(38, 37, 1092, 34.1f, 158.4f, "91", 0, 7, 122, 54),
            odiStatsBatting = BattingStatsSummary(69, 66, 2645, 43.4f, 105.2f, "154*", 6, 16, 290, 65),
            testStatsBatting = BattingStatsSummary(51, 86, 3350, 41.9f, 64.8f, "175", 7, 16, 410, 24)
        )

        val starc = CricketPlayer(
            id = "p_starc",
            name = "Mitchell Starc",
            teamName = "Australia",
            teamShort = "AUS",
            role = PlayerRole.BOWLER,
            battingStyle = "Left-hand bat",
            bowlingStyle = "Left-arm fast",
            jerseyNumber = 56,
            t20StatsBatting = BattingStatsSummary(65, 30, 124, 9.5f, 98.4f, "18", 0, 0, 10, 4),
            t20StatsBowling = BowlingStatsSummary(65, 65, 79, 23.8f, 7.64f, 18.7f, "4/20", 1, 0),
            odiStatsBatting = BattingStatsSummary(121, 68, 510, 11.6f, 85.0f, "52*", 0, 1, 44, 12),
            testStatsBatting = BattingStatsSummary(89, 138, 2096, 21.6f, 65.4f, "99", 0, 10, 245, 45)
        )

        val rohit = CricketPlayer(
            id = "p_rohit",
            name = "Rohit Sharma",
            teamName = "India",
            teamShort = "IND",
            role = PlayerRole.BATSMAN,
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm offbreak",
            jerseyNumber = 45,
            isCaptain = true,
            t20StatsBatting = BattingStatsSummary(159, 151, 4231, 32.1f, 140.9f, "121*", 5, 32, 383, 205),
            odiStatsBatting = BattingStatsSummary(265, 257, 10866, 49.2f, 92.4f, "264", 31, 57, 1014, 331),
            testStatsBatting = BattingStatsSummary(61, 105, 4210, 43.8f, 56.4f, "212", 12, 18, 460, 84)
        )

        val pandya = CricketPlayer(
            id = "p_pandya",
            name = "Hardik Pandya",
            teamName = "India",
            teamShort = "IND",
            role = PlayerRole.ALL_ROUNDER,
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm medium-fast",
            jerseyNumber = 33,
            t20StatsBatting = BattingStatsSummary(102, 79, 1520, 27.1f, 140.5f, "71*", 0, 4, 115, 78),
            t20StatsBowling = BowlingStatsSummary(102, 88, 86, 26.3f, 8.12f, 19.4f, "4/16", 1, 0),
            odiStatsBatting = BattingStatsSummary(86, 61, 1769, 34.0f, 110.3f, "92*", 0, 11, 142, 68),
            testStatsBatting = BattingStatsSummary(11, 18, 532, 31.3f, 73.9f, "108", 1, 4, 68, 12)
        )

        val cummins = CricketPlayer(
            id = "p_cummins",
            name = "Pat Cummins",
            teamName = "Australia",
            teamShort = "AUS",
            role = PlayerRole.BOWLER,
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm fast",
            jerseyNumber = 30,
            isCaptain = true,
            t20StatsBatting = BattingStatsSummary(52, 28, 147, 10.5f, 126.7f, "28", 0, 0, 12, 8),
            t20StatsBowling = BowlingStatsSummary(52, 52, 66, 22.4f, 7.38f, 18.2f, "3/15", 0, 0),
            odiStatsBatting = BattingStatsSummary(88, 50, 420, 12.0f, 76.5f, "36", 0, 0, 32, 10),
            testStatsBatting = BattingStatsSummary(62, 95, 1280, 16.4f, 48.0f, "64*", 0, 2, 140, 18)
        )

        val buttler = CricketPlayer(
            id = "p_buttler",
            name = "Jos Buttler",
            teamName = "England",
            teamShort = "ENG",
            role = PlayerRole.WICKET_KEEPER,
            battingStyle = "Right-hand bat",
            bowlingStyle = "None",
            jerseyNumber = 63,
            isCaptain = true,
            isWicketKeeper = true,
            t20StatsBatting = BattingStatsSummary(124, 113, 3264, 35.8f, 146.2f, "101*", 1, 24, 305, 137),
            odiStatsBatting = BattingStatsSummary(181, 154, 5134, 39.5f, 117.1f, "162*", 11, 26, 452, 170),
            testStatsBatting = BattingStatsSummary(57, 100, 2907, 31.9f, 54.3f, "152", 2, 18, 342, 21)
        )

        _players.value = listOf(kohli, bumrah, head, starc, rohit, pandya, cummins, buttler)

        // Seed Series / Tournaments
        _seriesList.value = listOf(
            SeriesTournament(
                id = "s_t20wc",
                name = "ICC Men's T20 World Cup 2026",
                category = "International",
                dates = "Sep 15 - Oct 12, 2026",
                totalMatches = 55,
                completedMatches = 28,
                currentLeader = "India",
                format = MatchFormat.T20,
                teamsCount = 20
            ),
            SeriesTournament(
                id = "s_ipl",
                name = "Indian Premier League 2026",
                category = "T20 Leagues",
                dates = "Mar 22 - May 28, 2026",
                totalMatches = 74,
                completedMatches = 0,
                currentLeader = "Chennai Super Kings",
                format = MatchFormat.T20,
                teamsCount = 10
            ),
            SeriesTournament(
                id = "s_ashes",
                name = "The Ashes 2026-27",
                category = "International",
                dates = "Nov 19 - Jan 10, 2027",
                totalMatches = 5,
                completedMatches = 0,
                currentLeader = "Australia",
                format = MatchFormat.TEST,
                teamsCount = 2
            ),
            SeriesTournament(
                id = "s_asiacup",
                name = "Asia Cup 2026",
                category = "International",
                dates = "Aug 20 - Sep 05, 2026",
                totalMatches = 13,
                completedMatches = 13,
                currentLeader = "India",
                format = MatchFormat.ODI,
                teamsCount = 6
            )
        )

        // Points Table
        val t20Entries = listOf(
            PointsTableEntry(1, teamInd, 4, 4, 0, 0, 0, 8, "+2.482", listOf("W", "W", "W", "W")),
            PointsTableEntry(2, teamAus, 4, 3, 1, 0, 0, 6, "+1.350", listOf("W", "L", "W", "W")),
            PointsTableEntry(3, teamSa, 4, 2, 2, 0, 0, 4, "+0.412", listOf("L", "W", "W", "L")),
            PointsTableEntry(4, teamPak, 4, 1, 3, 0, 0, 2, "-0.890", listOf("L", "L", "L", "W")),
            PointsTableEntry(5, teamNz, 4, 0, 4, 0, 0, 0, "-2.105", listOf("L", "L", "L", "L"))
        )
        val iplEntries = listOf(
            PointsTableEntry(1, teamCsk, 14, 10, 4, 0, 0, 20, "+0.852", listOf("W", "W", "W", "L", "W")),
            PointsTableEntry(2, teamKkr, 14, 9, 5, 0, 0, 18, "+0.710", listOf("W", "L", "W", "W", "W")),
            PointsTableEntry(3, teamRcb, 14, 8, 6, 0, 0, 16, "+0.459", listOf("W", "W", "W", "W", "L")),
            PointsTableEntry(4, teamMi, 14, 7, 7, 0, 0, 14, "+0.105", listOf("L", "W", "L", "W", "L"))
        )
        _pointsTables.value = listOf(
            TournamentPointsTable("s_t20wc", "ICC Men's T20 World Cup 2026", "Super 8 - Group 1", t20Entries),
            TournamentPointsTable("s_ipl", "Indian Premier League 2026", "League Stage", iplEntries)
        )

        // Matches Initial State
        val match1Scorecard = FullScorecard(
            innings1 = InningsScorecard(
                teamId = "aus",
                teamName = "Australia",
                runs = 186,
                wickets = 6,
                overs = 20.0f,
                maxOvers = 20,
                batters = listOf(
                    BatsmanStats("b1", "Travis Head", 64, 38, 8, 3, false, true, "c Suryakumar b Bumrah"),
                    BatsmanStats("b2", "David Warner", 22, 14, 3, 1, false, true, "b Arshdeep"),
                    BatsmanStats("b3", "Mitchell Marsh", 37, 26, 4, 2, false, true, "c Pant b Kuldeep"),
                    BatsmanStats("b4", "Glenn Maxwell", 19, 12, 1, 1, false, true, "b Bumrah"),
                    BatsmanStats("b5", "Marcus Stoinis", 28, 17, 2, 2, false, true, "c Jadeja b Hardik"),
                    BatsmanStats("b6", "Tim David", 10, 9, 1, 0, false, false, "not out"),
                    BatsmanStats("b7", "Pat Cummins", 4, 4, 0, 0, false, false, "not out")
                ),
                bowlers = listOf(
                    BowlerStats("bw1", "Arshdeep Singh", 4.0f, 0, 37, 1, 12),
                    BowlerStats("bw2", "Jasprit Bumrah", 4.0f, 0, 24, 2, 14),
                    BowlerStats("bw3", "Hardik Pandya", 4.0f, 0, 42, 1, 8),
                    BowlerStats("bw4", "Kuldeep Yadav", 4.0f, 0, 34, 1, 9),
                    BowlerStats("bw5", "Axar Patel", 4.0f, 0, 45, 0, 6)
                ),
                extras = ExtrasSummary(total = 6, wides = 4, noBalls = 0, legByes = 2, byes = 0),
                didNotBat = listOf("Mitchell Starc", "Adam Zampa", "Josh Hazlewood")
            ),
            innings2 = InningsScorecard(
                teamId = "ind",
                teamName = "India",
                runs = 162,
                wickets = 3,
                overs = 17.2f,
                maxOvers = 20,
                batters = listOf(
                    BatsmanStats("b21", "Rohit Sharma", 52, 29, 6, 3, false, true, "c Head b Starc"),
                    BatsmanStats("b22", "Virat Kohli", 24, 16, 2, 1, false, true, "c Warner b Hazlewood"),
                    BatsmanStats("b23", "Rishabh Pant", 15, 11, 2, 0, false, true, "c Wade b Zampa"),
                    BatsmanStats("b24", "Suryakumar Yadav", 45, 28, 4, 2, isStriker = true, isOut = false, dismissalText = "not out"),
                    BatsmanStats("b25", "Hardik Pandya", 22, 14, 1, 2, isStriker = false, isOut = false, dismissalText = "not out")
                ),
                bowlers = listOf(
                    BowlerStats("bw21", "Mitchell Starc", 3.2f, 0, 34, 1, 8, isCurrentBowler = true),
                    BowlerStats("bw22", "Josh Hazlewood", 4.0f, 0, 31, 1, 11),
                    BowlerStats("bw23", "Pat Cummins", 4.0f, 0, 38, 0, 9),
                    BowlerStats("bw24", "Adam Zampa", 4.0f, 0, 36, 1, 10),
                    BowlerStats("bw25", "Glenn Maxwell", 2.0f, 0, 21, 0, 3)
                ),
                extras = ExtrasSummary(total = 4, wides = 3, noBalls = 0, legByes = 1, byes = 0),
                didNotBat = listOf("Shivam Dube", "Ravindra Jadeja", "Axar Patel", "Kuldeep Yadav", "Arshdeep Singh", "Jasprit Bumrah")
            )
        )

        val liveMatch1 = CricketMatch(
            id = "m_live_1",
            tournamentName = "ICC Men's T20 World Cup 2026",
            matchNumberDesc = "Semi Final 1",
            venue = "Melbourne Cricket Ground",
            city = "Melbourne",
            format = MatchFormat.T20,
            maxOvers = 20,
            status = MatchStatus.LIVE,
            statusNote = "IND need 25 runs in 16 balls",
            team1 = teamAus,
            team2 = teamInd,
            team1Score = InningsScore("aus", 186, 6, 20.0f, isCompleted = true),
            team2Score = InningsScore("ind", 162, 3, 17.2f),
            currentBattingTeamId = "ind",
            target = 187,
            tossResult = "India won the toss and opted to bowl",
            currentOverBalls = listOf(
                DeliveryBall("1", 1),
                DeliveryBall("4", 4, isFour = true)
            ),
            recentOvers = listOf(
                OverSummary(
                    overNumber = 17,
                    bowlerName = "Pat Cummins",
                    totalRuns = 12,
                    balls = listOf(DeliveryBall("1", 1), DeliveryBall("6", 6, isSix = true), DeliveryBall("0", 0), DeliveryBall("1", 1), DeliveryBall("2", 2), DeliveryBall("2", 2))
                ),
                OverSummary(
                    overNumber = 16,
                    bowlerName = "Adam Zampa",
                    totalRuns = 8,
                    balls = listOf(DeliveryBall("1", 1), DeliveryBall("1", 1), DeliveryBall("4", 4, isFour = true), DeliveryBall("0", 0), DeliveryBall("1", 1), DeliveryBall("1", 1))
                )
            ),
            currentBatters = listOf(
                BatsmanStats("b24", "Suryakumar Yadav", 45, 28, 4, 2, isStriker = true, isOut = false),
                BatsmanStats("b25", "Hardik Pandya", 22, 14, 1, 2, isStriker = false, isOut = false)
            ),
            currentBowler = BowlerStats("bw21", "Mitchell Starc", 3.2f, 0, 34, 1, isCurrentBowler = true),
            currentPartnership = Partnership(48, 29, "Suryakumar Yadav", 26, "Hardik Pandya", 22),
            fallOfWickets = listOf(
                FallOfWicket(1, 38, 4.1f, "Virat Kohli"),
                FallOfWicket(2, 79, 8.4f, "Rohit Sharma"),
                FallOfWicket(3, 114, 12.3f, "Rishabh Pant")
            ),
            scorecard = match1Scorecard,
            winPrediction = WinPrediction(team1Percent = 38, team2Percent = 62),
            officials = MatchOfficials("Richard Illingworth", "Rod Tucker", "Javagal Srinath"),
            startTimeFormatted = "Today, 07:00 PM GMT",
            isFavorite = true
        )

        val liveMatch2 = CricketMatch(
            id = "m_live_2",
            tournamentName = "ICC Men's T20 World Cup 2026",
            matchNumberDesc = "Semi Final 2",
            venue = "Sydney Cricket Ground",
            city = "Sydney",
            format = MatchFormat.T20,
            maxOvers = 20,
            status = MatchStatus.LIVE,
            statusNote = "SA 124/4 (14.5 ov) - CRR: 8.35",
            team1 = teamEng,
            team2 = teamSa,
            team1Score = InningsScore("eng", 0, 0, 0f),
            team2Score = InningsScore("sa", 124, 4, 14.5f),
            currentBattingTeamId = "sa",
            target = null,
            tossResult = "South Africa won the toss and opted to bat",
            currentOverBalls = listOf(
                DeliveryBall("0", 0),
                DeliveryBall("1", 1),
                DeliveryBall("2", 2),
                DeliveryBall("W", 0, isWicket = true),
                DeliveryBall("6", 6, isSix = true)
            ),
            recentOvers = listOf(
                OverSummary(14, "Jofra Archer", 9, listOf(DeliveryBall("1", 1), DeliveryBall("1", 1), DeliveryBall("1", 1), DeliveryBall("4", 4, isFour = true), DeliveryBall("1", 1), DeliveryBall("1", 1)))
            ),
            currentBatters = listOf(
                BatsmanStats("sa_b1", "Heinrich Klaasen", 34, 19, 2, 3, isStriker = true),
                BatsmanStats("sa_b2", "David Miller", 12, 8, 1, 0, isStriker = false)
            ),
            currentBowler = BowlerStats("eng_bw1", "Sam Curran", 2.5f, 0, 26, 2, isCurrentBowler = true),
            currentPartnership = Partnership(22, 11, "Heinrich Klaasen", 14, "David Miller", 8),
            fallOfWickets = listOf(
                FallOfWicket(1, 24, 2.3f, "Quinton de Kock"),
                FallOfWicket(2, 58, 6.4f, "Aiden Markram"),
                FallOfWicket(3, 85, 10.1f, "Tristan Stubbs"),
                FallOfWicket(4, 102, 14.4f, "Ryan Rickelton")
            ),
            winPrediction = WinPrediction(52, 48),
            startTimeFormatted = "Today, 02:30 PM GMT",
            isFavorite = false
        )

        // Upcoming Matches
        val upcomingMatch1 = CricketMatch(
            id = "m_up_1",
            tournamentName = "ICC Men's T20 World Cup 2026",
            matchNumberDesc = "Final",
            venue = "Melbourne Cricket Ground",
            city = "Melbourne",
            format = MatchFormat.T20,
            maxOvers = 20,
            status = MatchStatus.UPCOMING,
            statusNote = "Starts in 2 days",
            team1 = teamInd,
            team2 = teamSa,
            team1Score = InningsScore("ind", 0, 0, 0f),
            team2Score = null,
            currentBattingTeamId = "ind",
            target = null,
            tossResult = "Toss yet to take place",
            startTimeFormatted = "Sunday, Oct 04 • 07:00 PM Local",
            isFavorite = true
        )

        val upcomingMatch2 = CricketMatch(
            id = "m_up_2",
            tournamentName = "Indian Premier League 2026",
            matchNumberDesc = "Match 1",
            venue = "MA Chidambaram Stadium",
            city = "Chennai",
            format = MatchFormat.T20,
            maxOvers = 20,
            status = MatchStatus.UPCOMING,
            statusNote = "Opening match of IPL 2026",
            team1 = teamCsk,
            team2 = teamMi,
            team1Score = InningsScore("csk", 0, 0, 0f),
            team2Score = null,
            currentBattingTeamId = "csk",
            target = null,
            tossResult = "Toss at 07:00 PM IST",
            startTimeFormatted = "Friday, Mar 27 • 07:30 PM IST",
            isFavorite = true
        )

        val upcomingMatch3 = CricketMatch(
            id = "m_up_3",
            tournamentName = "The Ashes 2026-27",
            matchNumberDesc = "1st Test",
            venue = "The Gabba",
            city = "Brisbane",
            format = MatchFormat.TEST,
            maxOvers = 90,
            status = MatchStatus.UPCOMING,
            statusNote = "Starts at 10:00 AM Local",
            team1 = teamAus,
            team2 = teamEng,
            team1Score = InningsScore("aus", 0, 0, 0f),
            team2Score = null,
            currentBattingTeamId = "aus",
            target = null,
            tossResult = "Toss at 09:30 AM Local",
            startTimeFormatted = "Nov 19 - Nov 23, 2026",
            isFavorite = false
        )

        // Completed Matches
        val completedMatch1 = CricketMatch(
            id = "m_comp_1",
            tournamentName = "ICC Men's T20 World Cup 2026",
            matchNumberDesc = "Super 8 - Match 12",
            venue = "Kensington Oval",
            city = "Barbados",
            format = MatchFormat.T20,
            maxOvers = 20,
            status = MatchStatus.COMPLETED,
            statusNote = "India won by 24 runs",
            team1 = teamInd,
            team2 = teamAus,
            team1Score = InningsScore("ind", 205, 5, 20.0f, isCompleted = true),
            team2Score = InningsScore("aus", 181, 7, 20.0f, isCompleted = true),
            currentBattingTeamId = "aus",
            target = 206,
            tossResult = "Australia won the toss and opted to bowl",
            startTimeFormatted = "Completed yesterday",
            isFavorite = true
        )

        val completedMatch2 = CricketMatch(
            id = "m_comp_2",
            tournamentName = "ICC Men's T20 World Cup 2026",
            matchNumberDesc = "Super 8 - Match 11",
            venue = "Sir Vivian Richards Stadium",
            city = "Antigua",
            format = MatchFormat.T20,
            maxOvers = 20,
            status = MatchStatus.COMPLETED,
            statusNote = "South Africa won by 18 runs",
            team1 = teamSa,
            team2 = teamPak,
            team1Score = InningsScore("sa", 175, 4, 20.0f, isCompleted = true),
            team2Score = InningsScore("pak", 157, 8, 20.0f, isCompleted = true),
            currentBattingTeamId = "pak",
            target = 176,
            tossResult = "South Africa won the toss and opted to bat",
            startTimeFormatted = "Completed 2 days ago",
            isFavorite = false
        )

        _matches.value = listOf(liveMatch1, liveMatch2, upcomingMatch1, upcomingMatch2, upcomingMatch3, completedMatch1, completedMatch2)
    }

    private fun startLiveScoreEngine() {
        simulationJob?.cancel()
        simulationJob = repositoryScope.launch {
            while (isActive) {
                delay(4000) // update live score every 4 seconds
                if (_isLiveSimulationRunning.value) {
                    simulateLiveDelivery()
                }
            }
        }
    }

    fun toggleLiveSimulation() {
        _isLiveSimulationRunning.value = !_isLiveSimulationRunning.value
    }

    fun triggerNextBallManually() {
        simulateLiveDelivery()
    }

    private fun simulateLiveDelivery() {
        val currentList = _matches.value.toMutableList()
        val matchIndex = currentList.indexOfFirst { it.id == "m_live_1" && it.status == MatchStatus.LIVE }
        if (matchIndex == -1) return

        val m = currentList[matchIndex]
        val score = m.team2Score ?: return
        val currentMaxBalls = m.maxOvers * 6
        val ballsBowled = score.ballsBowled()

        // Check if innings already won or completed
        val target = m.target
        if (target != null && score.runs >= target) {
            val updatedMatch = m.copy(
                status = MatchStatus.COMPLETED,
                statusNote = "${m.team2.name} won by ${10 - score.wickets} wickets!"
            )
            currentList[matchIndex] = updatedMatch
            _matches.value = currentList
            return
        }

        if (ballsBowled >= currentMaxBalls || score.wickets >= 10) {
            val updatedMatch = m.copy(
                status = MatchStatus.COMPLETED,
                statusNote = if (target != null && score.runs < target) {
                    "${m.team1.name} won by ${target - 1 - score.runs} runs!"
                } else "Match Tied"
            )
            currentList[matchIndex] = updatedMatch
            _matches.value = currentList
            return
        }

        // Realistic delivery generator
        val roll = Random.nextInt(100)
        val delivery: DeliveryBall
        var isWicket = false
        var isBoundary = false

        when {
            roll < 32 -> {
                // Dot
                delivery = DeliveryBall("0", 0)
            }
            roll < 62 -> {
                // 1 run
                delivery = DeliveryBall("1", 1)
            }
            roll < 72 -> {
                // 2 runs
                delivery = DeliveryBall("2", 2)
            }
            roll < 84 -> {
                // 4 FOUR
                delivery = DeliveryBall("4", 4, isFour = true)
                isBoundary = true
            }
            roll < 91 -> {
                // 6 SIX
                delivery = DeliveryBall("6", 6, isSix = true)
                isBoundary = true
            }
            roll < 97 -> {
                // Wicket
                delivery = DeliveryBall("W", 0, isWicket = true)
                isWicket = true
            }
            else -> {
                // 3 runs or extra
                delivery = DeliveryBall("1lb", 1, isExtra = true)
            }
        }

        val newRuns = score.runs + delivery.runs
        val newWickets = score.wickets + (if (isWicket) 1 else 0)

        // Increment ball
        val currentCompletedOvers = score.overs.toInt()
        val currentBallsInOver = ((score.overs - currentCompletedOvers) * 10).toInt()
        val newBallsInOver = currentBallsInOver + 1
        val isOverComplete = newBallsInOver >= 6

        val newOvers = if (isOverComplete) {
            (currentCompletedOvers + 1).toFloat()
        } else {
            currentCompletedOvers + (newBallsInOver / 10f)
        }

        val updatedTeam2Score = score.copy(
            runs = newRuns,
            wickets = newWickets,
            overs = newOvers
        )

        // Over balls list
        val updatedOverBalls = if (isOverComplete) {
            listOf(delivery) // start fresh or keep last ball
        } else {
            m.currentOverBalls + delivery
        }

        // Batters update
        var striker = m.currentBatters.find { it.isStriker } ?: m.currentBatters.firstOrNull()
        var nonStriker = m.currentBatters.find { !it.isStriker } ?: m.currentBatters.lastOrNull()
        val fowList = m.fallOfWickets.toMutableList()

        if (striker != null && nonStriker != null) {
            if (isWicket) {
                val dismissedBatterName = striker.name
                fowList.add(FallOfWicket(newWickets, newRuns, newOvers, dismissedBatterName))
                // New batter comes in
                val newBatterNames = listOf("Ravindra Jadeja", "Axar Patel", "Kuldeep Yadav", "Arshdeep Singh")
                val nextName = newBatterNames.getOrElse(newWickets - 4) { "Batter $newWickets" }
                striker = BatsmanStats(
                    id = "b_new_$newWickets",
                    name = nextName,
                    runs = 0,
                    balls = 0,
                    fours = 0,
                    sixes = 0,
                    isStriker = true
                )
                if (_notificationPreference.value.wickets) {
                    val bowlerName = m.currentBowler?.name ?: "Bowler"
                    _liveAlerts.tryEmit(
                        LiveEventAlert(
                            title = "🚨 WICKET FALLS!",
                            subtitle = "${m.team2.shortName} ${newRuns}/${newWickets} (${newOvers} ov) - $dismissedBatterName departs!",
                            isWicket = true
                        )
                    )
                    appContext?.let { ctx ->
                        LiveMatchNotificationScheduler.triggerWicketAlert(
                            context = ctx,
                            matchId = m.id,
                            matchTitle = "${m.team1.shortName} vs ${m.team2.shortName}",
                            tournament = m.tournamentName,
                            scoreText = "${m.team2.shortName} $newRuns/$newWickets (${newOvers} ov)",
                            batterName = dismissedBatterName,
                            dismissalInfo = "b $bowlerName",
                            bowlerName = bowlerName,
                            vibrationEnabled = _notificationPreference.value.vibrationEnabled
                        )
                    }
                }
            } else {
                val updatedStriker = striker.copy(
                    runs = striker.runs + delivery.runs,
                    balls = striker.balls + 1,
                    fours = striker.fours + (if (delivery.isFour) 1 else 0),
                    sixes = striker.sixes + (if (delivery.isSix) 1 else 0)
                )
                striker = updatedStriker

                if (isBoundary && _notificationPreference.value.boundaries) {
                    val boundaryType = if (delivery.isSix) "💥 MAXIMUM 6!" else "⚡ BOUNDARY 4!"
                    _liveAlerts.tryEmit(
                        LiveEventAlert(
                            title = boundaryType,
                            subtitle = "${striker.name} hits ${delivery.runs} runs! ${m.team2.shortName} ${newRuns}/${newWickets}",
                            isBoundary = true
                        )
                    )
                }

                // Swap strike on odd runs or over completion
                val shouldSwapForRuns = delivery.runs % 2 == 1
                if (shouldSwapForRuns xor isOverComplete) {
                    val temp = striker.copy(isStriker = false)
                    striker = nonStriker.copy(isStriker = true)
                    nonStriker = temp
                }
            }
        }

        // Bowler update
        val bowler = m.currentBowler?.let {
            val comp = it.overs.toInt()
            val b = ((it.overs - comp) * 10).toInt() + 1
            val updatedOvers = if (b >= 6) (comp + 1).toFloat() else comp + (b / 10f)
            it.copy(
                overs = updatedOvers,
                runs = it.runs + delivery.runs,
                wickets = it.wickets + (if (isWicket) 1 else 0),
                dots = it.dots + (if (delivery.runs == 0 && !delivery.isExtra) 1 else 0)
            )
        }

        // Partnership
        val p = m.currentPartnership
        val updatedPartnership = if (isWicket) {
            Partnership(0, 0, striker?.name ?: "Striker", 0, nonStriker?.name ?: "Non-Striker", 0)
        } else {
            p?.copy(
                runs = p.runs + delivery.runs,
                balls = p.balls + 1
            ) ?: Partnership(delivery.runs, 1, striker?.name ?: "", delivery.runs, nonStriker?.name ?: "", 0)
        }

        // Status note & Win prediction
        val runsNeeded = (target ?: 0) - newRuns
        val ballsLeft = currentMaxBalls - (newBallsInOver + currentCompletedOvers * 6)
        val note = if (target != null) {
            if (runsNeeded <= 0) "${m.team2.name} won by ${10 - newWickets} wickets!"
            else "${m.team2.shortName} need $runsNeeded runs in $ballsLeft balls"
        } else {
            "${m.team2.shortName} $newRuns/$newWickets (${newOvers} ov)"
        }

        // Dynamic win probability calculation
        val winT2 = when {
            runsNeeded <= 0 -> 100
            ballsLeft <= 0 -> 0
            else -> {
                val rrr = (runsNeeded.toFloat() / ballsLeft) * 6f
                val wicketsPenalty = (newWickets * 7)
                val base = 100 - (rrr * 6).toInt() - wicketsPenalty
                base.coerceIn(5, 95)
            }
        }
        val winT1 = 100 - winT2

        val updatedMatch = m.copy(
            team2Score = updatedTeam2Score,
            statusNote = note,
            currentOverBalls = updatedOverBalls,
            currentBatters = listOfNotNull(striker, nonStriker),
            currentBowler = bowler,
            currentPartnership = updatedPartnership,
            fallOfWickets = fowList,
            winPrediction = WinPrediction(winT1, winT2)
        )

        currentList[matchIndex] = updatedMatch
        _matches.value = currentList

        // Sync live delivery changes directly into Granular API Stats for real-time consistency
        val currentStats = _granularStatsMap.value[m.id]
        if (currentStats != null && currentStats.inningsList.size >= 2) {
            val inn2 = currentStats.inningsList[1]
            val updatedBatters = inn2.batters.map { b ->
                if (striker != null && b.name == striker.name) {
                    b.copy(
                        runs = striker.runs,
                        balls = striker.balls,
                        fours = striker.fours,
                        sixes = striker.sixes,
                        deathRuns = b.deathRuns + delivery.runs,
                        deathBalls = b.deathBalls + 1,
                        runsVsPace = b.runsVsPace + delivery.runs,
                        ballsVsPace = b.ballsVsPace + 1
                    )
                } else if (nonStriker != null && b.name == nonStriker.name) {
                    b.copy(
                        runs = nonStriker.runs,
                        balls = nonStriker.balls,
                        fours = nonStriker.fours,
                        sixes = nonStriker.sixes
                    )
                } else b
            }
            val updatedBowlers = inn2.bowlers.map { bw ->
                if (bowler != null && bw.name == bowler.name) {
                    bw.copy(
                        overs = bowler.overs,
                        runsConceded = bowler.runs,
                        wickets = bowler.wickets,
                        dotBalls = bowler.dots
                    )
                } else bw
            }
            val updatedPartnerships = inn2.partnerships.map { part ->
                if (part.isCurrentStand && updatedPartnership != null) {
                    part.copy(
                        totalRuns = updatedPartnership.runs,
                        totalBalls = updatedPartnership.balls
                    )
                } else part
            }
            val updatedInn2 = inn2.copy(
                totalRuns = newRuns,
                wickets = newWickets,
                overs = newOvers,
                batters = updatedBatters,
                bowlers = updatedBowlers,
                partnerships = updatedPartnerships
            )
            val newStatsMap = _granularStatsMap.value.toMutableMap()
            newStatsMap[m.id] = currentStats.copy(
                inningsList = listOf(currentStats.inningsList[0], updatedInn2)
            )
            _granularStatsMap.value = newStatsMap
        }
    }

    suspend fun fetchGranularStats(matchId: String, forceRefresh: Boolean = false) {
        if (!forceRefresh && _granularStatsMap.value.containsKey(matchId)) {
            return
        }
        val loadingMap = _isStatsLoading.value.toMutableMap()
        loadingMap[matchId] = true
        _isStatsLoading.value = loadingMap

        try {
            val stats = apiService.getMatchGranularStats(matchId)
            val currentMap = _granularStatsMap.value.toMutableMap()
            currentMap[matchId] = stats
            _granularStatsMap.value = currentMap
        } catch (e: Exception) {
            // keep existing or empty
        } finally {
            val updatedLoading = _isStatsLoading.value.toMutableMap()
            updatedLoading[matchId] = false
            _isStatsLoading.value = updatedLoading
        }
    }

    fun toggleFavorite(teamId: String) {
        val current = _favoriteTeamIds.value.toMutableSet()
        if (current.contains(teamId)) current.remove(teamId) else current.add(teamId)
        _favoriteTeamIds.value = current
    }

    fun toggleMatchFavorite(matchId: String) {
        _matches.value = _matches.value.map {
            if (it.id == matchId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun updateNotificationPreferences(pref: NotificationPreference) {
        _notificationPreference.value = pref
    }

    fun setMatchReminder(reminder: MatchReminder) {
        val current = _matchReminders.value.toMutableMap()
        current[reminder.matchId] = reminder
        _matchReminders.value = current
    }

    fun removeMatchReminder(matchId: String) {
        val current = _matchReminders.value.toMutableMap()
        current.remove(matchId)
        _matchReminders.value = current
    }

    fun resetMatch(matchId: String) {
        loadInitialData()
    }

    fun triggerTestWicketNotification(context: Context) {
        val liveMatch = _matches.value.find { it.status == MatchStatus.LIVE } ?: _matches.value.first()
        val batter = liveMatch.currentBatters.firstOrNull()?.name ?: "Virat Kohli"
        val bowler = liveMatch.currentBowler?.name ?: "Mitchell Starc"
        val score = liveMatch.team2Score?.let { "${liveMatch.team2.shortName} ${it.runs}/${it.wickets} (${it.overs} ov)" }
            ?: "${liveMatch.team1.shortName} 182/4 (17.4 ov)"
        LiveMatchNotificationScheduler.triggerWicketAlert(
            context = context,
            matchId = liveMatch.id,
            matchTitle = "${liveMatch.team1.shortName} vs ${liveMatch.team2.shortName}",
            tournament = liveMatch.tournamentName,
            scoreText = score,
            batterName = batter,
            dismissalInfo = "c Smith b $bowler - 67 (42b)",
            bowlerName = bowler,
            vibrationEnabled = _notificationPreference.value.vibrationEnabled
        )
    }

    fun triggerTestMatchStartNotification(context: Context, delaySeconds: Long = 0L) {
        val upcoming = _matches.value.find { it.status == MatchStatus.UPCOMING } ?: _matches.value.first()
        LiveMatchNotificationScheduler.scheduleMatchStart(
            context = context,
            match = upcoming,
            delaySeconds = delaySeconds,
            vibrationEnabled = _notificationPreference.value.vibrationEnabled
        )
    }

    fun startMatchAsLive(context: Context, matchId: String) {
        val currentList = _matches.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == matchId }
        if (index != -1) {
            val m = currentList[index]
            val updated = m.copy(
                status = MatchStatus.LIVE,
                statusNote = "Match started! Live action underway"
            )
            currentList[index] = updated
            _matches.value = currentList

            if (_notificationPreference.value.matchStart) {
                _liveAlerts.tryEmit(
                    LiveEventAlert(
                        title = "🏏 MATCH STARTED!",
                        subtitle = "${m.team1.shortName} vs ${m.team2.shortName} is now LIVE!",
                        isBoundary = false
                    )
                )
                LiveMatchNotificationScheduler.triggerMatchStartAlert(
                    context = context,
                    matchId = m.id,
                    matchTitle = "${m.team1.shortName} vs ${m.team2.shortName}",
                    tournament = m.tournamentName,
                    details = "${m.venue}, ${m.city} • Toss concluded, 1st innings started!",
                    vibrationEnabled = _notificationPreference.value.vibrationEnabled
                )
            }
        }
    }
}
