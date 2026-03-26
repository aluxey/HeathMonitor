package com.cyberdoc.app.core

import java.util.Locale

private val packagePattern = Regex("^[a-z0-9_]+(\\.[a-z0-9_]+)+$")

fun resolveSourceDisplayName(sourceId: String?, sourceName: String?): String {
    val normalizedName = sourceName?.trim().orEmpty()
    if (normalizedName.isNotEmpty() && !packagePattern.matches(normalizedName)) {
        return normalizedName
    }

    val normalizedId = sourceId?.trim().orEmpty()
    if (normalizedId.isEmpty()) {
        return normalizedName.ifBlank { "Health Connect" }
    }

    val lowerId = normalizedId.lowercase(Locale.US)
    return when {
        lowerId == "manual" -> "Saisie manuelle"
        lowerId == "health_connect" -> "Health Connect"
        "zepp" in lowerId || "amazfit" in lowerId -> "Zepp"
        "samsung" in lowerId && "health" in lowerId -> "Samsung Health"
        "google.android.apps.fitness" in lowerId -> "Google Fit"
        "myfitnesspal" in lowerId -> "MyFitnessPal"
        "yazio" in lowerId -> "Yazio"
        else -> prettifySourceId(normalizedId)
    }
}

private fun prettifySourceId(sourceId: String): String =
    sourceId
        .substringAfterLast('.')
        .split('.', '_', '-')
        .filter { it.isNotBlank() }
        .joinToString(separator = " ") { token ->
            token.replaceFirstChar { char ->
                if (char.isLowerCase()) {
                    char.titlecase(Locale.US)
                } else {
                    char.toString()
                }
            }
        }
        .ifBlank { sourceId }
