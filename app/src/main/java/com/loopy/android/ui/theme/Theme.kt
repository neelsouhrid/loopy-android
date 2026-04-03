package com.loopy.android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dark theme colors
val BackgroundDark = Color(0xFF121212)
val SurfaceDark = Color(0xFF1E1E1E)
val SurfaceVariantDark = Color(0xFF2D2D2D)
val PrimaryGreen = Color(0xFF4CAF50)
val PrimaryGreenVariant = Color(0xFF388E3C)
val SecondaryAmber = Color(0xFFFFC107)
val SecondaryAmberVariant = Color(0xFFFFB300)
val ErrorRed = Color(0xFFCF6679)
val OnPrimary = Color(0xFF000000)
val OnSecondary = Color(0xFF000000)
val OnBackground = Color(0xFFFFFFFF)
val OnSurface = Color(0xFFFFFFFF)
val OnError = Color(0xFF000000)

// Track colors
val TrackEmpty = Color(0xFF3D3D3D)
val TrackRecorded = Color(0xFF4CAF50)
val TrackSelected = Color(0xFF8BC34A)
val TrackPlaying = Color(0xFFFFC107)
val TrackRecording = Color(0xFFF44336)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimary,
    secondary = SecondaryAmber,
    onSecondary = OnSecondary,
    tertiary = PrimaryGreenVariant,
    background = BackgroundDark,
    onBackground = OnBackground,
    surface = SurfaceDark,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariantDark,
    error = ErrorRed,
    onError = OnError
)

@Composable
fun LoopyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // Always use dark theme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundDark.toArgb()
            window.navigationBarColor = BackgroundDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}