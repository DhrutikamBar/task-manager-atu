package com.atu.jira.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Jira Brand Colors
val JiraBlue = Color(0xFF0C66E4)
val JiraBlueDark = Color(0xFF0052CC)
val JiraSlate = Color(0xFF172B4D)
val JiraBackground = Color(0xFFFFFFFF)
val JiraSurfaceVariant = Color(0xFFF4F5F7)
val JiraError = Color(0xFFDE350B)
val JiraSuccess = Color(0xFF00875A)
val JiraWarning = Color(0xFFFF991F)

val JiraLightColorScheme = lightColorScheme(
    primary = JiraBlue,
    onPrimary = Color.White,
    primaryContainer = JiraBlue.copy(alpha = 0.1f),
    onPrimaryContainer = JiraBlueDark,
    secondary = JiraSlate,
    onSecondary = Color.White,
    secondaryContainer = JiraSurfaceVariant,
    onSecondaryContainer = JiraSlate,
    background = JiraBackground,
    onBackground = JiraSlate,
    surface = JiraBackground,
    onSurface = JiraSlate,
    surfaceVariant = JiraSurfaceVariant,
    onSurfaceVariant = Color(0xFF42526E),
    error = JiraError,
    onError = Color.White,
    outline = Color(0xFFDFE1E6)
)

val JiraDarkColorScheme = darkColorScheme(
    primary = Color(0xFF579DFF),
    onPrimary = Color(0xFF1D2125),
    primaryContainer = Color(0xFF0052CC),
    onPrimaryContainer = Color(0xFFE9F2FF),
    secondary = Color(0xFFB3BAC5),
    onSecondary = Color(0xFF091E42),
    secondaryContainer = Color(0xFF22272B),
    onSecondaryContainer = Color(0xFFB3BAC5),
    background = Color(0xFF1D2125),
    onBackground = Color(0xFFB3BAC5),
    surface = Color(0xFF22272B),
    onSurface = Color(0xFFB3BAC5),
    surfaceVariant = Color(0xFF2C333A),
    onSurfaceVariant = Color(0xFF9FADBC),
    error = Color(0xFFFF5630),
    onError = Color(0xFF1D2125),
    outline = Color(0xFF738496)
)

@Composable
fun JiraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        JiraDarkColorScheme
    } else {
        JiraLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
