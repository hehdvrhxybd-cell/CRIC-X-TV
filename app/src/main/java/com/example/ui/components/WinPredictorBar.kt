package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CricketMatch
import com.example.ui.theme.*

@Composable
fun WinPredictorBar(
    match: CricketMatch,
    modifier: Modifier = Modifier
) {
    val t1Percent = match.winPrediction.team1Percent
    val t2Percent = match.winPrediction.team2Percent

    val animatedT1 by animateFloatAsState(
        targetValue = t1Percent.toFloat() / 100f,
        animationSpec = tween(durationMillis = 600),
        label = "t1WinAnim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariantDark)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = match.team1.shortName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CricketGold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$t1Percent%",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            }

            Text(
                text = "WIN PROBABILITY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                letterSpacing = 1.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$t2Percent%",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = match.team2.shortName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CricketNeonGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CricketNeonGreen)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedT1.coerceIn(0.05f, 0.95f))
                    .background(CricketGold)
            )
        }
    }
}
