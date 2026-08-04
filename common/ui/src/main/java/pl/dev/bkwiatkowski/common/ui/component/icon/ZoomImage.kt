package pl.dev.bkwiatkowski.common.ui.component.icon

import android.graphics.Bitmap
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.github.panpf.zoomimage.ZoomImage
import com.github.panpf.zoomimage.compose.ZoomState
import com.github.panpf.zoomimage.compose.rememberZoomState
import kotlinx.coroutines.flow.distinctUntilChanged

data class ZoomImageData(
  val bitmap: Bitmap,
  val contentDescription: String? = null,
)

@Composable
fun ZoomImage(
  zoomImageData: ZoomImageData,
  onZoomChange: (Float) -> Unit = {},
) {
  var resetKey by remember { mutableIntStateOf(0) }
  var hadZoomedAboveThreshold by remember { mutableStateOf(false) }

  key(resetKey) {
    val zoomState: ZoomState = rememberZoomState()
    val painter = remember(zoomImageData.bitmap) {
      BitmapPainter(image = zoomImageData.bitmap.asImageBitmap())
    }

    LaunchedEffect(zoomState) {
      snapshotFlow { zoomState.zoomable.transform.scaleX }
        .distinctUntilChanged()
        .collect { scale ->
          onZoomChange(scale)

          val isZoomed = scale > 1.01f
          if (isZoomed) {
            hadZoomedAboveThreshold = true
          } else if (hadZoomedAboveThreshold) {
            resetKey++
            hadZoomedAboveThreshold = false
          }
        }
    }

    ZoomImage(
      modifier = Modifier
        .fillMaxSize(),
      painter = painter,
      contentDescription = zoomImageData.contentDescription,
      zoomState = zoomState,
    )
  }
}