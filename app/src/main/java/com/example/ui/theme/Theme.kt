package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FestivalColorScheme =
  darkColorScheme(
    primary = MarigoldGold,
    onPrimary = MidnightRiver,
    secondary = WarmSaffron,
    onSecondary = MidnightRiver,
    tertiary = CrimsonPataka,
    background = MidnightRiver,
    onBackground = CreamText,
    surface = DeepIndigo,
    onSurface = CreamText,
  )

@Composable
fun DangaBhasaTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = FestivalColorScheme,
    typography = Typography,
    content = content,
  )
}
