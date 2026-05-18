package pl.dev.bkwiatkowski.common.ui.component.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun MapComponent(
  content: @Composable () -> Unit
) {
  val context = LocalContext.current

}