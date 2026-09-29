package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.CricketMatch
import com.example.data.model.MatchReminder
import com.example.data.model.NotificationPreference
import com.example.ui.components.TeamLogoBadge
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
    currentPreferences: NotificationPreference,
    matchReminders: Map<String, MatchReminder>,
    upcomingMatches: List<CricketMatch>,
    onUpdatePreferences: (NotificationPreference) -> Unit,
    onCancelReminder: (String) -> Unit,
    onSetReminderClick: (CricketMatch) -> Unit,
    onTestWicketAlert: () -> Unit = {},
    onTestMatchStartAlert: () -> Unit = {},
    onScheduleMatchStartDemo: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    var prefs by remember { mutableStateOf(currentPreferences) }
    var actionFeedback by remember { mutableStateOf<String?>(null) }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            actionFeedback = "Push notifications permission granted!"
        }
    }

    fun update(newPrefs: NotificationPreference) {
        prefs = newPrefs
        onUpdatePreferences(newPrefs)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Push Notifications & Alerts",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Notification Permission Banner (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                item {
                    if (!hasNotificationPermission) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("notification_permission_banner"),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            shape = RoundedCornerShape(14.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(CricketGold, CricketGold))
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = CricketGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Notification Permission Needed",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = CricketGold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Enable push notification permission so WorkManager can alert you on your lockscreen when live matches start and wickets fall.",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CricketGold),
                                    modifier = Modifier.testTag("grant_notification_permission_button")
                                ) {
                                    Text("Grant Notification Permission", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CricketNeonGreen.copy(alpha = 0.12f))
                                .border(1.dp, CricketNeonGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CricketNeonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Push Notifications Active (Android System & WorkManager)",
                                color = CricketNeonGreen,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Temporary Action Feedback Alert
            if (actionFeedback != null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CricketGold.copy(alpha = 0.15f))
                            .border(1.dp, CricketGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = actionFeedback ?: "",
                            color = CricketGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { actionFeedback = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = CricketGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // WorkManager Push Notification Live Trigger Hub
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("workmanager_push_hub"),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(CricketNeonGreen.copy(alpha = 0.6f), CardBorder))
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CricketNeonGreen.copy(alpha = 0.18f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ElectricBolt,
                                        contentDescription = null,
                                        tint = CricketNeonGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "WORKMANAGER PUSH ENGINE",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Background alerts via Android WorkManager",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CricketNeonGreen)
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "HIGH PRIORITY",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BackgroundDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Test live push notifications delivered directly to the Android notification shade with sound, vibration, and deep links:",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Test Wicket Alert Button
                        OutlinedButton(
                            onClick = {
                                onTestWicketAlert()
                                actionFeedback = "⚡ Wicket push notification enqueued with WorkManager!"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_test_wicket_alert"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = CricketNeonGreen
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(CricketNeonGreen, CricketNeonGreen))
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsCricket,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("⚡ Test Wicket Alert (WorkManager)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Test Match Start Alert Button
                        OutlinedButton(
                            onClick = {
                                onTestMatchStartAlert()
                                actionFeedback = "🏏 Live match start notification enqueued with WorkManager!"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_test_match_start_alert"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = CricketGold
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(CricketGold, CricketGold))
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("🏏 Test Match Start Alert (WorkManager)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Schedule Match Start (5s Demo) Button
                        OutlinedButton(
                            onClick = {
                                onScheduleMatchStartDemo()
                                actionFeedback = "⏱️ Match start alert scheduled! WorkManager will fire in 5s."
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_schedule_match_start_demo"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextPrimary
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(CardBorder, CardBorder))
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("⏱️ Schedule Match Start (5s WorkManager Demo)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tapping any notification deep links directly into the match scorecard.",
                                fontSize = 11.sp,
                                color = TextTertiary
                            )
                        }
                    }
                }
            }

            // Scheduled Match Reminders Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SCHEDULED MATCH REMINDERS (${matchReminders.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricketGold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "WorkManager",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricketNeonGreen
                    )
                }
            }

            if (matchReminders.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark)
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No upcoming match reminders set",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Pick an upcoming match below to schedule a local reminder",
                            fontSize = 11.sp,
                            color = TextTertiary
                        )
                    }
                }
            } else {
                items(matchReminders.values.toList(), key = { it.matchId }) { reminder ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark)
                            .border(1.dp, CricketNeonGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CricketNeonGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = CricketNeonGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = reminder.matchTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${reminder.tournamentName} • ${reminder.scheduledDisplayTime}",
                                    fontSize = 11.sp,
                                    color = TextTertiary
                                )
                                Text(
                                    text = "Alert ${reminder.leadDescription} (WorkManager)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CricketNeonGreen
                                )
                            }
                        }

                        IconButton(onClick = { onCancelReminder(reminder.matchId) }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Cancel Reminder",
                                tint = LiveRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Quick Remind Upcoming Matches List
            if (upcomingMatches.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SET REMINDER FOR UPCOMING MATCHES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricketNeonGreen,
                        letterSpacing = 1.sp
                    )
                }

                items(upcomingMatches, key = { "rem_up_" + it.id }) { match ->
                    val hasReminder = matchReminders.containsKey(match.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark)
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                            .clickable { onSetReminderClick(match) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            TeamLogoBadge(team = match.team1, size = 28.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = match.team1.shortName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = " vs ",
                                fontSize = 11.sp,
                                color = TextTertiary
                            )
                            Text(
                                text = match.team2.shortName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            TeamLogoBadge(team = match.team2, size = 28.dp)
                        }

                        Button(
                            onClick = { onSetReminderClick(match) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasReminder) SurfaceContainerHighest else CricketNeonGreen,
                                contentColor = if (hasReminder) CricketNeonGreen else BackgroundDark
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (hasReminder) "Active 🔔" else "+ Remind",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // General notification settings
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LIVE MATCH ALERTS PREFERENCES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CricketNeonGreen,
                    letterSpacing = 1.sp
                )
            }

            item {
                NotificationToggleItem(
                    title = "Wicket Alerts",
                    subtitle = "WorkManager push notification whenever a wicket falls in live matches",
                    checked = prefs.wickets,
                    onCheckedChange = { update(prefs.copy(wickets = it)) }
                )
            }

            item {
                NotificationToggleItem(
                    title = "Match Start & Toss",
                    subtitle = "WorkManager push notification when live match starts & toss result",
                    checked = prefs.matchStart,
                    onCheckedChange = { update(prefs.copy(matchStart = it)) }
                )
            }

            item {
                NotificationToggleItem(
                    title = "Boundary Alerts (4s & 6s)",
                    subtitle = "Get alerted when boundaries and maximum sixes are hit",
                    checked = prefs.boundaries,
                    onCheckedChange = { update(prefs.copy(boundaries = it)) }
                )
            }

            item {
                NotificationToggleItem(
                    title = "Milestone Alerts",
                    subtitle = "Batter 50s, 100s, and bowler 5-wicket hauls",
                    checked = prefs.milestones,
                    onCheckedChange = { update(prefs.copy(milestones = it)) }
                )
            }

            item {
                NotificationToggleItem(
                    title = "Innings Break",
                    subtitle = "Scores summary at the end of each innings",
                    checked = prefs.inningsBreak,
                    onCheckedChange = { update(prefs.copy(inningsBreak = it)) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ALERT FEEDBACK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CricketGold,
                    letterSpacing = 1.sp
                )
            }

            item {
                NotificationToggleItem(
                    title = "Vibration Alert",
                    subtitle = "Vibrate phone on critical live wickets and match starts",
                    checked = prefs.vibrationEnabled,
                    onCheckedChange = { update(prefs.copy(vibrationEnabled = it)) }
                )
            }
        }
    }
}

@Composable
private fun NotificationToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BackgroundDark,
                checkedTrackColor = CricketNeonGreen,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = SurfaceVariantDark
            )
        )
    }
}
