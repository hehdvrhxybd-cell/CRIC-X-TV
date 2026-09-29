package com.example.worker

import android.content.Context
import androidx.work.*
import com.example.data.model.CricketMatch
import com.example.data.model.MatchReminder
import java.util.concurrent.TimeUnit

object MatchReminderScheduler {

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

    private fun getUniqueWorkName(matchId: String): String = "match_reminder_$matchId"

    fun scheduleMatchReminder(
        context: Context,
        match: CricketMatch,
        leadMinutes: Int,
        customDelaySeconds: Long? = null
    ): MatchReminder {
        val workManager = getWorkManager(context)

        val leadDesc = when {
            customDelaySeconds != null && customDelaySeconds <= 15 -> "in a few seconds (Demo)"
            leadMinutes == 15 -> "in 15 minutes"
            leadMinutes == 30 -> "in 30 minutes"
            leadMinutes == 60 -> "in 1 hour"
            else -> "soon"
        }

        val initialDelaySec = customDelaySeconds ?: when (leadMinutes) {
            15 -> 15L * 60L
            30 -> 30L * 60L
            60 -> 60L * 60L
            else -> 10L
        }

        val matchTitle = "${match.team1.shortName} vs ${match.team2.shortName}"

        val inputData = Data.Builder()
            .putString(MatchReminderWorker.KEY_MATCH_ID, match.id)
            .putString(MatchReminderWorker.KEY_MATCH_TITLE, matchTitle)
            .putString(MatchReminderWorker.KEY_TOURNAMENT, match.tournamentName)
            .putString(MatchReminderWorker.KEY_VENUE, "${match.venue}, ${match.city}")
            .putString(MatchReminderWorker.KEY_LEAD_DESC, leadDesc)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<MatchReminderWorker>()
            .setInitialDelay(initialDelaySec, TimeUnit.SECONDS)
            .setInputData(inputData)
            .addTag("match_reminder")
            .addTag("match_${match.id}")
            .build()

        workManager.enqueueUniqueWork(
            getUniqueWorkName(match.id),
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        return MatchReminder(
            matchId = match.id,
            matchTitle = matchTitle,
            tournamentName = match.tournamentName,
            venue = "${match.venue}, ${match.city}",
            scheduledDisplayTime = match.startTimeFormatted,
            leadMinutes = leadMinutes,
            leadDescription = leadDesc
        )
    }

    fun cancelMatchReminder(context: Context, matchId: String) {
        val workManager = getWorkManager(context)
        workManager.cancelUniqueWork(getUniqueWorkName(matchId))
    }
}
