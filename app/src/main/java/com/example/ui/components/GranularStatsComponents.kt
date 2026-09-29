package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun ApiFeedStatusHeader(
    sourceApi: String,
    latencyMs: Long,
    lastUpdated: String,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin_refresh")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(SurfaceVariantDark, SurfaceDark)
                )
            )
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(CricketNeonGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "API Status",
                        tint = CricketNeonGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LIVE API FEED",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = CricketNeonGreen,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CricketGold.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${latencyMs}ms",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CricketGold
                            )
                        }
                    }
                    Text(
                        text = "$sourceApi • Last updated: $lastUpdated",
                        fontSize = 11.sp,
                        color = TextTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = onRefresh,
                enabled = !isLoading,
                modifier = Modifier.testTag("refresh_api_stats_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh API Data",
                    tint = if (isLoading) CricketGold else TextPrimary,
                    modifier = Modifier
                        .size(22.dp)
                        .then(if (isLoading) Modifier.rotate(rotation) else Modifier)
                )
            }
        }
    }
}

@Composable
fun GranularBatterCard(
    batter: GranularBatterStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(
                1.dp,
                if (batter.isStriker) CricketNeonGreen.copy(alpha = 0.5f) else CardBorder,
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        // Top row: Player name, dismissal, and Overall Strike Rate badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = batter.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (batter.isStriker) CricketNeonGreen else TextPrimary
                    )
                    if (batter.isStriker) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CricketNeonGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ON STRIKE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = CricketNeonGreen
                            )
                        }
                    }
                }
                Text(
                    text = batter.dismissalText,
                    fontSize = 11.sp,
                    color = TextTertiary
                )
            }

            // Runs (Balls) & SR Badge
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${batter.runs}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = " (${batter.balls})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                val sr = batter.overallStrikeRate
                val srBadgeColor = when {
                    sr >= 170f -> CricketNeonGreen
                    sr >= 130f -> CricketGold
                    else -> FourBlue
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(srBadgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SR ${String.format(Locale.US, "%.1f", sr)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = srBadgeColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = CardBorder.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(10.dp))

        // Phase Breakdown Bars: Powerplay, Middle, Death
        Text(
            text = "STRIKE RATE BY MATCH PHASE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextTertiary,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PhaseMetricBox(
                phaseTitle = "Powerplay (1-6)",
                runs = batter.powerplayRuns,
                balls = batter.powerplayBalls,
                strikeRate = batter.powerplayStrikeRate,
                modifier = Modifier.weight(1f)
            )
            PhaseMetricBox(
                phaseTitle = "Middle (7-15)",
                runs = batter.middleRuns,
                balls = batter.middleBalls,
                strikeRate = batter.middleStrikeRate,
                modifier = Modifier.weight(1f)
            )
            PhaseMetricBox(
                phaseTitle = "Death (16-20)",
                runs = batter.deathRuns,
                balls = batter.deathBalls,
                strikeRate = batter.deathStrikeRate,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bowling Type Matchup: Pace vs Spin
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceVariantDark.copy(alpha = 0.6f))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "vs PACE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TextTertiary
                )
                Text(
                    text = "${batter.runsVsPace} runs (${batter.ballsVsPace}b)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = "SR: ${String.format(Locale.US, "%.1f", batter.strikeRateVsPace)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = CricketGold
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(34.dp)
                    .background(CardBorder)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = "vs SPIN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TextTertiary
                )
                Text(
                    text = "${batter.runsVsSpin} runs (${batter.ballsVsSpin}b)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = "SR: ${String.format(Locale.US, "%.1f", batter.strikeRateVsSpin)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = FourBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Boundaries & Dots Distribution
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Boundary Runs: ${batter.boundaryRuns} (${String.format(Locale.US, "%.0f", batter.boundaryRunPercent)}%) • 4s: ${batter.fours}, 6s: ${batter.sixes}",
                fontSize = 11.sp,
                color = TextSecondary
            )
            Text(
                text = "Dots: ${batter.dotBalls} (${String.format(Locale.US, "%.0f", batter.dotBallPercent)}%)",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = LiveRed.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun PhaseMetricBox(
    phaseTitle: String,
    runs: Int,
    balls: Int,
    strikeRate: Float?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceVariantDark)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = phaseTitle,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (balls > 0 && strikeRate != null) {
            Text(
                text = "$runs ($balls)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "${String.format(Locale.US, "%.0f", strikeRate)} SR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (strikeRate >= 150f) CricketNeonGreen else CricketGold
            )
        } else {
            Text(
                text = "—",
                fontSize = 13.sp,
                color = TextTertiary
            )
            Text(
                text = "Did not bat",
                fontSize = 9.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
fun GranularBowlerCard(
    bowler: GranularBowlerStats,
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
        // Top row: Name, figures, Economy badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bowler.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Text(
                    text = "${bowler.overs} Overs • ${bowler.maidens} Maiden",
                    fontSize = 11.sp,
                    color = TextTertiary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${bowler.wickets}/${bowler.runsConceded}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (bowler.wickets > 0) LiveRed else TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                val econ = bowler.overallEconomy
                val econColor = when {
                    econ <= 6.5f -> CricketNeonGreen
                    econ <= 8.5f -> CricketGold
                    else -> LiveRed
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(econColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ECON ${String.format(Locale.US, "%.2f", econ)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = econColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = CardBorder.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(10.dp))

        // Phase Breakdown
        Text(
            text = "ECONOMY BY MATCH PHASE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextTertiary,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BowlerPhaseBox(
                phaseTitle = "Powerplay (1-6)",
                overs = bowler.powerplayOvers,
                runs = bowler.powerplayRuns,
                wickets = bowler.powerplayWickets,
                economy = bowler.powerplayEconomy,
                modifier = Modifier.weight(1f)
            )
            BowlerPhaseBox(
                phaseTitle = "Middle (7-15)",
                overs = bowler.middleOvers,
                runs = bowler.middleRuns,
                wickets = bowler.middleWickets,
                economy = bowler.middleEconomy,
                modifier = Modifier.weight(1f)
            )
            BowlerPhaseBox(
                phaseTitle = "Death (16-20)",
                overs = bowler.deathOvers,
                runs = bowler.deathRuns,
                wickets = bowler.deathWickets,
                economy = bowler.deathEconomy,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Over by over analysis breakdown pills
        if (bowler.overDetails.isNotEmpty()) {
            Text(
                text = "OVER-BY-OVER PROGRESSION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                bowler.overDetails.forEach { ov ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceVariantDark)
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ov ${ov.overNumber}",
                            fontSize = 10.sp,
                            color = TextTertiary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${ov.runsConceded}r" + (if (ov.wicketsTaken > 0) " • ${ov.wicketsTaken}w" else ""),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ov.wicketsTaken > 0) LiveRed else TextPrimary
                        )
                        Text(
                            text = "${ov.dotBalls} dots",
                            fontSize = 9.sp,
                            color = CricketGold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Dot ball & Boundary control summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dot Balls: ${bowler.dotBalls} of ${bowler.totalBalls} (${String.format(Locale.US, "%.0f", bowler.dotBallPercent)}%)",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = CricketNeonGreen
            )
            Text(
                text = "Boundaries: ${bowler.foursConceded}x4 • ${bowler.sixesConceded}x6",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun BowlerPhaseBox(
    phaseTitle: String,
    overs: Float,
    runs: Int,
    wickets: Int,
    economy: Float?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceVariantDark)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = phaseTitle,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (overs > 0f && economy != null) {
            Text(
                text = "${wickets}w - ${runs}r (${overs} ov)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "${String.format(Locale.US, "%.1f", economy)} econ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (economy <= 7.0f) CricketNeonGreen else CricketGold
            )
        } else {
            Text(
                text = "—",
                fontSize = 13.sp,
                color = TextTertiary
            )
            Text(
                text = "Did not bowl",
                fontSize = 9.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
fun DetailedPartnershipBreakdownCard(
    partnership: DetailedPartnership,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(
                1.dp,
                if (partnership.isCurrentStand) CricketNeonGreen.copy(alpha = 0.5f) else CardBorder,
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${partnership.wicketNumber}${getOrdinalSuffix(partnership.wicketNumber)} Wicket Stand",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (partnership.isCurrentStand) CricketNeonGreen else TextPrimary
                )
                if (partnership.isCurrentStand) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(LiveRed.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = LiveRed
                        )
                    }
                }
            }

            Text(
                text = "${partnership.totalRuns} runs (${partnership.totalBalls} balls)",
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = CricketGold
            )
        }

        if (partnership.oversSpan.isNotEmpty()) {
            Text(
                text = "${partnership.oversSpan} • Run Rate: ${String.format(Locale.US, "%.2f", partnership.runRate)}",
                fontSize = 11.sp,
                color = TextTertiary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Split Bar representing contribution
        val share1 = (partnership.batter1SharePercent / 100f).coerceIn(0.05f, 0.95f)
        val share2 = 1f - share1

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(SurfaceVariantDark)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(share1)
                    .background(CricketNeonGreen)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(share2)
                    .background(FourBlue)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Side-by-side comparative player contribution
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Batter 1
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CricketNeonGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = partnership.batter1Name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${partnership.batter1Runs} runs in ${partnership.batter1Balls} balls",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = CricketNeonGreen
                )
                Text(
                    text = "SR: ${String.format(Locale.US, "%.1f", partnership.batter1StrikeRate)} • ${partnership.batter1Fours}x4, ${partnership.batter1Sixes}x6",
                    fontSize = 11.sp,
                    color = TextTertiary
                )
                Text(
                    text = "${String.format(Locale.US, "%.0f", partnership.batter1SharePercent)}% of stand",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Batter 2
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = partnership.batter2Name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(FourBlue)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${partnership.batter2Runs} runs in ${partnership.batter2Balls} balls",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = FourBlue
                )
                Text(
                    text = "SR: ${String.format(Locale.US, "%.1f", partnership.batter2StrikeRate)} • ${partnership.batter2Fours}x4, ${partnership.batter2Sixes}x6",
                    fontSize = 11.sp,
                    color = TextTertiary
                )
                Text(
                    text = "${String.format(Locale.US, "%.0f", partnership.batter2SharePercent)}% of stand",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
        }

        if (partnership.extras > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "+ ${partnership.extras} extras conceded in partnership",
                fontSize = 10.sp,
                color = TextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun getOrdinalSuffix(number: Int): String {
    return when {
        number in 11..13 -> "th"
        number % 10 == 1 -> "st"
        number % 10 == 2 -> "nd"
        number % 10 == 3 -> "rd"
        else -> "th"
    }
}
