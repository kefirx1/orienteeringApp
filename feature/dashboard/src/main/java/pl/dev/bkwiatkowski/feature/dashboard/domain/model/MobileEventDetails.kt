package pl.dev.bkwiatkowski.feature.dashboard.domain.model

import java.time.LocalDateTime

data class MobileEventDetails(
  val id: Int,
  val session: EventSession,
)

data class EventSession(
  val id: String,
  val startedAt: LocalDateTime,
  val userCanJoin: Boolean,
  val finishedAt: LocalDateTime?,
)