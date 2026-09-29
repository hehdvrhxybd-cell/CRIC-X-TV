package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.InningsScore
import com.example.data.repository.CricketRepository
import com.example.worker.LiveMatchAlertWorker
import com.example.worker.LiveMatchNotificationScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CRIC X TV", appName)
    }

    @Test
    fun `innings score calculations are accurate`() {
        val score = InningsScore(teamId = "ind", runs = 180, wickets = 4, overs = 18.0f)
        assertEquals("18.0", score.formattedOvers())
        assertEquals(108, score.ballsBowled())
        assertEquals(10.0f, score.runRate(), 0.01f)
    }

    @Test
    fun `repository provides matches and teams`() {
        val repo = CricketRepository()
        assertTrue(repo.matches.value.isNotEmpty())
        assertTrue(repo.teams.value.isNotEmpty())
        assertTrue(repo.players.value.isNotEmpty())
        assertTrue(repo.seriesList.value.isNotEmpty())

        val liveMatch = repo.matches.value.find { it.id == "m_live_1" }
        assertNotNull(liveMatch)
        assertEquals("IND", liveMatch?.currentBattingTeam?.shortName)
    }

    @Test
    fun `match reminder scheduling and removal in repository`() {
        val repo = CricketRepository()
        val reminder = com.example.data.model.MatchReminder(
            matchId = "m_up_1",
            matchTitle = "ENG vs SA",
            tournamentName = "ICC Men's T20 World Cup",
            venue = "SuperSport Park, Centurion",
            scheduledDisplayTime = "Today, 07:30 PM",
            leadMinutes = 15,
            leadDescription = "in 15 minutes"
        )
        repo.setMatchReminder(reminder)
        assertTrue(repo.matchReminders.value.containsKey("m_up_1"))
        assertEquals("ENG vs SA", repo.matchReminders.value["m_up_1"]?.matchTitle)

        repo.removeMatchReminder("m_up_1")
        assertTrue(repo.matchReminders.value.isEmpty())
    }

    @Test
    fun `granular batter stats calculations are accurate`() {
        val batter = com.example.data.model.GranularBatterStats(
            playerId = "b1",
            name = "Travis Head",
            runs = 64,
            balls = 38,
            fours = 8,
            sixes = 3,
            dotBalls = 11,
            powerplayRuns = 42,
            powerplayBalls = 22,
            runsVsPace = 44,
            ballsVsPace = 24,
            runsVsSpin = 20,
            ballsVsSpin = 14
        )

        assertEquals(168.42f, batter.overallStrikeRate, 0.05f)
        assertEquals(50, batter.boundaryRuns)
        assertEquals(78.125f, batter.boundaryRunPercent, 0.05f)
        assertEquals(28.947f, batter.dotBallPercent, 0.05f)
        assertEquals(190.9f, batter.powerplayStrikeRate ?: 0f, 0.1f)
        assertEquals(183.33f, batter.strikeRateVsPace, 0.05f)
        assertEquals(142.85f, batter.strikeRateVsSpin, 0.05f)
    }

    @Test
    fun `granular bowler stats calculations are accurate`() {
        val bowler = com.example.data.model.GranularBowlerStats(
            playerId = "bw2",
            name = "Jasprit Bumrah",
            overs = 4.0f,
            maidens = 0,
            runsConceded = 24,
            wickets = 2,
            dotBalls = 14,
            powerplayOvers = 2.0f,
            powerplayRuns = 11,
            powerplayWickets = 0
        )

        assertEquals(24, bowler.totalBalls)
        assertEquals(6.0f, bowler.overallEconomy, 0.01f)
        assertEquals(58.33f, bowler.dotBallPercent, 0.05f)
        assertEquals(5.5f, bowler.powerplayEconomy ?: 0f, 0.05f)
    }

    @Test
    fun `detailed partnership calculations are accurate`() {
        val partnership = com.example.data.model.DetailedPartnership(
            wicketNumber = 2,
            totalRuns = 68,
            totalBalls = 42,
            batter1Name = "Travis Head",
            batter1Runs = 44,
            batter1Balls = 24,
            batter2Name = "Mitchell Marsh",
            batter2Runs = 23,
            batter2Balls = 18,
            extras = 1,
            oversSpan = "Overs 4.2 - 11.1"
        )

        assertEquals(9.71f, partnership.runRate, 0.02f)
        assertEquals(183.33f, partnership.batter1StrikeRate, 0.05f)
        assertEquals(127.77f, partnership.batter2StrikeRate, 0.05f)
        assertEquals(64.7f, partnership.batter1SharePercent, 0.1f)
        assertEquals(33.82f, partnership.batter2SharePercent, 0.1f)
    }

    @Test
    fun `api service fetches match granular statistics`() = kotlinx.coroutines.runBlocking {
        val apiService = com.example.data.api.CricketApiServiceImpl()
        val stats = apiService.getMatchGranularStats("m_live_1")
        assertNotNull(stats)
        assertEquals("m_live_1", stats.matchId)
        assertTrue(stats.inningsList.isNotEmpty())
        val ausInnings = stats.inningsList[0]
        assertEquals("Australia", ausInnings.teamName)
        assertTrue(ausInnings.batters.isNotEmpty())
        assertTrue(ausInnings.bowlers.isNotEmpty())
        assertTrue(ausInnings.partnerships.isNotEmpty())
    }

    @Test
    fun `matches can be filtered by team name and short code`() {
        val repo = CricketRepository()
        val allMatches = repo.matches.value

        // Filter by full team name "Australia"
        val queryAus = "australia"
        val ausMatches = allMatches.filter {
            it.team1.name.contains(queryAus, ignoreCase = true) ||
            it.team1.shortName.contains(queryAus, ignoreCase = true) ||
            it.team2.name.contains(queryAus, ignoreCase = true) ||
            it.team2.shortName.contains(queryAus, ignoreCase = true)
        }
        assertTrue(ausMatches.isNotEmpty())
        assertTrue(ausMatches.any { it.team1.shortName == "AUS" || it.team2.shortName == "AUS" })

        // Filter by short team code "IND"
        val queryInd = "IND"
        val indMatches = allMatches.filter {
            it.team1.name.contains(queryInd, ignoreCase = true) ||
            it.team1.shortName.contains(queryInd, ignoreCase = true) ||
            it.team2.name.contains(queryInd, ignoreCase = true) ||
            it.team2.shortName.contains(queryInd, ignoreCase = true)
        }
        assertTrue(indMatches.isNotEmpty())
        assertTrue(indMatches.any { it.team1.shortName == "IND" || it.team2.shortName == "IND" })
    }

    @Test
    fun `matches can be filtered by series title`() {
        val repo = CricketRepository()
        val allMatches = repo.matches.value

        val querySeries = "t20 world cup"
        val seriesMatches = allMatches.filter {
            it.tournamentName.contains(querySeries, ignoreCase = true)
        }
        assertTrue(seriesMatches.isNotEmpty())
        assertTrue(seriesMatches.all { it.tournamentName.contains("T20 World Cup", ignoreCase = true) })

        val queryAshes = "ashes"
        val ashesMatches = allMatches.filter {
            it.tournamentName.contains(queryAshes, ignoreCase = true)
        }
        assertTrue(ashesMatches.isNotEmpty())
        assertTrue(ashesMatches.all { it.tournamentName.contains("Ashes", ignoreCase = true) })
    }

    @Test
    fun `live match notification scheduler enqueues wicket alert work`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LiveMatchNotificationScheduler.triggerWicketAlert(
            context = context,
            matchId = "m_live_1",
            matchTitle = "IND vs AUS",
            tournament = "ICC Men's T20 World Cup 2026",
            scoreText = "AUS 142/4 (16.2 ov)",
            batterName = "Travis Head",
            dismissalInfo = "c Kohli b Bumrah",
            bowlerName = "Jasprit Bumrah"
        )
        val workManager = LiveMatchNotificationScheduler.getWorkManager(context)
        val workInfos = workManager.getWorkInfosByTag("wicket_alert").get()
        assertTrue(workInfos.isNotEmpty())
    }

    @Test
    fun `live match notification scheduler enqueues match start alert work`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LiveMatchNotificationScheduler.triggerMatchStartAlert(
            context = context,
            matchId = "m_up_1",
            matchTitle = "ENG vs SA",
            tournament = "ICC Men's T20 World Cup 2026",
            details = "Melbourne Cricket Ground • Toss at 06:30 PM",
            delaySeconds = 0L
        )
        val workManager = LiveMatchNotificationScheduler.getWorkManager(context)
        val workInfos = workManager.getWorkInfosByTag("match_start_alert").get()
        assertTrue(workInfos.isNotEmpty())
    }

    @Test
    fun `notification channel created for live match alerts`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val channel = android.app.NotificationChannel(
            LiveMatchAlertWorker.CHANNEL_ID,
            LiveMatchAlertWorker.CHANNEL_NAME,
            android.app.NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = LiveMatchAlertWorker.CHANNEL_DESC
        }
        notificationManager.createNotificationChannel(channel)
        val retrieved = notificationManager.getNotificationChannel(LiveMatchAlertWorker.CHANNEL_ID)
        assertNotNull(retrieved)
        assertEquals(LiveMatchAlertWorker.CHANNEL_NAME, retrieved.name)
    }

    @Test
    fun `repository triggers live notifications when context is attached`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = CricketRepository()
        repo.attachContext(context)
        repo.updateNotificationPreferences(
            com.example.data.model.NotificationPreference(
                matchStart = true,
                wickets = true
            )
        )
        assertTrue(repo.notificationPreference.value.wickets)
        assertTrue(repo.notificationPreference.value.matchStart)

        repo.triggerTestWicketNotification(context)
        val workManager = LiveMatchNotificationScheduler.getWorkManager(context)
        val wicketWorks = workManager.getWorkInfosByTag("wicket_alert").get()
        assertTrue(wicketWorks.isNotEmpty())
    }

    @Test
    fun `abbreviated score formatting displays correct runs, wickets, and overs`() {
        val score = InningsScore(teamId = "ind", runs = 182, wickets = 4, overs = 17.4f)
        val formatted = com.example.ui.components.formatAbbreviatedScore(score)
        assertEquals("182/4 (17.4 ov)", formatted)

        val compact = com.example.ui.components.formatCompactScore(score)
        assertEquals("182/4", compact)

        val nullFormatted = com.example.ui.components.formatAbbreviatedScore(null)
        assertEquals("Yet to bat", nullFormatted)

        val nullCompact = com.example.ui.components.formatCompactScore(null)
        assertEquals("-", nullCompact)
    }

    @Test
    fun `ongoing and upcoming matches list filtering separates statuses properly`() {
        val repo = CricketRepository()
        val allMatches = repo.matches.value

        val ongoing = allMatches.filter {
            it.status == com.example.data.model.MatchStatus.LIVE ||
            it.status == com.example.data.model.MatchStatus.INNINGS_BREAK
        }
        val upcoming = allMatches.filter {
            it.status == com.example.data.model.MatchStatus.UPCOMING
        }

        assertTrue(ongoing.isNotEmpty())
        assertTrue(upcoming.isNotEmpty())
        assertTrue(ongoing.all { it.status == com.example.data.model.MatchStatus.LIVE || it.status == com.example.data.model.MatchStatus.INNINGS_BREAK })
        assertTrue(upcoming.all { it.status == com.example.data.model.MatchStatus.UPCOMING })
    }
}

