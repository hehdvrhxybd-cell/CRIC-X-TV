package com.example.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R

class MatchReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "cric_x_match_reminders"
        const val CHANNEL_NAME = "CRIC X TV Match Reminders"
        const val CHANNEL_DESC = "Notifications and reminders for upcoming cricket match fixtures"

        const val KEY_MATCH_ID = "KEY_MATCH_ID"
        const val KEY_MATCH_TITLE = "KEY_MATCH_TITLE"
        const val KEY_TOURNAMENT = "KEY_TOURNAMENT"
        const val KEY_VENUE = "KEY_VENUE"
        const val KEY_LEAD_DESC = "KEY_LEAD_DESC"
    }

    override suspend fun doWork(): Result {
        val matchId = inputData.getString(KEY_MATCH_ID) ?: return Result.failure()
        val matchTitle = inputData.getString(KEY_MATCH_TITLE) ?: "Cricket Match"
        val tournament = inputData.getString(KEY_TOURNAMENT) ?: "CRIC X TV"
        val venue = inputData.getString(KEY_VENUE) ?: ""
        val leadDesc = inputData.getString(KEY_LEAD_DESC) ?: "soon"

        createNotificationChannel()
        showNotification(matchId, matchTitle, tournament, venue, leadDesc)

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(
        matchId: String,
        matchTitle: String,
        tournament: String,
        venue: String,
        leadDesc: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                return
            }
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_MATCH_ID", matchId)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            matchId.hashCode(),
            intent,
            pendingIntentFlags
        )

        val title = "🏏 Match Starting $leadDesc: $matchTitle"
        val contentText = "$tournament • $venue"
        val bigText = "🏏 Match Starting $leadDesc: $matchTitle\n$tournament\n📍 Venue: $venue\nOpen CRIC X TV for real-time scores, team line-ups, and live updates!"

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.cric_x_tv_icon_1790598756088)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val notificationId = (matchId.hashCode() and 0x7FFFFFFF)
        NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)
    }
}
