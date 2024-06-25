package com.example.jetarticlesapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.jetarticlesapp.datastores.SettingsDataStore

@Composable
fun AppTheme(
    colors: List<Color>? = null,
    content: @Composable () -> Unit
) {
    val isDark = when (SettingsDataStore(LocalContext.current).theme.collectAsState(initial = "system").value) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    if (colors == null) {
        val dynamicColor = SettingsDataStore(LocalContext.current).dynamicColors.collectAsState(initial = false).value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        val extraDark by SettingsDataStore(LocalContext.current).extraDark.collectAsState(initial = false)
        val colorTheme by SettingsDataStore(LocalContext.current).colorTheme.collectAsState(initial = "green")
        val lightColors = when(colorTheme) {
            "blue" -> BlueLightColors
            "green" -> GreenLightColors
            "red" -> RedLightColors
            "orange" -> OrangeLightColors
            "purple" -> PurpleLightColors
            else -> BlueLightColors
        }
        val darkColors = when(colorTheme) {
            "blue" -> BlueDarkColors
            "green" -> GreenDarkColors
            "red" -> RedDarkColors
            "orange" -> OrangeDarkColors
            "purple" -> PurpleDarkColors
            else -> BlueDarkColors
        }
        val colorScheme = when {
            dynamicColor && isDark -> {
                if(extraDark) dynamicDarkColorScheme(LocalContext.current).copy(
                    background = Color.Black,
                    surface = Color.Black
                )
                else dynamicDarkColorScheme(LocalContext.current)
            }
            dynamicColor && !isDark -> {
                dynamicLightColorScheme(LocalContext.current)
            }
            isDark and extraDark -> darkColors.copy(
                background = Color.Black,
                surface = Color.Black
            )
            isDark -> darkColors
            else -> lightColors
        }
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    } else {
        val colorScheme =
            if (isDark) darkColorScheme(
                primary = colors[0],
                primaryContainer = colors[2],
                onPrimaryContainer = colors[1],
                secondary = colors[3],
                secondaryContainer = colors[5],
                onSecondaryContainer = colors[4],
            )
            else lightColorScheme(
                primary = colors[0],
                primaryContainer = colors[1],
                onPrimaryContainer = colors[2],
                secondary = colors[3],
                secondaryContainer = colors[4],
                onSecondaryContainer = colors[5],
            )
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}