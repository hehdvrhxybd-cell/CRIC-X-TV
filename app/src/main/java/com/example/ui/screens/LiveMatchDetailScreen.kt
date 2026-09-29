package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.Locale

enum class MatchDetailTab(val title: String) {
    SUMMARY("LIVE SUMMARY"),
    GRANULAR_STATS("GRANULAR STATS"),
    PARTNERSHIP("PARTNERSHIPS"),
    SCORECARD("SCORECARD"),
    INFO("MATCH INFO")
}

enum class GranularStatFilter(val label: String) {
    BATTERS("STRIKE RATES"),
    BOWLERS("ECONOMY"),
    PHASES("PHASE BREAKDOWN")
}

@Composable
fun LiveMatchDetailScreen(
    match: CricketMatch,
    isSimulationRunning: Boolean,
    onToggleSimulation: () -> Unit,
    onTriggerNextBall: () -> Unit,
    onResetMatch: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    reminder: MatchReminder? = null,
    onReminderClick: ((CricketMatch) -> Unit)? = null,
    granularStats: MatchGranularStats? = null,
    isLoadingStats: Boolean = false,
    onRefreshStats: () -> Unit = {}
) {
    BackHandler(onBack = onBack)

    var selectedTab by remember { mutableStateOf(MatchDetailTab.SUMMARY) }
    var selectedScorecardInnings by remember { mutableStateOf(1) }
    var selectedStatsFilter by remember { mutableStateOf(GranularStatFilter.BATTERS) }
    var selectedStatsInnings by remember { mutableStateOf(if ((granularStats?.inningsList?.size ?: 0) >= 2) 2 else 1) }
    var selectedPartnershipInnings by remember { mutableStateOf(if ((granularStats?.inningsList?.size ?: 0) >= 2) 2 else 1) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                        Column {
                            Text(
                                text = "${match.team1.shortName} vs ${match.team2.shortName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${match.tournamentName} • ${match.format}",
                                fontSize = 11.sp,
                                color = TextTertiary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Match Reminder button (for upcoming matches)
                        if (match.status == MatchStatus.UPCOMING && onReminderClick != null) {
                            IconButton(onClick = { onReminderClick(match) }) {
                                Icon(
                                    imageVector = if (reminder != null) Icons.Default.NotificationsActive else Icons.Outlined.NotificationsNone,
                                    contentDescription = "Match Reminder",
                                    tint = if (reminder != null) CricketNeonGreen else TextTertiary
                                )
                            }
                        }

                        // Live simulation pause/play (for live matches)
                        if (match.status == MatchStatus.LIVE) {
                            IconButton(onClick = onToggleSimulation) {
                                Icon(
                                    imageVector = if (isSimulationRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Toggle Live Simulation",
                                    tint = if (isSimulationRunning) CricketNeonGreen else TextTertiary
                                )
                            }

                            // Next ball manual trigger
                            IconButton(onClick = onTriggerNextBall) {
                                Icon(
                                    imageVector = Icons.Default.FastForward,
                                    contentDescription = "Simulate Ball",
                                    tint = CricketGold
                                )
                            }
                        }

                        // Favorite star
                        IconButton(onClick = { onToggleFavorite(match.id) }) {
                            Icon(
                                imageVector = if (match.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Favorite",
                                tint = if (match.isFavorite) CricketGold else TextTertiary
                            )
                        }
                    }
                }

                // Sub-tabs: SUMMARY | SCORECARD | PARTNERSHIP | INFO
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MatchDetailTab.values()) { tab ->
                        val isSelected = selectedTab == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CricketNeonGreen else SurfaceDark)
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = if (isSelected) BackgroundDark else TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
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
            // 1. Large Live Scoreboard Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(SurfaceVariantDark, SurfaceDark)
                            )
                        )
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    // Match Status Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (match.status == MatchStatus.LIVE) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(LiveRed.copy(alpha = dotAlpha))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE SCORE",
                                    color = LiveRed,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp
                                )
                            } else {
                                Text(
                                    text = match.status.name,
                                    color = CricketGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = match.venue,
                            color = TextTertiary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Scores Display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Team 1
                        Column(horizontalAlignment = Alignment.Start) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TeamLogoBadge(team = match.team1, size = 38.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = match.team1.shortName,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${match.team1Score.runs}/${match.team1Score.wickets}",
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Overs: ${match.team1Score.formattedOvers()}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        // VS / Target
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "VS",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = TextTertiary
                            )
                            if (match.target != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Target: ${match.target}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = CricketGold
                                )
                            }
                        }

                        // Team 2
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = match.team2.shortName,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                TeamLogoBadge(team = match.team2, size = 38.dp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            val t2Score = match.team2Score
                            if (t2Score != null) {
                                Text(
                                    text = "${t2Score.runs}/${t2Score.wickets}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp,
                                    color = CricketNeonGreen
                                )
                                Text(
                                    text = "Overs: ${t2Score.formattedOvers()}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            } else {
                                Text(
                                    text = "Yet to bat",
                                    fontSize = 13.sp,
                                    color = TextTertiary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Equation & Run Rates
                    HorizontalDivider(color = CardBorder.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = match.statusNote,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CricketNeonGreen,
                            modifier = Modifier.weight(1f)
                        )

                        val crr = match.currentInningsScore.runRate()
                        Text(
                            text = "CRR: ${String.format(Locale.US, "%.2f", crr)}" +
                                    (match.requiredRunRate?.let { " • RRR: ${String.format(Locale.US, "%.2f", it)}" } ?: ""),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }

                    // Current Over Balls
                    if (match.currentOverBalls.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Current Over:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextTertiary
                            )
                            match.currentOverBalls.forEach { ball ->
                                BallBubble(ball = ball, size = 26.dp)
                            }
                        }
                    }
                }
            }

            // Dedicated WorkManager Reminder Section for Upcoming Matches
            if (match.status == MatchStatus.UPCOMING && onReminderClick != null) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark)
                            .border(1.dp, if (reminder != null) CricketNeonGreen else CardBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
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
                                        .background(if (reminder != null) CricketNeonGreen.copy(alpha = 0.2f) else SurfaceVariantDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (reminder != null) Icons.Default.NotificationsActive else Icons.Outlined.NotificationsNone,
                                        contentDescription = null,
                                        tint = if (reminder != null) CricketNeonGreen else CricketGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (reminder != null) "Reminder Active" else "Match Reminder",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (reminder != null) "WorkManager will notify you ${reminder.leadDescription}" else "Never miss toss or first ball",
                                        fontSize = 12.sp,
                                        color = if (reminder != null) CricketNeonGreen else TextTertiary
                                    )
                                }
                            }

                            Button(
                                onClick = { onReminderClick(match) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (reminder != null) SurfaceContainerHighest else CricketNeonGreen,
                                    contentColor = if (reminder != null) TextPrimary else BackgroundDark
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (reminder != null) "Edit" else "Set Reminder",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                MatchDetailTab.SUMMARY -> {
                    // Batsmen Table
                    if (match.currentBatters.isNotEmpty()) {
                        item {
                            LiveBattersTable(batters = match.currentBatters)
                        }
                    }

                    // Current Bowler Table
                    if (match.currentBowler != null) {
                        item {
                            LiveBowlerTable(bowler = match.currentBowler)
                        }
                    }

                    // Current Partnership
                    if (match.currentPartnership != null) {
                        item {
                            PartnershipCard(partnership = match.currentPartnership)
                        }
                    }

                    // Win Probability
                    item {
                        WinPredictorBar(match = match)
                    }

                    // Recent Overs Summary
                    if (match.recentOvers.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceDark)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "RECENT OVERS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                match.recentOvers.forEach { over ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Over ${over.overNumber}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = over.bowlerName,
                                                fontSize = 11.sp,
                                                color = TextTertiary
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            over.balls.forEach { ball ->
                                                BallBubble(ball = ball, size = 22.dp)
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "= ${over.totalRuns} runs",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = CricketNeonGreen
                                            )
                                        }
                                    }
                                    HorizontalDivider(
                                        color = CardBorder.copy(alpha = 0.3f),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                MatchDetailTab.SCORECARD -> {
                    val scorecard = match.scorecard
                    if (scorecard != null) {
                        item {
                            // Innings Toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceDark)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selectedScorecardInnings == 1) CricketNeonGreen else Color.Transparent)
                                        .clickable { selectedScorecardInnings = 1 }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${scorecard.innings1.teamName} (${scorecard.innings1.runs}/${scorecard.innings1.wickets})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (selectedScorecardInnings == 1) BackgroundDark else TextSecondary
                                    )
                                }

                                if (scorecard.innings2 != null) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (selectedScorecardInnings == 2) CricketNeonGreen else Color.Transparent)
                                            .clickable { selectedScorecardInnings = 2 }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${scorecard.innings2.teamName} (${scorecard.innings2.runs}/${scorecard.innings2.wickets})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (selectedScorecardInnings == 2) BackgroundDark else TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        val activeInnings = if (selectedScorecardInnings == 1) scorecard.innings1 else (scorecard.innings2 ?: scorecard.innings1)

                        // Batting Innings Table
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceDark)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "BATTING",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                HorizontalDivider(color = CardBorder.copy(alpha = 0.5f), thickness = 1.dp)

                                activeInnings.batters.forEach { b ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(2f)) {
                                            Text(
                                                text = b.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = b.dismissalText,
                                                fontSize = 11.sp,
                                                color = TextTertiary
                                            )
                                        }

                                        Text(
                                            text = "${b.runs}",
                                            modifier = Modifier.weight(0.6f),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextPrimary,
                                            textAlign = TextAlign.End
                                        )
                                        Text(
                                            text = "${b.balls}",
                                            modifier = Modifier.weight(0.5f),
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            textAlign = TextAlign.End
                                        )
                                        Text(
                                            text = "${b.fours}",
                                            modifier = Modifier.weight(0.5f),
                                            fontSize = 12.sp,
                                            color = FourBlue,
                                            textAlign = TextAlign.End
                                        )
                                        Text(
                                            text = "${b.sixes}",
                                            modifier = Modifier.weight(0.5f),
                                            fontSize = 12.sp,
                                            color = SixPurple,
                                            textAlign = TextAlign.End
                                        )
                                        Text(
                                            text = String.format(Locale.US, "%.1f", b.strikeRate),
                                            modifier = Modifier.weight(0.8f),
                                            fontSize = 12.sp,
                                            color = TextPrimary,
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                HorizontalDivider(color = CardBorder.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(6.dp))

                                // Extras & Total
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Extras (w ${activeInnings.extras.wides}, nb ${activeInnings.extras.noBalls}, lb ${activeInnings.extras.legByes}, b ${activeInnings.extras.byes})",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "${activeInnings.extras.total}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Total Runs (${activeInnings.overs} ov, RR ${String.format(Locale.US, "%.2f", (activeInnings.runs.toFloat() / ((activeInnings.overs.toInt() * 6) + ((activeInnings.overs - activeInnings.overs.toInt()) * 10).toInt()).coerceAtLeast(1)) * 6)})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = CricketNeonGreen
                                    )
                                    Text(
                                        text = "${activeInnings.runs}/${activeInnings.wickets}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                }

                                if (activeInnings.didNotBat.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Did not bat: " + activeInnings.didNotBat.joinToString(", "),
                                        fontSize = 11.sp,
                                        color = TextTertiary
                                    )
                                }
                            }
                        }

                        // Bowling Innings Table
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceDark)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "BOWLING",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                HorizontalDivider(color = CardBorder.copy(alpha = 0.5f), thickness = 1.dp)

                                activeInnings.bowlers.forEach { bw ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = bw.name,
                                            modifier = Modifier.weight(2f),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = bw.formattedOvers(),
                                            modifier = Modifier.weight(0.5f),
                                            fontSize = 12.sp,
                                            color = TextPrimary,
                                            textAlign = TextAlign.End
                                        )
                                        Text(
                                            text = "${bw.maidens}",
                                            modifier = Modifier.weight(0.4f),
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            textAlign = TextAlign.End
                                        )
                                        Text(
                                            text = "${bw.runs}",
                                            modifier = Modifier.weight(0.5f),
                                            fontSize = 12.sp,
                                            color = TextPrimary,
                                            textAlign = TextAlign.End
                                        )
                                        Text(
                                            text = "${bw.wickets}",
                                            modifier = Modifier.weight(0.5f),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = LiveRed,
                                            textAlign = TextAlign.End
                                        )
                                        Text(
                                            text = String.format(Locale.US, "%.2f", bw.economy),
                                            modifier = Modifier.weight(0.8f),
                                            fontSize = 12.sp,
                                            color = TextPrimary,
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Full scorecard not yet available for this match",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                MatchDetailTab.GRANULAR_STATS -> {
                    // API Status Header
                    item {
                        if (granularStats != null) {
                            ApiFeedStatusHeader(
                                sourceApi = granularStats.sourceApi,
                                latencyMs = granularStats.latencyMs,
                                lastUpdated = granularStats.lastUpdated,
                                isLoading = isLoadingStats,
                                onRefresh = onRefreshStats
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceDark)
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = CricketNeonGreen,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Connecting to Cricket Telemetry API...",
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    if (granularStats != null && granularStats.inningsList.isNotEmpty()) {
                        val inningsList = granularStats.inningsList
                        val currentInningsIdx = (selectedStatsInnings - 1).coerceIn(0, inningsList.size - 1)
                        val activeInnings = inningsList[currentInningsIdx]

                        // Innings selector if more than 1 innings
                        if (inningsList.size > 1) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceDark)
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    inningsList.forEachIndexed { index, inn ->
                                        val isSel = (index + 1) == selectedStatsInnings
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) CricketNeonGreen else Color.Transparent)
                                                .clickable { selectedStatsInnings = index + 1 }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${inn.teamName} (${inn.totalRuns}/${inn.wickets})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (isSel) BackgroundDark else TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Granular Stat Filter Sub-Tabs: STRIKE RATES | ECONOMY | PHASE BREAKDOWN
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceVariantDark)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                GranularStatFilter.values().forEach { filter ->
                                    val isFilterSel = selectedStatsFilter == filter
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(if (isFilterSel) SurfaceDark else Color.Transparent)
                                            .clickable { selectedStatsFilter = filter }
                                            .padding(vertical = 7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = filter.label,
                                            fontWeight = if (isFilterSel) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp,
                                            color = if (isFilterSel) CricketGold else TextTertiary
                                        )
                                    }
                                }
                            }
                        }

                        // Filter Content
                        when (selectedStatsFilter) {
                            GranularStatFilter.BATTERS -> {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SurfaceDark)
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "INNINGS STRIKE RATE",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextTertiary,
                                                letterSpacing = 0.5.sp
                                            )
                                            val completedOvers = activeInnings.overs.toInt()
                                            val ballsPart = ((activeInnings.overs - completedOvers) * 10).toInt()
                                            val totalBalls = (completedOvers * 6) + ballsPart
                                            val teamSR = if (totalBalls > 0) (activeInnings.totalRuns.toFloat() / totalBalls) * 100f else 0f
                                            Text(
                                                text = "${String.format(Locale.US, "%.1f", teamSR)} SR",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Black,
                                                color = CricketNeonGreen
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            val totalBoundaryRuns = activeInnings.batters.sumOf { it.boundaryRuns }
                                            val totalBoundaryPct = if (activeInnings.totalRuns > 0) (totalBoundaryRuns.toFloat() / activeInnings.totalRuns) * 100f else 0f
                                            Text(
                                                text = "BOUNDARY PERCENTAGE",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextTertiary,
                                                letterSpacing = 0.5.sp
                                            )
                                            Text(
                                                text = "${String.format(Locale.US, "%.1f", totalBoundaryPct)}% of runs",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = FourBlue
                                            )
                                        }
                                    }
                                }

                                items(activeInnings.batters) { batter ->
                                    GranularBatterCard(batter = batter)
                                }
                            }

                            GranularStatFilter.BOWLERS -> {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SurfaceDark)
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "BOWLING TEAM ECONOMY",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextTertiary,
                                                letterSpacing = 0.5.sp
                                            )
                                            Text(
                                                text = "${String.format(Locale.US, "%.2f", activeInnings.runRate)} RPO",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Black,
                                                color = CricketGold
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            val totalDots = activeInnings.bowlers.sumOf { it.dotBalls }
                                            val totalBalls = activeInnings.bowlers.sumOf { it.totalBalls }
                                            val dotPct = if (totalBalls > 0) (totalDots.toFloat() / totalBalls) * 100f else 0f
                                            Text(
                                                text = "DOT BALL CONTROL",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextTertiary,
                                                letterSpacing = 0.5.sp
                                            )
                                            Text(
                                                text = "$totalDots dots (${String.format(Locale.US, "%.0f", dotPct)}%)",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CricketNeonGreen
                                            )
                                        }
                                    }
                                }

                                items(activeInnings.bowlers) { bowler ->
                                    GranularBowlerCard(bowler = bowler)
                                }
                            }

                            GranularStatFilter.PHASES -> {
                                item {
                                    PhaseBreakdownCard(summary = activeInnings.phaseSummary)
                                }
                            }
                        }
                    }
                }

                MatchDetailTab.PARTNERSHIP -> {
                    // API Status Header
                    item {
                        if (granularStats != null) {
                            ApiFeedStatusHeader(
                                sourceApi = granularStats.sourceApi,
                                latencyMs = granularStats.latencyMs,
                                lastUpdated = granularStats.lastUpdated,
                                isLoading = isLoadingStats,
                                onRefresh = onRefreshStats
                            )
                        }
                    }

                    val partnerships = if (granularStats != null && granularStats.inningsList.isNotEmpty()) {
                        val currentInningsIdx = (selectedPartnershipInnings - 1).coerceIn(0, granularStats.inningsList.size - 1)
                        granularStats.inningsList[currentInningsIdx].partnerships
                    } else emptyList()

                    if (granularStats != null && granularStats.inningsList.size > 1) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceDark)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                granularStats.inningsList.forEachIndexed { index, inn ->
                                    val isSel = (index + 1) == selectedPartnershipInnings
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) CricketNeonGreen else Color.Transparent)
                                            .clickable { selectedPartnershipInnings = index + 1 }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${inn.teamName} Stands",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSel) BackgroundDark else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (partnerships.isNotEmpty()) {
                        item {
                            val maxStand = partnerships.maxByOrNull { it.totalRuns }
                            val totalRunsInStands = partnerships.sumOf { it.totalRuns }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceDark)
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "HIGHEST PARTNERSHIP",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextTertiary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "${maxStand?.totalRuns ?: 0} runs (${maxStand?.totalBalls ?: 0}b)",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = CricketGold
                                    )
                                    Text(
                                        text = "${maxStand?.batter1Name} & ${maxStand?.batter2Name}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "TOTAL STANDS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextTertiary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "${partnerships.size} Wickets",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Avg: ${if (partnerships.isNotEmpty()) totalRunsInStands / partnerships.size else 0} r/stand",
                                        fontSize = 11.sp,
                                        color = TextTertiary
                                    )
                                }
                            }
                        }

                        items(partnerships) { part ->
                            DetailedPartnershipBreakdownCard(partnership = part)
                        }
                    } else if (match.currentPartnership != null) {
                        item {
                            PartnershipCard(partnership = match.currentPartnership)
                        }
                    }

                    // Fall of Wickets
                    item {
                        FallOfWicketsList(fowList = match.fallOfWickets)
                    }
                }

                MatchDetailTab.INFO -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceDark)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "MATCH DETAILS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextTertiary,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            DetailInfoRow("Tournament", match.tournamentName)
                            DetailInfoRow("Match", match.matchNumberDesc)
                            DetailInfoRow("Format", "${match.format} (${match.maxOvers} Overs per side)")
                            DetailInfoRow("Venue", match.venue)
                            DetailInfoRow("City", match.city)
                            DetailInfoRow("Toss", match.tossResult)
                            match.officials?.let { off ->
                                DetailInfoRow("Umpires", off.umpires)
                                DetailInfoRow("Third Umpire", off.thirdUmpire)
                                DetailInfoRow("Match Referee", off.matchReferee)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextTertiary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
private fun PhaseBreakdownCard(
    summary: MatchPhaseSummary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Text(
            text = "MATCH PHASE ANALYSIS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextTertiary,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        listOf(summary.powerplay, summary.middleOvers, summary.deathOvers).forEach { phase ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariantDark)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${phase.name} (Overs ${phase.oversRange})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "RR: ${String.format(Locale.US, "%.2f", phase.runRate)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = CricketNeonGreen
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Score: ${phase.runs}/${phase.wickets} in ${phase.balls} balls",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = if (phase.balls > 0) "${(phase.runs.toFloat() / phase.balls * 100).toInt()} SR" else "—",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CricketGold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
