package pl.dev.bkwiatkowski.common.ui.preview

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import pl.dev.bkwiatkowski.common.core.time.DateFormatter
import pl.dev.bkwiatkowski.common.ui.image.BitmapReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MockProvider {
  val bitmapReader: BitmapReader by lazy { BitmapReader { _ -> null } }

  val dateFormatter: DateFormatter by lazy {
    object : DateFormatter {
      override fun format(dateTime: LocalDateTime, format: DateFormatter.Format): String {
        val formatter: DateTimeFormatter = DateTimeFormatter
          .ofPattern(format.pattern)
          .withLocale(Locale.getDefault())

      return dateTime.format(formatter)
      }
    }
  }

  val snackbarHost by lazy { SnackbarHostState() }

  val lifecycleOwner: LifecycleOwner by lazy {
    object : LifecycleOwner {
      private val lifecycleRegistry = LifecycleRegistry(this).apply {
        currentState = Lifecycle.State.RESUMED
      }

      override val lifecycle: Lifecycle
        get() = lifecycleRegistry
    }
  }
}