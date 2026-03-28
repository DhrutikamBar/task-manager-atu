package com.atu.jira.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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

// Jira-style Typography
val JiraTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)

class ThemeController(isDark: Boolean) {
    var isDark by mutableStateOf(isDark)
    fun toggle() {
        isDark = !isDark
    }
}

val LocalThemeController = staticCompositionLocalOf<ThemeController> {
    error("No ThemeController provided")
}

@Composable
fun JiraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val themeController = remember { ThemeController(darkTheme) }

    CompositionLocalProvider(LocalThemeController provides themeController) {
        val colorScheme = if (themeController.isDark) {
            JiraDarkColorScheme
        } else {
            JiraLightColorScheme
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = JiraTypography,
            content = content
        )
    }
}
