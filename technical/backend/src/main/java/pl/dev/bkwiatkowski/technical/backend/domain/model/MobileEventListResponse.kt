package pl.dev.bkwiatkowski.technical.backend.domain.model

import java.time.LocalDateTime

data class MobileEventListResponse(
  val id: Int,
  val map: BEMobileMap,
  val name: String,
  val description: String,
  val createdAt: LocalDateTime,
  val startDate: LocalDateTime,
  val startLocationX: Float,
  val startLocationY: Float,
  val createdByUsername: String,
  val eventType: BEEventType,
)

enum class BEEventType {
  ONLINE,
  OFFLINE,
}
