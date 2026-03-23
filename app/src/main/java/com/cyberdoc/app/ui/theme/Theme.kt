package com.cyberdoc.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = FigmaPrimary,
    onPrimary = FigmaCard,
    primaryContainer = FigmaPrimarySoft,
    onPrimaryContainer = FigmaForeground,
    secondary = Chart2,
    onSecondary = FigmaCard,
    secondaryContainer = FigmaMuted,
    onSecondaryContainer = FigmaForeground,
    tertiary = Chart3,
    onTertiary = FigmaForeground,
    tertiaryContainer = FigmaMuted,
    onTertiaryContainer = FigmaForeground,
    error = FigmaError,
    onError = FigmaCard,
    background = FigmaBackground,
    onBackground = FigmaForeground,
    surface = FigmaCard,
    onSurface = FigmaForeground,
    surfaceVariant = FigmaMuted,
    onSurfaceVariant = FigmaMutedText,
    outline = FigmaBorder,
    outlineVariant = FigmaBorder,
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
