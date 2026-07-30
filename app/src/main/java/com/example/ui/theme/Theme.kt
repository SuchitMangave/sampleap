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
    primary = IosBlue,
    onPrimary = Color.White,
    primaryContainer = IosSecondarySurfaceDark,
    onPrimaryContainer = Color.White,
    secondary = IosGreen,
    onSecondary = Color.White,
    tertiary = IosPurple,
    background = IosBackgroundDark,
    onBackground = IosTextPrimaryDark,
    surface = IosSurfaceDark,
    onSurface = IosTextPrimaryDark,
    surfaceVariant = IosSecondarySurfaceDark,
    onSurfaceVariant = IosTextSecondaryDark,
    outline = IosBorderDark,
    error = IosRed
)

private val LightColorScheme = lightColorScheme(
    primary = IosBlue,
    onPrimary = Color.White,
    primaryContainer = IosSecondarySurfaceLight,
    onPrimaryContainer = IosTextPrimaryLight,
    secondary = IosGreen,
    onSecondary = Color.White,
    tertiary = IosPurple,
    background = IosBackgroundLight,
    onBackground = IosTextPrimaryLight,
    surface = IosSurfaceLight,
    onSurface = IosTextPrimaryLight,
    surfaceVariant = IosSecondarySurfaceLight,
    onSurfaceVariant = IosTextSecondaryLight,
    outline = IosBorderLight,
    error = IosRed
)

@Composable
fun RupeeTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // We stick to Apple iOS styling palette for unified iOS feel
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
