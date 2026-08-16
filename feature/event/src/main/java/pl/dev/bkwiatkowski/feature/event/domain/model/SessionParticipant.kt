package pl.dev.bkwiatkowski.feature.event.domain.model

import java.time.LocalDateTime

data class SessionParticipant (
  val sessionUuid: String,
  val joinedAt: LocalDateTime,
  val finishedAt: LocalDateTime,
)