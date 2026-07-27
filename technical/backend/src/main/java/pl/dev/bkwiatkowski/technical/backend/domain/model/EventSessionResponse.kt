package pl.dev.bkwiatkowski.technical.backend.domain.model

import java.time.LocalDateTime

data class EventSessionResponse(
  val id: String,
  val startedAt: LocalDateTime,
  val userCanJoin: Boolean,
  val finishedAt: LocalDateTime? = null
)