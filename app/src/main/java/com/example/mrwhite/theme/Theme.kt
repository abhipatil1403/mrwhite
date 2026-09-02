package com.example.mrwhite.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val AppColorScheme =
  lightColorScheme(
    primary = NavyPrimary,
    onPrimary = WhiteBackground,
    secondary = NavyPrimary,
    onSecondary = WhiteBackground,
    tertiary = NavyPrimary,
    onTertiary = WhiteBackground,
    background = WhiteBackground,
    surface = WhiteBackground,
    onBackground = BlackText,
    onSurface = BlackText,
    error = ErrorRed,
    onError = WhiteBackground
  )

@Composable
fun MrWhiteTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = AppColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
