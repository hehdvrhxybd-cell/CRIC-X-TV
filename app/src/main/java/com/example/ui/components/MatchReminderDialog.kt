package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.data.model.CricketMatch
import com.example.data.model.MatchReminder
import com.example.ui.theme.*

@Composable
fun MatchReminderDialog(
    match: CricketMatch,
    existingReminder: MatchReminder?,
    onSetReminder: (leadMinutes: Int, customDelaySeconds: Long?) -> Unit,
    onCancelReminder: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedOption by remember { mutableIntStateOf(existingReminder?.leadMinutes ?: 15) }
    var isTestModeSelected by remember { mutableStateOf(false) }

    // Android 13+ Notification Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val delaySeconds = if (isTestModeSelected) 10L else null
            onSetReminder(selectedOption, delaySeconds)
            Toast.makeText(
                context,
                "Reminder scheduled with WorkManager!",
                Toast.LENGTH_SHORT
            ).show()
            onDismiss()
        } else {
            Toast.makeText(
                context,
                "Notification permission required for match reminders",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun submitReminder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }

        val delaySeconds = if (isTestModeSelected) 10L else null
        onSetReminder(selectedOption, delaySeconds)
        val msg = if (isTestModeSelected) {
            "WorkManager task enqueued: notification in 10s!"
        } else {
            "WorkManager reminder set for $selectedOption mins before!"
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        onDismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            // Dialog Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CricketNeonGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = CricketNeonGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Set Match Reminder",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Scheduled via WorkManager",
                            fontSize = 11.sp,
                            color = CricketNeonGreen
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Match Preview Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceVariantDark)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamLogoBadge(team = match.team1, size = 32.dp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = match.team1.shortName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Text(
                    text = " vs ",
                    fontSize = 12.sp,
                    color = TextTertiary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = match.team2.shortName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                TeamLogoBadge(team = match.team2, size = 32.dp)

                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = match.startTimeFormatted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CricketGold
                    )
                    Text(
                        text = match.city,
                        fontSize = 10.sp,
                        color = TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "WHEN WOULD YOU LIKE TO BE NOTIFIED?",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Options: 15 mins, 30 mins, 1 hour
            val options = listOf(
                Pair(15, "15 minutes before match"),
                Pair(30, "30 minutes before match"),
                Pair(60, "1 hour before match")
            )

            options.forEach { (mins, label) ->
                val isSelected = !isTestModeSelected && selectedOption == mins
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) SurfaceContainerHighest else SurfaceVariantDark)
                        .border(
                            1.dp,
                            if (isSelected) CricketNeonGreen else CardBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            isTestModeSelected = false
                            selectedOption = mins
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isSelected) CricketNeonGreen else TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TextPrimary else TextSecondary
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = {
                            isTestModeSelected = false
                            selectedOption = mins
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = CricketNeonGreen,
                            unselectedColor = TextTertiary
                        ),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Test WorkManager in 10s option
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isTestModeSelected) SurfaceContainerHighest else SurfaceVariantDark)
                    .border(
                        1.dp,
                        if (isTestModeSelected) CricketGold else CardBorder,
                        RoundedCornerShape(10.dp)
                    )
                        .clickable { isTestModeSelected = true }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "⚡ Test WorkManager (Fires in 10s)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isTestModeSelected) CricketGold else TextPrimary
                    )
                    Text(
                        text = "Instant demo: schedule local notification right now",
                        fontSize = 10.sp,
                        color = TextTertiary
                    )
                }

                RadioButton(
                    selected = isTestModeSelected,
                    onClick = { isTestModeSelected = true },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = CricketGold,
                        unselectedColor = TextTertiary
                    ),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            Button(
                onClick = { submitReminder() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CricketNeonGreen,
                    contentColor = BackgroundDark
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (existingReminder != null) "Update Reminder" else "Set Reminder",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            if (existingReminder != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        onCancelReminder()
                        Toast.makeText(context, "Reminder cancelled", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LiveRed),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(LiveRed.copy(alpha = 0.5f))),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Cancel Scheduled Reminder",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
