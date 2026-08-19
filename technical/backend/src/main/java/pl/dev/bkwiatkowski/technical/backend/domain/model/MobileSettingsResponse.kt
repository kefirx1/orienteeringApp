package pl.dev.bkwiatkowski.technical.backend.domain.model

import java.time.LocalDateTime

data class MobileSettingsResponse(
  val serverLocalDateTime: LocalDateTime,
  val userId: Int,
)