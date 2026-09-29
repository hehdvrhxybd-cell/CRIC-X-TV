package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BatsmanStats
import com.example.data.model.BowlerStats
import com.example.data.model.FallOfWicket
import com.example.data.model.Partnership
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun LiveBattersTable(
    batters: List<BatsmanStats>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .padding(12.dp)
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BATSMAN",
                modifier = Modifier.weight(1.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary
            )
            Text(
                text = "R",
                modifier = Modifier.weight(0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                textAlign = TextAlign.End
            )
            Text(
                text = "B",
                modifier = Modifier.weight(0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                textAlign = TextAlign.End
            )
            Text(
                text = "4s",
                modifier = Modifier.weight(0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                textAlign = TextAlign.End
            )
            Text(
                text = "6s",
                modifier = Modifier.weight(0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                textAlign = TextAlign.End
            )
            Text(
                text = "SR",
                modifier = Modifier.weight(0.9f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                textAlign = TextAlign.End
            )
        }

        HorizontalDivider(color = CardBorder.copy(alpha = 0.5f), thickness = 1.dp)

        batters.forEach { batter ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1.8f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = batter.name,
                        fontSize = 13.sp,
                        fontWeight = if (batter.isStriker) FontWeight.Bold else FontWeight.Medium,
                        color = if (batter.isStriker) CricketNeonGreen else TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (batter.isStriker) {
                        Text(
                            text = " *",
                            color = CricketNeonGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }

                Text(
                    text = "${batter.runs}",
                    modifier = Modifier.weight(0.6f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.End
                )
                Text(
                    text = "${batter.balls}",
                    modifier = Modifier.weight(0.6f),
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.End
                )
                Text(
                    text = "${batter.fours}",
                    modifier = Modifier.weight(0.6f),
                    fontSize = 13.sp,
                    color = FourBlue,
                    textAlign = TextAlign.End
                )
                Text(
                    text = "${batter.sixes}",
                    modifier = Modifier.weight(0.6f),
                    fontSize = 13.sp,
                    color = SixPurple,
                    textAlign = TextAlign.End
                )
                Text(
                    text = String.format(Locale.US, "%.1f", batter.strikeRate),
                    modifier = Modifier.weight(0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun LiveBowlerTable(
    bowler: BowlerStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .padding(12.dp)
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BOWLER",
                modifier = Modifier.weight(1.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary
            )
            Text(
                text = "O",
                modifier = Modifier.weight(0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                textAlign = TextAlign.End
            )
            Text(
                text = "M",
                modifier = Modifier.weight(0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                textAlign = TextAlign.End
            )
            Text(
                text = "R",
                modifier = Modifier.weight(0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                textAlign = TextAlign.End
            )
            Text(
                text = "W",
                modifier = Modifier.weight(0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                textAlign = TextAlign.End
            )
            Text(
                text = "ECON",
                modifier = Modifier.weight(0.9f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                textAlign = TextAlign.End
            )
        }

        HorizontalDivider(color = CardBorder.copy(alpha = 0.5f), thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1.8f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bowler.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CricketGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = " *",
                    color = CricketGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }

            Text(
                text = bowler.formattedOvers(),
                modifier = Modifier.weight(0.6f),
                fontSize = 13.sp,
                color = TextPrimary,
                textAlign = TextAlign.End
            )
            Text(
                text = "${bowler.maidens}",
                modifier = Modifier.weight(0.5f),
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.End
            )
            Text(
                text = "${bowler.runs}",
                modifier = Modifier.weight(0.6f),
                fontSize = 13.sp,
                color = TextPrimary,
                textAlign = TextAlign.End
            )
            Text(
                text = "${bowler.wickets}",
                modifier = Modifier.weight(0.6f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = LiveRed,
                textAlign = TextAlign.End
            )
            Text(
                text = String.format(Locale.US, "%.2f", bowler.economy),
                modifier = Modifier.weight(0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun PartnershipCard(
    partnership: Partnership,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CURRENT PARTNERSHIP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "${partnership.runs} (${partnership.balls}b) • RR: ${String.format(Locale.US, "%.2f", partnership.runRate)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = CricketNeonGreen
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${partnership.batter1Name} (${partnership.batter1Runs})",
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${partnership.batter2Name} (${partnership.batter2Runs})",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FallOfWicketsList(
    fowList: List<FallOfWicket>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .padding(12.dp)
    ) {
        Text(
            text = "FALL OF WICKETS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextTertiary,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        HorizontalDivider(color = CardBorder.copy(alpha = 0.5f), thickness = 1.dp)

        if (fowList.isEmpty()) {
            Text(
                text = "No wickets fallen yet",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            fowList.forEach { fow ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${fow.score}/${fow.wicketNumber} (${fow.batterName}, ${fow.over} ov)",
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
