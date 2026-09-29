package com.example.worker

import android.content.Context
import androidx.work.*
import com.example.data.model.CricketMatch
import java.util.concurrent.TimeUnit

object LiveMatchNotificationScheduler {

    fun getWorkManager(context: Context): WorkManager {
        return try {
            WorkManager.getInstance(context)
        } catch (e: IllegalStateException) {
            try {
                val config = Configuration.Builder().build()
                WorkManager.initialize(context, config)
            } catch (_: Exception) {}
            WorkManager.getInstance(context)
        }
    }

    private fun getWicketWorkName(matchId: String, timestamp: Long): String = "wicket_${matchId}_$timestamp"
    private fun getMatchStartWorkName(matchId: String): String = "match_start_$matchId"

    /**
     * Enqueues an immediate WorkManager task to alert the user of a wicket falling in a live match.
     */
    fun triggerWicketAlert(
        context: Context,
        matchId: String,
        matchTitle: String,
        tournament: String,
        scoreText: String,
        batterName: String,
        dismissalInfo: String = "out",
        bowlerName: String = "",
        vibrationEnabled: Boolean = true
    ) {
        val inputData = Data.Builder()
            .putString(LiveMatchAlertWorker.KEY_ALERT_TYPE, LiveMatchAlertWorker.TYPE_WICKET)
            .putString(LiveMatchAlertWorker.KEY_MATCH_ID, matchId)
            .putString(LiveMatchAlertWorker.KEY_MATCH_TITLE, matchTitle)
            .putString(LiveMatchAlertWorker.KEY_TOURNAMENT, tournament)
            .putString(LiveMatchAlertWorker.KEY_SCORE_TEXT, scoreText)
            .putString(LiveMatchAlertWorker.KEY_BATTER_NAME, batterName)
            .putString(LiveMatchAlertWorker.KEY_DISMISSAL_INFO, dismissalInfo)
            .putString(LiveMatchAlertWorker.KEY_BOWLER_NAME, bowlerName)
            .putBoolean(LiveMatchAlertWorker.KEY_VIBRATION, vibrationEnabled)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<LiveMatchAlertWorker>()
            .setInputData(inputData)
            .addTag("live_cricket_alert")
            .addTag("wicket_alert")
            .addTag("match_$matchId")
            .build()

        getWorkManager(context).enqueue(workRequest)
    }

    /**
     * Enqueues a WorkManager task to alert the user when a live match starts (immediately or with delay).
     */
    fun triggerMatchStartAlert(
        context: Context,
        matchId: String,
        matchTitle: String,
        tournament: String,
        details: String = "",
        delaySeconds: Long = 0L,
        vibrationEnabled: Boolean = true
    ) {
        val inputData = Data.Builder()
            .putString(LiveMatchAlertWorker.KEY_ALERT_TYPE, LiveMatchAlertWorker.TYPE_MATCH_START)
            .putString(LiveMatchAlertWorker.KEY_MATCH_ID, matchId)
            .putString(LiveMatchAlertWorker.KEY_MATCH_TITLE, matchTitle)
            .putString(LiveMatchAlertWorker.KEY_TOURNAMENT, tournament)
            .putString(LiveMatchAlertWorker.KEY_DETAILS, details)
            .putBoolean(LiveMatchAlertWorker.KEY_VIBRATION, vibrationEnabled)
            .build()

        val builder = OneTimeWorkRequestBuilder<LiveMatchAlertWorker>()
            .setInputData(inputData)
            .addTag("live_cricket_alert")
            .addTag("match_start_alert")
            .addTag("match_$matchId")

        if (delaySeconds > 0L) {
            builder.setInitialDelay(delaySeconds, TimeUnit.SECONDS)
        }

        val workRequest = builder.build()

        getWorkManager(context).enqueueUniqueWork(
            getMatchStartWorkName(matchId),
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    /**
     * Convenience helper to schedule an alert from a CricketMatch instance.
     */
    fun scheduleMatchStart(
        context: Context,
        match: CricketMatch,
        delaySeconds: Long = 0L,
        vibrationEnabled: Boolean = true
    ) {
        val title = "${match.team1.shortName} vs ${match.team2.shortName}"
        val details = "${match.venue}, ${match.city} • Format: ${match.format}"
        triggerMatchStartAlert(
            context = context,
            matchId = match.id,
            matchTitle = title,
            tournament = match.tournamentName,
            details = details,
            delaySeconds = delaySeconds,
            vibrationEnabled = vibrationEnabled
        )
    }

    /**
     * Cancel scheduled live alerts for a match.
     */
    fun cancelMatchAlerts(context: Context, matchId: String) {
        val workManager = getWorkManager(context)
        workManager.cancelUniqueWork(getMatchStartWorkName(matchId))
        workManager.cancelAllWorkByTag("match_$matchId")
    }
}

