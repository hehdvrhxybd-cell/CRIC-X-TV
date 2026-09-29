package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CricketMatch
import com.example.data.model.CricketPlayer
import com.example.data.model.SeriesTournament
import com.example.data.model.Team
import com.example.ui.components.MatchScoreCard
import com.example.ui.components.TeamLogoBadge
import com.example.ui.theme.*

@Composable
fun SearchScreen(
    matches: List<CricketMatch>,
    teams: List<Team>,
    seriesList: List<SeriesTournament>,
    players: List<CricketPlayer>,
    onMatchClick: (CricketMatch) -> Unit,
    onTeamClick: (Team) -> Unit,
    onSeriesClick: (SeriesTournament) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    var query by remember { mutableStateOf("") }

    val matchedMatches = remember(query, matches) {
        if (query.isBlank()) emptyList()
        else matches.filter {
            it.team1.name.contains(query, ignoreCase = true) ||
            it.team2.name.contains(query, ignoreCase = true) ||
            it.tournamentName.contains(query, ignoreCase = true) ||
            it.city.contains(query, ignoreCase = true)
        }
    }

    val matchedTeams = remember(query, teams) {
        if (query.isBlank()) emptyList()
        else teams.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.shortName.contains(query, ignoreCase = true)
        }
    }

    val matchedPlayers = remember(query, players) {
        if (query.isBlank()) emptyList()
        else players.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.teamName.contains(query, ignoreCase = true)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search matches, teams, players...", color = TextTertiary, fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true
                )
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
            if (query.isBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Search cricket matches, teams & players",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                if (matchedMatches.isEmpty() && matchedTeams.isEmpty() && matchedPlayers.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No results found for \"$query\"",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Matched Matches
                if (matchedMatches.isNotEmpty()) {
                    item {
                        Text(
                            text = "Matches (${matchedMatches.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CricketGold
                        )
                    }
                    items(matchedMatches) { match ->
                        MatchScoreCard(match = match, onMatchClick = onMatchClick, onToggleFavorite = {})
                    }
                }

                // Matched Teams
                if (matchedTeams.isNotEmpty()) {
                    item {
                        Text(
                            text = "Teams (${matchedTeams.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CricketNeonGreen
                        )
                    }
                    items(matchedTeams) { team ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceDark)
                                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                .clickable { onTeamClick(team) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TeamLogoBadge(team = team, size = 36.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = team.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }

                // Matched Players
                if (matchedPlayers.isNotEmpty()) {
                    item {
                        Text(
                            text = "Players (${matchedPlayers.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CricketGold
                        )
                    }
                    items(matchedPlayers) { player ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceDark)
                                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = player.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${player.teamShort} • ${player.role.name}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
