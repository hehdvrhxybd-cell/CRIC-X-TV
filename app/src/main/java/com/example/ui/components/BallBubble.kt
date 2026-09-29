package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeliveryBall
import com.example.ui.theme.*

@Composable
fun BallBubble(
    ball: DeliveryBall,
    modifier: Modifier = Modifier,
    size: Dp = 26.dp
) {
    val (bgColor, textColor, borderColor) = when {
        ball.isWicket -> Triple(LiveRed, Color.White, LiveRed)
        ball.isSix -> Triple(SixPurple, Color.White, SixPurple)
        ball.isFour -> Triple(FourBlue, Color.White, FourBlue)
        ball.isExtra -> Triple(ExtraYellow.copy(alpha = 0.25f), ExtraYellow, ExtraYellow)
        ball.runs == 0 -> Triple(SurfaceVariantDark, TextSecondary, CardBorder)
        else -> Triple(SurfaceContainerHighest, TextPrimary, CardBorder)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (ball.display == "0") "•" else ball.display,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = if (ball.display == "0") 16.sp else (size.value * 0.42f).sp
        )
    }
}
