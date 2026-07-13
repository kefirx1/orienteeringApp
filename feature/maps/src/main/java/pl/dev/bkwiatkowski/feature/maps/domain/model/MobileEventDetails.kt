package pl.dev.bkwiatkowski.feature.maps.domain.model

import java.time.LocalDateTime

data class MobileEventDetails(
  val id: Int,
  val map: MobileMap,
  val name: String,
  val description: String,
  val createdAt: LocalDateTime,
  val startDate: LocalDateTime,
  val startLocationX: Float,
  val startLocationY: Float,
  val eventStatus: EventStatus,
  val eventType: EventType,
  val finishedAt: LocalDateTime? = null,
  val allowOfflineTracking: Boolean? = null,
)

enum class EventStatus {
  PLANNED,
  IN_PROGRESS,
  COMPLETED,
  CONTINUOUS,
}
