package pl.dev.bkwiatkowski.common.ui.component.icon

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage


enum class ImageSize(val size: Dp) {
  SMALL(size = 12.dp),
  SMALL_X(size = 24.dp),
  MEDIUM(size = 32.dp),
  LARGE(size = 64.dp),
  EXTRA_LARGE(size = 150.dp),
}

@Composable
fun CustomImage(
  iconRes: Int,
  imageSize: ImageSize = ImageSize.MEDIUM,
  color: Color? = null,
  contentDescription: String? = null
) {
  AsyncImage(
    modifier = Modifier.size(imageSize.size),
    model = iconRes,
    contentDescription = contentDescription,
    colorFilter = color?.let { ColorFilter.tint(color = it) },
  )
}