package com.x256n.sdtrainingimagepreparer.desktop.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

// https://material.io/design/color/the-color-system.html#color-theme-creation

private val LightColorPalette = lightColors(
    primary = GreenLight,
    primaryVariant = Green,
    onPrimary = BlackDark,
    secondary = WhiteLight,
    secondaryVariant = WhiteDark,
    onSecondary = BlueLight,
    error = RedDark,
    onError = WhiteLight,
    background = Blue,
    onBackground = WhiteLight,
    surface = BlackDark,
    onSurface = WhiteLight,
)

@Composable
fun DefaultTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSpaces provides Spaces()) {
        MaterialTheme(
            colors = LightColorPalette,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}