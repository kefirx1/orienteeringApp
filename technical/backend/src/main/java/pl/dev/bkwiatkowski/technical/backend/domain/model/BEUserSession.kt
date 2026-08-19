package pl.dev.bkwiatkowski.technical.backend.domain.model

import java.time.LocalDateTime

data class BEUserSession(
  val sessionUuid: String,
  val startedAt: LocalDateTime,
  val visitedWaypointsCount: Int,
  val mapName: String,
  val eventName: String,
  val finishedAt: LocalDateTime,
)
