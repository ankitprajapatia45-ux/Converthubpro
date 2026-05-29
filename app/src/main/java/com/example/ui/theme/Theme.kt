package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val SlateDarkColorScheme = darkColorScheme(
    primary = AccentNeonBlue,
    secondary = AccentCyan,
    tertiary = AccentPurple,
    background = SlateDarkBg,
    surface = SlateSurface,
    onBackground = TextLight,
    onSurface = TextLight,
    surfaceVariant = SlateCard,
    onSurfaceVariant = TextMuted,
    outline = BorderGlass
)

private val SlateLightColorScheme = lightColorScheme(
    primary = AccentNeonBlue,
    secondary = AccentCyan,
    tertiary = AccentPurple,
    background = SlateDarkBg, // Keep it dark for a permanent premium dark SaaS vibe
    surface = SlateSurface,
    onBackground = TextLight,
    onSurface = TextLight,
    surfaceVariant = SlateCard,
    onSurfaceVariant = TextMuted,
    outline = BorderGlass
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force premium dark theme by default
    dynamicColor: Boolean = false, // Use our handcrafted slate colors for identity
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) SlateDarkColorScheme else SlateLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
