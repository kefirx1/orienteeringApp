package pl.dev.bkwiatkowski.common.ui.component.divider

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pl.dev.bkwiatkowski.common.ui.theme.AppColors

@Composable
fun Divider(
  spacer: Dp = 0.dp,
  color: Color = AppColors.warmthBeige10,
) {
  Spacer(modifier = Modifier.height(spacer))
  HorizontalDivider(
    thickness = 2.dp,
    color = color,
  )
  Spacer(modifier = Modifier.height(spacer))
}