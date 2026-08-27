package pl.dev.bkwiatkowski.feature.event.domain.model

import java.time.LocalDateTime

data class EventWaypointVisitRecord(
  val id: Long,
  val waypointId: Int,
  val visitedAt: LocalDateTime,
  val imagePath: String,
  val sendOnBackend: Boolean,
)
