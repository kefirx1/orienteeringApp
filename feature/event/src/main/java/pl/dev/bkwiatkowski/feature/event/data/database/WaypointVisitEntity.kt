package pl.dev.bkwiatkowski.feature.event.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "waypoint_visits")
data class WaypointVisitEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val waypointId: Int,
  val visitedAt: LocalDateTime,
  val imagePath: String,
  val sessionUuid: String,
  val sendOnBackend: Boolean,
)