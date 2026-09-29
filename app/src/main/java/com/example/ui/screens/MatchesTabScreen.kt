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
import com.example.ui.components.MatchCard
import com.example.ui.theme.*

enum class MatchCategoryFilter(val title: String) {
    ALL("All"),
    LIVE("Live"),
    UPCOMING("Upcoming"),
    COMPLETED("Completed")
}

@Composable
fun MatchesTabScreen(
    matches: List<CricketMatch>,
    matchReminders: Map<String, MatchReminder>,
    onMatchClick: (CricketMatch) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onReminderClick: (CricketMatch) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(MatchCategoryFilter.ALL) }
    var selectedFormat by remember { mutableStateOf("All") }
    val formats = listOf("All", "T20", "ODI", "TEST")

    val formatFilteredMatches = remember(matches, selectedFormat) {
        if (selectedFormat == "All") matches
        else matches.filter { it.format.name.equals(selectedFormat, ignoreCase = true) }
    }

    val liveCount = remember(formatFilteredMatches) {
        formatFilteredMatches.count { it.status == MatchStatus.LIVE || it.status == MatchStatus.INNINGS_BREAK }
    }
    val upcomingCount = remember(formatFilteredMatches) {
        formatFilteredMatches.count { it.status == MatchStatus.UPCOMING }
    }
    val completedCount = remember(formatFilteredMatches) {
        formatFilteredMatches.count { it.status == MatchStatus.COMPLETED }
    }

    val displayedMatches = remember(formatFilteredMatches, selectedCategory) {
        when (selectedCategory) {
            MatchCategoryFilter.ALL -> formatFilteredMatches
            MatchCategoryFilter.LIVE -> formatFilteredMatches.filter { it.status == MatchStatus.LIVE || it.status == MatchStatus.INNINGS_BREAK }
            MatchCategoryFilter.UPCOMING -> formatFilteredMatches.filter { it.status == MatchStatus.UPCOMING }
            MatchCategoryFilter.COMPLETED -> formatFilteredMatches.filter { it.status == MatchStatus.COMPLETED }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Status Filter Row: All | Live | Upcoming | Completed
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MatchCategoryFilter.values().forEach { cat ->
                    val isSelected = selectedCategory == cat
                    val count = when (cat) {
                        MatchCategoryFilter.ALL -> formatFilteredMatches.size
                        MatchCategoryFilter.LIVE -> liveCount
                        MatchCategoryFilter.UPCOMING -> upcomingCount
                        MatchCategoryFilter.COMPLETED -> completedCount
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CricketNeonGreen else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${cat.title} ($count)",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                            color = if (isSelected) BackgroundDark else TextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Format Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(formats) { format ->
                    val isSelected = selectedFormat == format
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) CricketGold.copy(alpha = 0.2f) else SurfaceDark)
                            .border(
                                1.dp,
                                if (isSelected) CricketGold else CardBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedFormat = format }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = format,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) CricketGold else TextSecondary
                        )
                    }
                }
            }
        }

        // Display Matches
        if (displayedMatches.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matches found for ${selectedCategory.title} ($selectedFormat)",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else if (selectedCategory != MatchCategoryFilter.ALL) {
            // Filtered single category
            item {
                Text(
                    text = "${selectedCategory.title.uppercase()} MATCHES (${displayedMatches.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = when (selectedCategory) {
                        MatchCategoryFilter.LIVE -> LiveRed
                        MatchCategoryFilter.UPCOMING -> CricketNeonGreen
                        MatchCategoryFilter.COMPLETED -> CricketGold
                        else -> TextPrimary
                    },
                    letterSpacing = 1.sp
                )
            }
            items(displayedMatches, key = { it.id }) { match ->
                MatchCard(
                    match = match,
                    onMatchClick = onMatchClick,
                    onToggleFavorite = onToggleFavorite,
                    reminder = matchReminders[match.id],
                    onReminderClick = onReminderClick
                )
            }
        } else {
            // ALL View: Grouped by Live, Upcoming, Completed
            val liveList = displayedMatches.filter { it.status == MatchStatus.LIVE || it.status == MatchStatus.INNINGS_BREAK }
            if (liveList.isNotEmpty()) {
                item {
                    Text(
                        text = "LIVE NOW (${liveList.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = LiveRed,
                        letterSpacing = 1.sp
                    )
                }
                items(liveList, key = { "live_" + it.id }) { match ->
                    MatchCard(
                        match = match,
                        onMatchClick = onMatchClick,
                        onToggleFavorite = onToggleFavorite
                    )
                }
            }

            val upList = displayedMatches.filter { it.status == MatchStatus.UPCOMING }
            if (upList.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "UPCOMING FIXTURES (${upList.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = CricketNeonGreen,
                        letterSpacing = 1.sp
                    )
                }
                items(upList, key = { "up_" + it.id }) { match ->
                    MatchCard(
                        match = match,
                        onMatchClick = onMatchClick,
                        onToggleFavorite = onToggleFavorite,
                        reminder = matchReminders[match.id],
                        onReminderClick = onReminderClick
                    )
                }
            }

            val compList = displayedMatches.filter { it.status == MatchStatus.COMPLETED }
            if (compList.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "RECENT RESULTS (${compList.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = CricketGold,
                        letterSpacing = 1.sp
                    )
                }
                items(compList, key = { "comp_" + it.id }) { match ->
                    MatchCard(
                        match = match,
                        onMatchClick = onMatchClick,
                        onToggleFavorite = onToggleFavorite
                    )
                }
            }
        }
    }
}
