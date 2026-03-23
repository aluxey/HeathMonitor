package com.cyberdoc.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Sage700,
    onPrimary = Sand50,
    primaryContainer = Sage100,
    onPrimaryContainer = Ink900,
    secondary = Olive600,
    onSecondary = Sand50,
    secondaryContainer = Sand200,
    onSecondaryContainer = Ink900,
    tertiary = Sage500,
    onTertiary = Sand50,
    tertiaryContainer = Sage50,
    onTertiaryContainer = Ink900,
    error = ErrorSoft,
    onError = Sand50,
    background = Sand50,
    onBackground = Ink900,
    surface = ColorUnused.surface,
    onSurface = Ink900,
    surfaceVariant = Sand100,
    onSurfaceVariant = Ink700,
    outline = Mist300,
    outlineVariant = Sand200,
)

private object ColorUnused {
    val surface = androidx.compose.ui.graphics.Color(0xFFFFFCF8)
}

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
