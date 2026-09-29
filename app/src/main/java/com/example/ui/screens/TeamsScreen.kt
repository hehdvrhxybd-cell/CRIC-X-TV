package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Team
import com.example.ui.components.TeamLogoBadge
import com.example.ui.theme.*

@Composable
fun TeamsScreen(
    teams: List<Team>,
    favoriteTeamIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onTeamClick: (Team) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "International", "T20 Leagues", "Favorites")

    val filteredTeams = remember(teams, selectedCategory, favoriteTeamIds) {
        when (selectedCategory) {
            "Favorites" -> teams.filter { favoriteTeamIds.contains(it.id) }
            "International" -> teams.filter { it.shortName.length == 3 && listOf("IND", "AUS", "ENG", "SA", "PAK", "NZ").contains(it.shortName) }
            "T20 Leagues" -> teams.filter { listOf("CSK", "MI", "RCB", "KKR").contains(it.shortName) }
            else -> teams
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Category Filter
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CricketNeonGreen else SurfaceDark)
                            .border(
                                1.dp,
                                if (isSelected) CricketNeonGreen else CardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                            color = if (isSelected) BackgroundDark else TextSecondary
                        )
                    }
                }
            }
        }

        items(filteredTeams, key = { it.id }) { team ->
            val isFav = favoriteTeamIds.contains(team.id)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .clickable { onTeamClick(team) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TeamLogoBadge(team = team, size = 44.dp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = team.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Code: ${team.shortName} • Ranking: #${teams.indexOf(team) + 1}",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }
                }

                IconButton(onClick = { onToggleFavorite(team.id) }) {
                    Icon(
                        imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Toggle Favorite",
                        tint = if (isFav) CricketGold else TextTertiary
                    )
                }
            }
        }
    }
}
