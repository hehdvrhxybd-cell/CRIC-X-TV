package com.example.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R

class LiveMatchAlertWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "cric_x_live_alerts"
        const val CHANNEL_NAME = "CRIC X TV Live Alerts"
        const val CHANNEL_DESC = "Instant push notifications for live match starts, wickets, and match turning points"

        const val KEY_ALERT_TYPE = "KEY_ALERT_TYPE"
        const val TYPE_WICKET = "WICKET"
        const val TYPE_MATCH_START = "MATCH_START"

        const val KEY_MATCH_ID = "KEY_MATCH_ID"
        const val KEY_MATCH_TITLE = "KEY_MATCH_TITLE"
        const val KEY_TOURNAMENT = "KEY_TOURNAMENT"
        const val KEY_SCORE_TEXT = "KEY_SCORE_TEXT"
        const val KEY_BATTER_NAME = "KEY_BATTER_NAME"
        const val KEY_DISMISSAL_INFO = "KEY_DISMISSAL_INFO"
        const val KEY_BOWLER_NAME = "KEY_BOWLER_NAME"
        const val KEY_DETAILS = "KEY_DETAILS"
        const val KEY_VIBRATION = "KEY_VIBRATION"
    }

    override suspend fun doWork(): Result {
        val alertType = inputData.getString(KEY_ALERT_TYPE) ?: TYPE_WICKET
        val matchId = inputData.getString(KEY_MATCH_ID) ?: "m_live_1"
        val matchTitle = inputData.getString(KEY_MATCH_TITLE) ?: "Cricket Match"
        val tournament = inputData.getString(KEY_TOURNAMENT) ?: "CRIC X TV"
        val scoreText = inputData.getString(KEY_SCORE_TEXT) ?: ""
        val batterName = inputData.getString(KEY_BATTER_NAME) ?: "Batter"
        val dismissalInfo = inputData.getString(KEY_DISMISSAL_INFO) ?: "out"
        val bowlerName = inputData.getString(KEY_BOWLER_NAME) ?: "Bowler"
        val details = inputData.getString(KEY_DETAILS) ?: ""
        val vibrationEnabled = inputData.getBoolean(KEY_VIBRATION, true)

        createNotificationChannel()

        if (alertType == TYPE_WICKET) {
            showWicketNotification(
                matchId = matchId,
                matchTitle = matchTitle,
                tournament = tournament,
                scoreText = scoreText,
                batterName = batterName,
                dismissalInfo = dismissalInfo,
                bowlerName = bowlerName,
                vibrationEnabled = vibrationEnabled
            )
        } else {
            showMatchStartNotification(
                matchId = matchId,
                matchTitle = matchTitle,
                tournament = tournament,
                details = details,
                vibrationEnabled = vibrationEnabled
            )
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    private fun getPendingIntent(matchId: String): PendingIntent {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_MATCH_ID", matchId)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(
            applicationContext,
            matchId.hashCode() + System.currentTimeMillis().toInt(),
            intent,
            flags
        )
    }

    private fun showWicketNotification(
        matchId: String,
        matchTitle: String,
        tournament: String,
        scoreText: String,
        batterName: String,
        dismissalInfo: String,
        bowlerName: String,
        vibrationEnabled: Boolean
    ) {
        if (!checkPermission()) return

        val pendingIntent = getPendingIntent(matchId)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val title = "🚨 WICKET FALLS! $batterName is OUT!"
        val contentText = if (scoreText.isNotEmpty()) "$matchTitle: $scoreText ($dismissalInfo)" else "$matchTitle • $dismissalInfo"

        val bigText = buildString {
            append("🚨 WICKET FALLEN IN $matchTitle\n")
            append("Batter: $batterName ($dismissalInfo)\n")
            if (bowlerName.isNotEmpty()) append("Bowler: $bowlerName\n")
            if (scoreText.isNotEmpty()) append("Live Score: $scoreText\n")
            append("Tournament: $tournament\n")
            append("Tap to open live ball-by-ball scorecard & player statistics!")
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_cricket)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setColor(Color.parseColor("#00E676")) // Cricket Neon Green
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .addAction(
                R.drawable.ic_notification_cricket,
                "View Scorecard",
                pendingIntent
            )

        if (vibrationEnabled) {
            notificationBuilder.setVibrate(longArrayOf(0, 300, 150, 300))
        }

        val notificationId = 1000 + (matchId.hashCode() % 1000) + 1
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        try {
            notificationManager.notify(notificationId, notificationBuilder.build())
        } catch (_: SecurityException) {
            // Handled when permission is revoked
        }
    }

    private fun showMatchStartNotification(
        matchId: String,
        matchTitle: String,
        tournament: String,
        details: String,
        vibrationEnabled: Boolean
    ) {
        if (!checkPermission()) return

        val pendingIntent = getPendingIntent(matchId)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val title = "🏏 MATCH IS LIVE: $matchTitle"
        val contentText = "$tournament • Match started! Action is underway."

        val bigText = buildString {
            append("🏏 LIVE ACTION UNDERWAY: $matchTitle!\n")
            append("Tournament: $tournament\n")
            if (details.isNotEmpty()) append("Match Info: $details\n")
            append("Live scores, live wagon wheels, and real-time player strike rates are now updating!")
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_cricket)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setColor(Color.parseColor("#FFD700")) // Cricket Gold
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .addAction(
                R.drawable.ic_notification_cricket,
                "Open Live Match",
                pendingIntent
            )

        if (vibrationEnabled) {
            notificationBuilder.setVibrate(longArrayOf(0, 200, 100, 200))
        }

        val notificationId = 2000 + (matchId.hashCode() % 1000) + 2
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        try {
            notificationManager.notify(notificationId, notificationBuilder.build())
        } catch (_: SecurityException) {
            // Handled when permission is revoked
        }
    }
}
