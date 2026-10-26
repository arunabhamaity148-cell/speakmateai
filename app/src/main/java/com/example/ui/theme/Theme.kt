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

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimary,
    onPrimary = Color(0xFF00382E),
    primaryContainer = TealContainerDark,
    onPrimaryContainer = TealLight,
    secondary = CyanSecondary,
    onSecondary = Color(0xFF003548),
    secondaryContainer = CyanContainerDark,
    onSecondaryContainer = Color(0xFFB8EAFF),
    tertiary = GoldAccent,
    onTertiary = Color(0xFF432C00),
    tertiaryContainer = GoldContainer,
    onTertiaryContainer = Color(0xFFFFDF9E),
    background = DarkNavyBackground,
    onBackground = DarkTextPrimary,
    surface = DarkNavySurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkNavySurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkOutline,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = TealContainerLight,
    onPrimaryContainer = Color(0xFF00382E),
    secondary = CyanSecondaryDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC7EFFF),
    onSecondaryContainer = Color(0xFF001F2A),
    tertiary = Color(0xFF8F6200),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDF9E),
    onTertiaryContainer = Color(0xFF2C1C00),
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightOutline,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun SpeakMateTheme(
    darkTheme: Boolean = true, // Default to deep navy theme as per design requirements
    dynamicColor: Boolean = false, // Keep brand aesthetic consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
