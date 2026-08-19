package pl.dev.bkwiatkowski.feature.dashboard.domain.model

import java.time.LocalDateTime

data class SessionsData(
  val sessions: List<UserSessionData>,
)

data class UserSessionData(
  val sessionUuid: String,
  val startedAt: LocalDateTime,
  val visitedWaypointsCount: Int,
  val mapName: String,
  val eventName: String,
  val finishedAt: LocalDateTime,
)