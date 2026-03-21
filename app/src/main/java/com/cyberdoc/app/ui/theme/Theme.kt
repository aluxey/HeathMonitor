package com.cyberdoc.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Moss700,
    onPrimary = Clay50,
    primaryContainer = Moss300,
    onPrimaryContainer = Ink900,
    secondary = Clay700,
    onSecondary = Clay50,
    secondaryContainer = Clay100,
    onSecondaryContainer = Ink900,
    tertiary = Coral700,
    onTertiary = Clay50,
    tertiaryContainer = Coral300,
    onTertiaryContainer = Ink900,
    background = Clay50,
    onBackground = Ink900,
    surface = Color(0xFFFFFBF7),
    onSurface = Ink900,
)

@Composable
fun CyberDocTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = CyberDocTypography,
        content = content,
    )
}
