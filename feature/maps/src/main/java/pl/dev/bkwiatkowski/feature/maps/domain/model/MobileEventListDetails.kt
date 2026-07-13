package pl.dev.bkwiatkowski.feature.maps.domain.model

import java.time.LocalDateTime

data class MobileEvents(
  val events: List<MobileEventListDetails>,
)

data class MobileEventListDetails(
  val id: Int,
  val map: MobileMap,
  val name: String,
  val description: String,
  val createdAt: LocalDateTime,
  val startDate: LocalDateTime,
  val startLocationX: Float,
  val startLocationY: Float,
  val createdByUsername: String,
  val eventType: EventType,
)

enum class EventType {
  ONLINE,
  OFFLINE,
}
