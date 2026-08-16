package pl.dev.bkwiatkowski.technical.backend.domain.model

import java.time.LocalDateTime

data class BESessionParticipant (
  val sessionUuid: String,
  val joinedAt: LocalDateTime,
  val finishedAt: LocalDateTime,
)