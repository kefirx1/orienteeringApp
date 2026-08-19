package pl.dev.bkwiatkowski.technical.mobile.domain.model

import java.time.LocalDateTime

data class MobileSettings(
  val serverLocalDateTime: LocalDateTime,
  val userId: Int,
)
