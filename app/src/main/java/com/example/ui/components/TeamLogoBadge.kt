package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Team
import com.example.ui.theme.CricketGold

@Composable
fun TeamLogoBadge(
    team: Team,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    val primaryColor = try {
        Color(android.graphics.Color.parseColor(team.primaryColorHex))
    } catch (e: Exception) {
        Color(0xFF1E88E5)
    }

    val secondaryColor = try {
        Color(android.graphics.Color.parseColor(team.secondaryColorHex))
    } catch (e: Exception) {
        Color(0xFFFFB300)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(primaryColor, secondaryColor)
                )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.25f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = team.shortName.take(3),
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = (size.value * 0.32f).sp,
            letterSpacing = (-0.5).sp
        )
    }
}
