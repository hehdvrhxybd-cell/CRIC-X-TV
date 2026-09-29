package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CricketMatch
import com.example.data.model.MatchFormat
import com.example.data.model.MatchReminder
import com.example.data.model.MatchStatus
import com.example.ui.components.MatchScoreCard
import com.example.ui.theme.*

@Composable
fun MatchesTabScreen(
    matches: List<CricketMatch>,
    matchReminders: Map<String, MatchReminder>,
    onMatchClick: (CricketMatch) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onReminderClick: (CricketMatch) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFormat by remember { mutableStateOf("All") }
    val formats = listOf("All", "T20", "ODI", "TEST")

    val filteredMatches = remember(matches, selectedFormat) {
        if (selectedFormat == "All") matches
        else matches.filter { it.format.name.equals(selectedFormat, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Format Filter
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(formats) { format ->
                    val isSelected = selectedFormat == format
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) CricketNeonGreen else SurfaceDark)
                            .border(
                                1.dp,
                                if (isSelected) CricketNeonGreen else CardBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedFormat = format }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = format,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                            color = if (isSelected) BackgroundDark else TextSecondary
                        )
                    }
                }
            }
        }

        // Live Matches
        val liveList = filteredMatches.filter { it.status == MatchStatus.LIVE || it.status == MatchStatus.INNINGS_BREAK }
        if (liveList.isNotEmpty()) {
            item {
                Text(
                    text = "Live Now",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LiveRed
                )
            }
            items(liveList, key = { "live_" + it.id }) { match ->
                MatchScoreCard(match = match, onMatchClick = onMatchClick, onToggleFavorite = onToggleFavorite)
            }
        }

        // Upcoming Matches
        val upList = filteredMatches.filter { it.status == MatchStatus.UPCOMING }
        if (upList.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Upcoming Matches",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = CricketNeonGreen
                )
            }
            items(upList, key = { "up_" + it.id }) { match ->
                MatchScoreCard(
                    match = match,
                    onMatchClick = onMatchClick,
                    onToggleFavorite = onToggleFavorite,
                    reminder = matchReminders[match.id],
                    onReminderClick = onReminderClick
                )
            }
        }

        // Completed Matches
        val compList = filteredMatches.filter { it.status == MatchStatus.COMPLETED }
        if (compList.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Recent Results",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = CricketGold
                )
            }
            items(compList, key = { "comp_" + it.id }) { match ->
                MatchScoreCard(match = match, onMatchClick = onMatchClick, onToggleFavorite = onToggleFavorite)
            }
        }
    }
}
