package com.charbel.drivetracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Asphalt950,
    onPrimary = PureWhite,
    primaryContainer = Concrete100,
    onPrimaryContainer = Asphalt950,
    secondary = SignalBlue,
    onSecondary = PureWhite,
    secondaryContainer = Color(0xFFE8F0FF),
    onSecondaryContainer = Asphalt950,
    tertiary = TaxiAmber,
    onTertiary = Asphalt950,
    background = Concrete050,
    onBackground = Asphalt950,
    surface = PureWhite,
    onSurface = Asphalt950,
    surfaceVariant = Concrete100,
    onSurfaceVariant = Fog600,
    outline = Concrete200,
)

private val DarkColors = darkColorScheme(
    primary = PureWhite,
    onPrimary = Asphalt950,
    primaryContainer = Asphalt700,
    onPrimaryContainer = PureWhite,
    secondary = SignalBlue,
    onSecondary = Asphalt950,
    secondaryContainer = Asphalt600,
    onSecondaryContainer = PureWhite,
    tertiary = TaxiAmber,
    onTertiary = Asphalt950,
    background = Asphalt950,
    onBackground = PureWhite,
    surface = Asphalt900,
    onSurface = PureWhite,
    surfaceVariant = Asphalt800,
    onSurfaceVariant = Fog500,
    outline = Asphalt600,
)

@Composable
fun DriveTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content,
    )
}
