package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TournamentPointsTable
import com.example.ui.components.TeamLogoBadge
import com.example.ui.theme.*

@Composable
fun PointsTableScreen(
    pointsTables: List<TournamentPointsTable>,
    modifier: Modifier = Modifier
) {
    var selectedTableIndex by remember { mutableIntStateOf(0) }
    val currentTable = pointsTables.getOrNull(selectedTableIndex)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tournament Selector
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pointsTables.indices.toList()) { index ->
                    val isSelected = selectedTableIndex == index
                    val table = pointsTables[index]
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) CricketNeonGreen else SurfaceDark)
                            .border(
                                1.dp,
                                if (isSelected) CricketNeonGreen else CardBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedTableIndex = index }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = table.tournamentName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                            color = if (isSelected) BackgroundDark else TextSecondary
                        )
                    }
                }
            }
        }

        if (currentTable != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceDark)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = currentTable.groupName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CricketGold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#",
                            modifier = Modifier.width(24.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary
                        )
                        Text(
                            text = "TEAM",
                            modifier = Modifier.weight(1.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary
                        )
                        Text(
                            text = "P",
                            modifier = Modifier.weight(0.5f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "W",
                            modifier = Modifier.weight(0.5f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "L",
                            modifier = Modifier.weight(0.5f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "PTS",
                            modifier = Modifier.weight(0.7f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CricketNeonGreen,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "NRR",
                            modifier = Modifier.weight(0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary,
                            textAlign = TextAlign.End
                        )
                    }

                    HorizontalDivider(color = CardBorder.copy(alpha = 0.5f), thickness = 1.dp)

                    currentTable.entries.forEach { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${entry.rank}",
                                modifier = Modifier.width(24.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (entry.rank <= 2) CricketNeonGreen else TextSecondary
                            )

                            Row(
                                modifier = Modifier.weight(1.8f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TeamLogoBadge(team = entry.team, size = 26.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = entry.team.shortName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Text(
                                text = "${entry.played}",
                                modifier = Modifier.weight(0.5f),
                                fontSize = 12.sp,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "${entry.won}",
                                modifier = Modifier.weight(0.5f),
                                fontSize = 12.sp,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "${entry.lost}",
                                modifier = Modifier.weight(0.5f),
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "${entry.points}",
                                modifier = Modifier.weight(0.7f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = CricketNeonGreen,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = entry.netRunRate,
                                modifier = Modifier.weight(0.9f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (entry.netRunRate.startsWith("+")) CricketNeonGreen else LiveRed,
                                textAlign = TextAlign.End
                            )
                        }

                        // Recent Form circles
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, bottom = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Form:",
                                fontSize = 10.sp,
                                color = TextTertiary
                            )
                            entry.recentForm.forEach { res ->
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(if (res == "W") CricketNeonGreen else LiveRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = res,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BackgroundDark
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = CardBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}
