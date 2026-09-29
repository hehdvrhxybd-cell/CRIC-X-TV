package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CricketPlayer
import com.example.data.model.PlayerRole
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun PlayersScreen(
    players: List<CricketPlayer>,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Players") }
    var selectedPlayerForDialog by remember { mutableStateOf<CricketPlayer?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tabs: Players | Leaderboards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceDark)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Players", "Leaderboards").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CricketNeonGreen else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                            color = if (isSelected) BackgroundDark else TextSecondary
                        )
                    }
                }
            }
        }

        if (selectedTab == "Players") {
            items(players, key = { it.id }) { player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceDark)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .clickable { selectedPlayerForDialog = player }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar / Jersey
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SurfaceVariantDark)
                            .border(1.dp, CricketGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${player.jerseyNumber}",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = CricketGold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = player.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            if (player.isCaptain) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(C)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CricketGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${player.teamName} • ${player.role.name.replace("_", " ")}",
                            fontSize = 12.sp,
                            color = CricketNeonGreen
                        )
                        Text(
                            text = "T20 Runs: ${player.t20StatsBatting.runs} (Avg: ${player.t20StatsBatting.average}, SR: ${player.t20StatsBatting.strikeRate})",
                            fontSize = 11.sp,
                            color = TextTertiary
                        )
                    }
                }
            }
        } else {
            // Leaderboards
            item {
                Text(
                    text = "Most Runs (T20 Internationals)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = CricketGold
                )
            }

            val topBatters = players.sortedByDescending { it.t20StatsBatting.runs }
            items(topBatters.take(5), key = { "top_bat_" + it.id }) { player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .clickable { selectedPlayerForDialog = player }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "#${topBatters.indexOf(player) + 1}",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = CricketNeonGreen,
                            modifier = Modifier.width(28.dp)
                        )
                        Column {
                            Text(
                                text = player.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${player.teamShort} • Innings: ${player.t20StatsBatting.innings}",
                                fontSize = 11.sp,
                                color = TextTertiary
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${player.t20StatsBatting.runs} Runs",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = CricketGold
                        )
                        Text(
                            text = "Avg: ${player.t20StatsBatting.average} | SR: ${player.t20StatsBatting.strikeRate}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }

    // Player Details Dialog
    selectedPlayerForDialog?.let { player ->
        Dialog(onDismissRequest = { selectedPlayerForDialog = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = player.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "${player.teamName} • #${player.jerseyNumber}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = CricketNeonGreen
                        )
                    }
                    IconButton(onClick = { selectedPlayerForDialog = null }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Role: ${player.role.name.replace("_", " ")}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Batting: ${player.battingStyle} | Bowling: ${player.bowlingStyle}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = CardBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "T20 CAREER STATISTICS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CricketGold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBox("Matches", "${player.t20StatsBatting.matches}")
                    StatBox("Runs", "${player.t20StatsBatting.runs}")
                    StatBox("Average", "${player.t20StatsBatting.average}")
                    StatBox("Strike Rate", "${player.t20StatsBatting.strikeRate}")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBox("High Score", player.t20StatsBatting.highestScore)
                    StatBox("100s", "${player.t20StatsBatting.hundreds}")
                    StatBox("50s", "${player.t20StatsBatting.fifties}")
                    StatBox("Sixes", "${player.t20StatsBatting.sixes}")
                }

                if (player.t20StatsBowling != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "BOWLING FIGURES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricketNeonGreen,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatBox("Wickets", "${player.t20StatsBowling.wickets}")
                        StatBox("Economy", "${player.t20StatsBowling.economy}")
                        StatBox("Best", player.t20StatsBowling.bestBowling)
                        StatBox("Average", "${player.t20StatsBowling.average}")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceVariantDark)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = TextPrimary
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextTertiary
        )
    }
}
