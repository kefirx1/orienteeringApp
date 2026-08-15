package pl.dev.bkwiatkowski.technical.backend.domain.model

import java.time.LocalDateTime

data class WebsocketWaypointVisit(
  val waypointId: Int,
  val visitedAt: LocalDateTime,
  val imagePath: String,
)
