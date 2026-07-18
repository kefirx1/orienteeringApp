package pl.dev.bkwiatkowski.common.time

import pl.dev.bkwiatkowski.common.core.time.DateFormatter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateFormatterImpl : DateFormatter {
  override fun format(dateTime: LocalDateTime, format: DateFormatter.Format): String {
    val formatter: DateTimeFormatter = DateTimeFormatter
      .ofPattern(format.pattern)
      .withLocale(Locale.getDefault())

    return dateTime.format(formatter)
  }
}
