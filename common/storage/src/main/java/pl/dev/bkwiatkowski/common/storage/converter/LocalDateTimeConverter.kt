package pl.dev.bkwiatkowski.common.storage.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ProvidedTypeConverter
class LocalDateTimeConverter {
  private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  @TypeConverter
  fun fromTimestamp(value: String?): LocalDateTime? = value?.let {
    LocalDateTime.parse(it, formatter)
  }

  @TypeConverter
  fun toTimestamp(date: LocalDateTime?): String? = date?.format(formatter)
}


