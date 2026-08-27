package pl.dev.bkwiatkowski.feature.event.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.dev.bkwiatkowski.common.storage.converter.LocalDateTimeConverter

@Database(entities = [WaypointVisitEntity::class], version = 1)
@TypeConverters(value = [LocalDateTimeConverter::class])
abstract class EventDatabase : RoomDatabase() {
  abstract fun waypointVisitDao(): WaypointVisitDao

  companion object {
    const val EVENT_DATABASE_NAME = "event-database"
  }
}