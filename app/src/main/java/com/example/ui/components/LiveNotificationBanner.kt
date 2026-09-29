package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.LiveEventAlert
import com.example.ui.theme.*

@Composable
fun LiveNotificationBanner(
    alert: LiveEventAlert?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = alert != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        if (alert != null) {
            val borderColor = if (alert.isWicket) LiveRed else CricketGold
            val bgColor = if (alert.isWicket) LiveRed.copy(alpha = 0.95f) else SurfaceVariantDark

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alert.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = if (alert.isWicket) Color.White else CricketGold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = alert.subtitle,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (alert.isWicket) Color.White.copy(alpha = 0.9f) else TextPrimary
                    )
                }
            }
        }
    }
}
