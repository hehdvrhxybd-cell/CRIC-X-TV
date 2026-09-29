package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CricketMatch
import com.example.data.model.InningsScore
import com.example.data.model.MatchReminder
import com.example.data.model.MatchStatus
import com.example.data.model.Team
import com.example.ui.theme.*
import java.util.Locale

/**
 * Formats an abbreviated cricket score line for quick glance.
 * Example: "182/4 (17.4 ov)" or "Yet to bat"
 */
fun formatAbbreviatedScore(score: InningsScore?): String {
    if (score == null) return "Yet to bat"
    return "${score.runs}/${score.wickets} (${score.formattedOvers()} ov)"
}

/**
 * Formats a short compact score (e.g. "182/4") without overs.
 */
fun formatCompactScore(score: InningsScore?): String {
    if (score == null) return "-"
    return "${score.runs}/${score.wickets}"
}

/**
 * Match Card UI composable that displays:
 * 1. Team names & short codes with crests
 * 2. Match status (live pulsing badge, upcoming countdown/schedule, completed)
 * 3. Abbreviated scores for ongoing and upcoming cricket matches
 */
@Composable
fun MatchCard(
    match: CricketMatch,
    onMatchClick: (CricketMatch) -> Unit,
    modifier: Modifier = Modifier,
    onToggleFavorite: ((String) -> Unit)? = null,
    onReminderClick: ((CricketMatch) -> Unit)? = null,
    reminder: MatchReminder? = null
) {
    val isLive = match.status == MatchStatus.LIVE || match.status == MatchStatus.INNINGS_BREAK
    val isUpcoming = match.status == MatchStatus.UPCOMING

    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val liveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("match_card_${match.id}")
            .clickable { onMatchClick(match) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = if (isLive) {
                Brush.linearGradient(
                    listOf(
                        CricketNeonGreen.copy(alpha = 0.5f),
                        CardBorder,
                        CricketNeonGreen.copy(alpha = 0.2f)
                    )
                )
            } else {
                Brush.linearGradient(listOf(CardBorder, CardBorder))
            }
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Tournament + Format & Status Pill + Actions
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Match Status Pill (Live / Upcoming / Break)
                    MatchStatusBadge(
                        status = match.status,
                        startTimeFormatted = match.startTimeFormatted,
                        liveAlpha = liveAlpha
                    )

                    // WorkManager Match Reminder Button (for Upcoming fixtures)
                    if (isUpcoming && onReminderClick != null) {
                        IconButton(
                            onClick = { onReminderClick(match) },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("match_card_reminder_${match.id}")
                        ) {
                            Icon(
                                imageVector = if (reminder != null) Icons.Default.NotificationsActive else Icons.Outlined.NotificationsNone,
                                contentDescription = "Match Reminder",
                                tint = if (reminder != null) CricketNeonGreen else TextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Favorite Star Toggle
                    if (onToggleFavorite != null) {
                        IconButton(
                            onClick = { onToggleFavorite(match.id) },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("match_card_fav_${match.id}")
                        ) {
                            Icon(
                                imageVector = if (match.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Toggle Favorite",
                                tint = if (match.isFavorite) CricketGold else TextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Team 1 Row: Name, Crest, Short Code & Abbreviated Score
            TeamScoreRow(
                teamName = match.team1.name,
                teamShortName = match.team1.shortName,
                team = match.team1,
                abbreviatedScore = if (isUpcoming) "Yet to bat" else formatAbbreviatedScore(match.team1Score),
                isBatting = isLive && match.currentBattingTeam?.id == match.team1.id,
                isUpcoming = isUpcoming
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Team 2 Row: Name, Crest, Short Code & Abbreviated Score
            TeamScoreRow(
                teamName = match.team2.name,
                teamShortName = match.team2.shortName,
                team = match.team2,
                abbreviatedScore = if (isUpcoming) "Yet to bat" else formatAbbreviatedScore(match.team2Score),
                isBatting = isLive && match.currentBattingTeam?.id == match.team2.id,
                isUpcoming = isUpcoming
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Footer: Abbreviated Status / Rates / Delivery Balls / Schedule
            MatchAbbreviatedFooter(
                match = match,
                reminder = reminder,
                onReminderClick = onReminderClick
            )
        }
    }
}

/**
 * Row displaying team crest, team name, team short code, and abbreviated score.
 */
@Composable
fun TeamScoreRow(
    teamName: String,
    teamShortName: String,
    team: Team,
    abbreviatedScore: String,
    isBatting: Boolean,
    isUpcoming: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TeamLogoBadge(team = team, size = 30.dp)
        Spacer(modifier = Modifier.width(10.dp))

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = teamName,
                fontWeight = if (isBatting) FontWeight.Black else FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isBatting) TextPrimary else TextPrimary.copy(alpha = 0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceContainerHighest)
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = teamShortName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
            if (isBatting) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(CricketNeonGreen)
                )
            }
        }

        // Abbreviated Score Box
        if (isUpcoming) {
            Text(
                text = "Yet to bat",
                fontSize = 12.sp,
                color = TextTertiary,
                fontWeight = FontWeight.Medium
            )
        } else {
            Text(
                text = abbreviatedScore,
                fontSize = 13.5.sp,
                fontWeight = if (isBatting) FontWeight.Black else FontWeight.Bold,
                color = if (isBatting) CricketNeonGreen else TextPrimary
            )
        }
    }
}

/**
 * Match Status Badge showing Live (with pulsating indicator), Upcoming (with time), or Completed.
 */
@Composable
fun MatchStatusBadge(
    status: MatchStatus,
    startTimeFormatted: String,
    liveAlpha: Float = 1f,
    modifier: Modifier = Modifier
) {
    when (status) {
        MatchStatus.LIVE -> {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(LiveRed.copy(alpha = 0.15f))
                    .border(1.dp, LiveRed.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
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
                fontSize = 10.5.sp,
                modifier = modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(ExtraYellow.copy(alpha = 0.15f))
                    .border(1.dp, ExtraYellow.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
        MatchStatus.UPCOMING -> {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CricketNeonGreen.copy(alpha = 0.12f))
                    .border(1.dp, CricketNeonGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = CricketNeonGreen,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "UPCOMING",
                    color = CricketNeonGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                )
            }
        }
        MatchStatus.COMPLETED -> {
            Text(
                text = "COMPLETED",
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                modifier = modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceVariantDark)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

/**
 * Bottom abbreviated footer showing run rates, current situation, balls preview, or upcoming venue.
 */
@Composable
fun MatchAbbreviatedFooter(
    match: CricketMatch,
    reminder: MatchReminder?,
    onReminderClick: ((CricketMatch) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceVariantDark)
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        when (match.status) {
            MatchStatus.LIVE -> {
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

                        val crr = match.currentInningsScore.runRate()
                        Text(
                            text = "CRR: ${String.format(Locale.US, "%.2f", crr)}" +
                                    (match.requiredRunRate?.let { " • RRR: ${String.format(Locale.US, "%.2f", it)}" } ?: ""),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }

                    // Over balls micro strip
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
                                BallBubble(ball = ball, size = 18.dp)
                            }
                        }
                    }
                }
            }
            MatchStatus.INNINGS_BREAK -> {
                Text(
                    text = match.statusNote.ifEmpty { "Innings break in progress" },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ExtraYellow
                )
            }
            MatchStatus.UPCOMING -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Starts ${match.startTimeFormatted}",
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
                        Spacer(modifier = Modifier.height(6.dp))
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
                                text = if (reminder != null) "Reminder active (${reminder.leadDescription}) • WorkManager" else "Set WorkManager Reminder",
                                fontSize = 11.sp,
                                fontWeight = if (reminder != null) FontWeight.Bold else FontWeight.Medium,
                                color = if (reminder != null) CricketNeonGreen else TextSecondary
                            )
                        }
                    }
                }
            }
            MatchStatus.COMPLETED -> {
                Text(
                    text = "🏆 ${match.statusNote}",
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

/**
 * Filter mode for Ongoing & Upcoming match lists.
 */
enum class MatchListFilter(val title: String) {
    ALL("ALL"),
    ONGOING("LIVE NOW"),
    UPCOMING("UPCOMING")
}

/**
 * Dedicated composable function for displaying a list of ongoing and upcoming cricket matches
 * utilizing the [MatchCard] component.
 */
@Composable
fun OngoingUpcomingMatchList(
    matches: List<CricketMatch>,
    onMatchClick: (CricketMatch) -> Unit,
    modifier: Modifier = Modifier,
    matchReminders: Map<String, MatchReminder> = emptyMap(),
    onToggleFavorite: ((String) -> Unit)? = null,
    onReminderClick: ((CricketMatch) -> Unit)? = null
) {
    var selectedFilter by remember { mutableStateOf(MatchListFilter.ALL) }

    val ongoingMatches = remember(matches) {
        matches.filter { it.status == MatchStatus.LIVE || it.status == MatchStatus.INNINGS_BREAK }
    }
    val upcomingMatches = remember(matches) {
        matches.filter { it.status == MatchStatus.UPCOMING }
    }

    val displayMatches = remember(matches, selectedFilter) {
        when (selectedFilter) {
            MatchListFilter.ALL -> ongoingMatches + upcomingMatches
            MatchListFilter.ONGOING -> ongoingMatches
            MatchListFilter.UPCOMING -> upcomingMatches
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ongoing_upcoming_match_list")
    ) {
        // Filter Tabs: ALL | LIVE NOW | UPCOMING
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MatchListFilter.values().forEach { filter ->
                val isSelected = selectedFilter == filter
                val count = when (filter) {
                    MatchListFilter.ALL -> ongoingMatches.size + upcomingMatches.size
                    MatchListFilter.ONGOING -> ongoingMatches.size
                    MatchListFilter.UPCOMING -> upcomingMatches.size
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CricketNeonGreen else Color.Transparent)
                        .clickable { selectedFilter = filter }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${filter.title} ($count)",
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                        fontSize = 11.5.sp,
                        color = if (isSelected) BackgroundDark else TextSecondary,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Match list items
        if (displayMatches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (selectedFilter) {
                        MatchListFilter.ONGOING -> "No ongoing cricket matches right now."
                        MatchListFilter.UPCOMING -> "No upcoming matches scheduled."
                        MatchListFilter.ALL -> "No ongoing or upcoming matches found."
                    },
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                displayMatches.forEach { match ->
                    MatchCard(
                        match = match,
                        onMatchClick = onMatchClick,
                        onToggleFavorite = onToggleFavorite,
                        onReminderClick = onReminderClick,
                        reminder = matchReminders[match.id]
                    )
                }
            }
        }
    }
}
