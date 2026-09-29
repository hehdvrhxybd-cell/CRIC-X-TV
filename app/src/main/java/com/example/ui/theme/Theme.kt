package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CricketNeonGreen,
    onPrimary = BackgroundDark,
    primaryContainer = CricketGreenDark,
    onPrimaryContainer = CricketNeonGreen,
    secondary = CricketGold,
    onSecondary = BackgroundDark,
    secondaryContainer = CricketGoldDark,
    onSecondaryContainer = CricketGold,
    tertiary = LiveRed,
    onTertiary = TextPrimary,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    outlineVariant = SurfaceContainerHighest
)

@Composable
fun CricXTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
