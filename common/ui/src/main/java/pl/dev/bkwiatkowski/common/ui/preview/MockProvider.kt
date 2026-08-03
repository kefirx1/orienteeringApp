package pl.dev.bkwiatkowski.common.ui.preview

import android.graphics.BitmapFactory
import androidx.compose.material3.SnackbarHostState
import pl.dev.bkwiatkowski.common.core.time.DateFormatter
import pl.dev.bkwiatkowski.common.ui.image.BitmapReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MockProvider {
  val bitmapReader: BitmapReader = BitmapReader { encoded ->
    BitmapFactory.decodeByteArray(
      encoded.toByteArray(),
      0,
      encoded.toByteArray().size,
    )
  }

  val dateFormatter: DateFormatter = object : DateFormatter {
    override fun format(dateTime: LocalDateTime, format: DateFormatter.Format): String {
      val formatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern(format.pattern)
        .withLocale(Locale.getDefault())

      return dateTime.format(formatter)
    }
  }

  val snackbarHost = SnackbarHostState()
}