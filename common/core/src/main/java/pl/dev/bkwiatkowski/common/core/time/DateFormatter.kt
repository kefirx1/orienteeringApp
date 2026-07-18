package pl.dev.bkwiatkowski.common.core.time

import java.time.LocalDateTime

interface DateFormatter {
  enum class Format(val pattern: String) {
    DATE_ONLY(pattern = "dd.MM.yyyy"),
    TIME_ONLY(pattern = "HH:mm"),
    DATE_TIME(pattern = "dd.MM.yyyy HH:mm"),
    ISO(pattern = "yyyy-MM-dd'T'HH:mm:ss") ,
    ;
  }

  fun format(dateTime: LocalDateTime, format: Format): String
}
