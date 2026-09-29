package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CricketMatch
import com.example.data.model.MatchReminder
import com.example.data.model.MatchStatus
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun MatchScoreCard(
    match: CricketMatch,
    onMatchClick: (CricketMatch) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier,
    reminder: MatchReminder? = null,
    onReminderClick: ((CricketMatch) -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val liveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "liveAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable { onMatchClick(match) }
            .padding(14.dp)
    ) {
        // Header: Tournament + Status Pill + Favorite Star
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = match.tournamentName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CricketGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${match.matchNumberDesc} • ${match.format} • ${match.city}",
                    fontSize = 11.sp,
                    color = TextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Status Pill
                when (match.status) {
                    MatchStatus.LIVE -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(LiveRed.copy(alpha = 0.15f))
                                .border(1.dp, LiveRed.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(LiveRed.copy(alpha = liveAlpha))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "LIVE",
                                color = LiveRed,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                    MatchStatus.INNINGS_BREAK -> {
                        Text(
                            text = "INNINGS BREAK",
                            color = ExtraYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(ExtraYellow.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    MatchStatus.UPCOMING -> {
                        Text(
                            text = "UPCOMING",
                            color = CricketNeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(CricketNeonGreen.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    MatchStatus.COMPLETED -> {
                        Text(
                            text = "COMPLETED",
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(SurfaceVariantDark)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                if (match.status == MatchStatus.UPCOMING && onReminderClick != null) {
                    IconButton(
                        onClick = { onReminderClick(match) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (reminder != null) Icons.Default.NotificationsActive else Icons.Outlined.NotificationsNone,
                            contentDescription = "Match Reminder",
                            tint = if (reminder != null) CricketNeonGreen else TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { onToggleFavorite(match.id) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (match.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (match.isFavorite) CricketGold else TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Teams and Scores
        // Team 1 Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamLogoBadge(team = match.team1, size = 32.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = match.team1.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            if (match.status != MatchStatus.UPCOMING) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${match.team1Score.runs}/${match.team1Score.wickets}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${match.team1Score.formattedOvers()})",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Team 2 Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamLogoBadge(team = match.team2, size = 32.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = match.team2.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            if (match.status != MatchStatus.UPCOMING && match.team2Score != null) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${match.team2Score.runs}/${match.team2Score.wickets}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (match.status == MatchStatus.LIVE) CricketNeonGreen else TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${match.team2Score.formattedOvers()})",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Info Row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceVariantDark)
                .padding(horizontal = 10.dp, vertical = 7.dp)
        ) {
            if (match.status == MatchStatus.LIVE) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = match.statusNote,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CricketNeonGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        // Current run rate
                        val crr = match.currentInningsScore.runRate()
                        Text(
                            text = "CRR: ${String.format(Locale.US, "%.2f", crr)}" +
                                    (match.requiredRunRate?.let { " • RRR: ${String.format(Locale.US, "%.2f", it)}" } ?: ""),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }

                    // Over balls preview
                    if (match.currentOverBalls.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "This Over:",
                                fontSize = 10.sp,
                                color = TextTertiary,
                                fontWeight = FontWeight.Bold
                            )
                            match.currentOverBalls.takeLast(6).forEach { ball ->
                                BallBubble(ball = ball, size = 20.dp)
                            }
                        }
                    }
                }
            } else if (match.status == MatchStatus.UPCOMING) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = match.startTimeFormatted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CricketNeonGreen
                        )
                        Text(
                            text = match.venue,
                            fontSize = 11.sp,
                            color = TextTertiary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (onReminderClick != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (reminder != null) CricketNeonGreen.copy(alpha = 0.15f) else SurfaceContainerHighest)
                                .border(
                                    0.5.dp,
                                    if (reminder != null) CricketNeonGreen else CardBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { onReminderClick(match) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (reminder != null) Icons.Default.NotificationsActive else Icons.Outlined.NotificationsNone,
                                contentDescription = null,
                                tint = if (reminder != null) CricketNeonGreen else TextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (reminder != null) "Reminder set (${reminder.leadDescription}) • WorkManager" else "Set Reminder via WorkManager",
                                fontSize = 11.sp,
                                fontWeight = if (reminder != null) FontWeight.Bold else FontWeight.Medium,
                                color = if (reminder != null) CricketNeonGreen else TextSecondary
                            )
                        }
                    }
                }
            } else {
                // Completed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏆 " + match.statusNote,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricketGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
