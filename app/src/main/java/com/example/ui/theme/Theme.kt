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
    primary = MedicalPrimaryDarkTheme,
    onPrimary = Color(0xFF003258),
    primaryContainer = MedicalPrimaryDark,
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = MedicalTealDarkTheme,
    onSecondary = Color(0xFF003730),
    secondaryContainer = Color(0xFF005047),
    onSecondaryContainer = Color(0xFF9CF2E6),
    tertiary = CaduceusGoldDarkTheme,
    onTertiary = Color(0xFF3F2E00),
    background = MedicalBackgroundDark,
    onBackground = Color(0xFFE2E2E6),
    surface = MedicalSurfaceDark,
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = MedicalSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC4C6D0)
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = MedicalTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC7F8EE),
    onSecondaryContainer = Color(0xFF00201B),
    tertiary = CaduceusGold,
    onTertiary = Color.White,
    background = MedicalBackground,
    onBackground = TextPrimary,
    surface = MedicalSurface,
    onSurface = TextPrimary,
    surfaceVariant = MedicalSurfaceVariant,
    onSurfaceVariant = TextSecondary
)

@Composable
fun BandeSanteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep medical identity crisp
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
