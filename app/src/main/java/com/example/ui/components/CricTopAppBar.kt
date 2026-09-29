package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CricTopAppBar(
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    isSimulationRunning: Boolean,
    onToggleSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Logo: CRIC X TV
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { /* noop */ }
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF00C853), Color(0xFF00701A))
                        )
                    )
                    .padding(horizontal = 7.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CRIC",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = BackgroundDark,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(CricketGold, Color(0xFFFF9100))
                        )
                    )
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "X",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = BackgroundDark
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, CricketNeonGreen, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TV",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = CricketNeonGreen
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Pulse Live Dot
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(LiveRed.copy(alpha = if (isSimulationRunning) dotAlpha else 0.4f))
            )
        }

        // Action Icons
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Live Simulation toggle button
            IconButton(
                onClick = onToggleSimulation,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isSimulationRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isSimulationRunning) "Pause Live Feed" else "Resume Live Feed",
                    tint = if (isSimulationRunning) CricketNeonGreen else TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Match Notifications",
                    tint = CricketGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
