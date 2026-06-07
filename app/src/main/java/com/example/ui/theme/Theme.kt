package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    secondary = GoldLight,
    tertiary = WhitePure,
    background = NavyDark,
    surface = NavyPrimary,
    onBackground = LightBg,
    onSurface = WhitePure
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    secondary = GoldAccent,
    tertiary = NavyLight,
    background = LightBg,
    surface = WhitePure,
    onBackground = DarkText,
    onSurface = DarkText
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to enforce our high-contrast Navy/Gold palette
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
