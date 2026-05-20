package pl.dev.bkwiatkowski.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = AppColors.warmthBeige30,
  onPrimary = AppColors.creamyWhite,
  primaryContainer = AppColors.warmthBeige80,
  onPrimaryContainer = AppColors.sageGreen90,
  secondary = AppColors.forestGreen40,
  onSecondary = AppColors.creamyWhite,
  secondaryContainer = AppColors.forestGreen30,
  onSecondaryContainer = AppColors.forestGreen90,
  tertiary = AppColors.vibrantOrange80,
  onTertiary = AppColors.neutralBlack,
  tertiaryContainer = AppColors.vibrantOrange30,
  onTertiaryContainer = AppColors.vibrantOrange90,
  background = AppColors.forestGreen20,
  onBackground = AppColors.creamyWhite,
  surface = AppColors.forestGreen10,
  onSurface = AppColors.creamyWhite,
  surfaceVariant = AppColors.forestGreen30,
  onSurfaceVariant = AppColors.creamyWhite,
  error = AppColors.errorRed80,
  onError = AppColors.errorRed10,
  errorContainer = AppColors.errorRed30,
  onErrorContainer = AppColors.errorRedLightContainer
)

private val LightColorScheme = lightColorScheme(
  primary = AppColors.sageGreen40,
  onPrimary = AppColors.neutralBlack,
  primaryContainer = AppColors.sageGreen90,
  onPrimaryContainer = AppColors.sageGreen10,
  secondary = AppColors.forestGreen40,
  onSecondary = AppColors.neutralBlack,
  secondaryContainer = AppColors.forestGreen90,
  onSecondaryContainer = AppColors.forestGreen10,
  tertiary = AppColors.vibrantOrange40,
  onTertiary = AppColors.creamyWhite,
  tertiaryContainer = AppColors.vibrantOrange90,
  onTertiaryContainer = AppColors.vibrantOrange10,
  background = AppColors.warmthBeige90,
  onBackground = AppColors.neutralBlack,
  surface = AppColors.warmthBeige80,
  onSurface = AppColors.neutralBlack,
  surfaceVariant = AppColors.warmthBeige80,
  onSurfaceVariant = AppColors.neutralBlack,
  error = AppColors.errorRed40,
  onError = AppColors.creamyWhite,
  errorContainer = AppColors.errorRed90,
  onErrorContainer = AppColors.errorRed10
)

@Composable
fun OrienteeringAppTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}