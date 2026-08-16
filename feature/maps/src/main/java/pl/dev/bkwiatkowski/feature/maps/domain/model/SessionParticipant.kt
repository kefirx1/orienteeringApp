package pl.dev.bkwiatkowski.feature.maps.domain.model

import java.time.LocalDateTime

data class SessionParticipant(
  val sessionUuid: String,
  val joinedAt: LocalDateTime,
  val finishedAt: LocalDateTime,
)
