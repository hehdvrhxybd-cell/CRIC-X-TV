package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CricketMatch
import com.example.data.model.MatchReminder
import com.example.data.model.MatchStatus
import com.example.ui.components.MatchScoreCard
import com.example.ui.theme.*

enum class HomeTab(val title: String) {
    LIVE("LIVE"),
    UPCOMING("UPCOMING"),
    COMPLETED("COMPLETED"),
    ALL("ALL")
}

@Composable
fun HomeScreen(
    matches: List<CricketMatch>,
    favoriteTeamIds: Set<String>,
    matchReminders: Map<String, MatchReminder>,
    onMatchClick: (CricketMatch) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onReminderClick: (CricketMatch) -> Unit,
    onNavigateToSeries: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(HomeTab.LIVE) }
    var selectedSeriesFilter by remember { mutableStateOf("All") }
    val focusManager = LocalFocusManager.current

    val quickFilters = listOf("India", "Australia", "England", "T20 World Cup", "IPL 2026", "The Ashes")
    val seriesList = listOf("All", "T20 World Cup", "IPL 2026", "The Ashes")

    val normalizedQuery = searchQuery.trim().lowercase()

    // Helper to evaluate if a match fulfills the search query (team name/code or series/tournament title)
    fun matchesSearch(match: CricketMatch): Boolean {
        if (normalizedQuery.isEmpty()) return true
        return match.team1.name.lowercase().contains(normalizedQuery) ||
                match.team1.shortName.lowercase().contains(normalizedQuery) ||
                match.team2.name.lowercase().contains(normalizedQuery) ||
                match.team2.shortName.lowercase().contains(normalizedQuery) ||
                match.tournamentName.lowercase().contains(normalizedQuery) ||
                match.city.lowercase().contains(normalizedQuery) ||
                match.venue.lowercase().contains(normalizedQuery)
    }

    // Helper to evaluate series filter
    fun matchesSeries(match: CricketMatch): Boolean {
        return if (selectedSeriesFilter == "All") true
        else match.tournamentName.contains(selectedSeriesFilter, ignoreCase = true)
    }

    // Dynamic counts per tab factoring search and series filter
    val liveCount = remember(matches, normalizedQuery, selectedSeriesFilter) {
        matches.count {
            (it.status == MatchStatus.LIVE || it.status == MatchStatus.INNINGS_BREAK) &&
                    matchesSearch(it) && matchesSeries(it)
        }
    }
    val upcomingCount = remember(matches, normalizedQuery, selectedSeriesFilter) {
        matches.count { it.status == MatchStatus.UPCOMING && matchesSearch(it) && matchesSeries(it) }
    }
    val completedCount = remember(matches, normalizedQuery, selectedSeriesFilter) {
        matches.count { it.status == MatchStatus.COMPLETED && matchesSearch(it) && matchesSeries(it) }
    }
    val allCount = remember(matches, normalizedQuery, selectedSeriesFilter) {
        matches.count { matchesSearch(it) && matchesSeries(it) }
    }

    val filteredMatches = remember(matches, selectedTab, selectedSeriesFilter, normalizedQuery) {
        matches.filter { match ->
            val statusMatch = when (selectedTab) {
                HomeTab.ALL -> true
                HomeTab.LIVE -> match.status == MatchStatus.LIVE || match.status == MatchStatus.INNINGS_BREAK
                HomeTab.UPCOMING -> match.status == MatchStatus.UPCOMING
                HomeTab.COMPLETED -> match.status == MatchStatus.COMPLETED
            }
            statusMatch && matchesSeries(match) && matchesSearch(match)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Search Bar for instant filtering by Team Name or Series Title
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_bar"),
                    placeholder = {
                        Text(
                            text = "Filter matches by team or series...",
                            color = TextTertiary,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = CricketNeonGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                    focusManager.clearFocus()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("home_search_clear_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedBorderColor = CricketNeonGreen,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = CricketNeonGreen
                    )
                )

                // Quick Team / Series filter chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(quickFilters) { filter ->
                        val isFilterActive = searchQuery.equals(filter, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isFilterActive) CricketNeonGreen.copy(alpha = 0.2f) else SurfaceContainerHighest)
                                .border(
                                    1.dp,
                                    if (isFilterActive) CricketNeonGreen else Color.Transparent,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    if (isFilterActive) {
                                        searchQuery = ""
                                    } else {
                                        searchQuery = filter
                                    }
                                    focusManager.clearFocus()
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("search_filter_chip_$filter")
                        ) {
                            Text(
                                text = filter,
                                fontSize = 11.sp,
                                fontWeight = if (isFilterActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isFilterActive) CricketNeonGreen else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Active search filter status indicator
        if (searchQuery.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = CricketGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Filtering by: \"$searchQuery\"",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CricketGold
                        )
                    }

                    Text(
                        text = "Reset",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricketNeonGreen,
                        modifier = Modifier
                            .clickable {
                                searchQuery = ""
                                selectedSeriesFilter = "All"
                            }
                            .padding(4.dp)
                    )
                }
            }
        }

        // Hero Stadium Broadcast Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cricket_stadium_banner_1790598834583),
                    contentDescription = "Live Cricket Stadium",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xDD090D16))
                            )
                        )
                )

                // Banner Content
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = CricketNeonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "REAL-TIME LIVE SCORECARD",
                                color = CricketNeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Ultra-Fast Live Scores & Stats",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CricketGold)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "NO COMM",
                            color = BackgroundDark,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Top Tabs: LIVE | UPCOMING | COMPLETED | ALL
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HomeTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    val count = when (tab) {
                        HomeTab.LIVE -> liveCount
                        HomeTab.UPCOMING -> upcomingCount
                        HomeTab.COMPLETED -> completedCount
                        HomeTab.ALL -> allCount
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CricketNeonGreen else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "${tab.title} ($count)" else tab.title,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                            color = if (isSelected) BackgroundDark else TextSecondary,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }
        }

        // Series Filter Chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(seriesList) { series ->
                    val isSelected = selectedSeriesFilter == series
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) SurfaceContainerHighest else SurfaceDark)
                            .border(
                                1.dp,
                                if (isSelected) CricketNeonGreen else CardBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedSeriesFilter = series }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = series,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) CricketNeonGreen else TextSecondary
                        )
                    }
                }
            }
        }

        // Matches Header count
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (selectedTab) {
                        HomeTab.LIVE -> "Live Matches (${filteredMatches.size})"
                        HomeTab.UPCOMING -> "Upcoming Fixtures (${filteredMatches.size})"
                        HomeTab.COMPLETED -> "Recent Results (${filteredMatches.size})"
                        HomeTab.ALL -> "All Matching Matches (${filteredMatches.size})"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                if (selectedTab == HomeTab.LIVE) {
                    Text(
                        text = "Auto-Updating",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CricketNeonGreen
                    )
                }
            }
        }

        // Matches List & Filter Empty States
        if (filteredMatches.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = CricketGold,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        if (searchQuery.isNotEmpty()) {
                            Text(
                                text = "No matches found for \"$searchQuery\"",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            if (allCount > 0 && selectedTab != HomeTab.ALL) {
                                Text(
                                    text = "$allCount match(es) found in other tabs",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { selectedTab = HomeTab.ALL },
                                    colors = ButtonDefaults.buttonColors(containerColor = CricketNeonGreen)
                                ) {
                                    Text("View in ALL ($allCount)", color = BackgroundDark, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(
                                    text = "Try checking the team name spelling or series title",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                OutlinedButton(
                                    onClick = {
                                        searchQuery = ""
                                        selectedSeriesFilter = "All"
                                    }
                                ) {
                                    Text("Clear Filter", color = CricketNeonGreen)
                                }
                            }
                        } else {
                            Text(
                                text = "No ${selectedTab.title.lowercase()} matches found for $selectedSeriesFilter",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        } else {
            items(filteredMatches, key = { it.id }) { match ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    MatchScoreCard(
                        match = match,
                        onMatchClick = onMatchClick,
                        onToggleFavorite = onToggleFavorite,
                        reminder = matchReminders[match.id],
                        onReminderClick = onReminderClick
                    )
                }
            }
        }
    }
}
