package pl.dev.bkwiatkowski.feature.event.domain.model

import java.time.LocalDateTime

data class WebsocketWaypointVisit(
  val waypointId: Int,
  val visitedAt: LocalDateTime,
  val imagePath: String,
)