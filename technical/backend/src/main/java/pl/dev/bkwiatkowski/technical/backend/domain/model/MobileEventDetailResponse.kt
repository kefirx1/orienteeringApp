package pl.dev.bkwiatkowski.technical.backend.domain.model

import java.time.LocalDateTime

data class MobileEventDetailResponse(
  val id: Int,
  val map: BEMobileMap,
  val name: String,
  val description: String,
  val createdAt: LocalDateTime,
  val startDate: LocalDateTime,
  val startLocationX: Float,
  val startLocationY: Float,
  val eventStatus: BEEventStatus,
  val eventType: BEEventType,
  val finishedAt: LocalDateTime? = null,
  val allowOfflineTracking: Boolean? = null,
  val eventWaypoints: List<BEMapWaypoint>,
  val session: EventSessionResponse? = null,
)

enum class BEEventStatus(val value: String) {
  PLANNED("PLANNED"),
  IN_PROGRESS("IN_PROGRESS"),
  COMPLETED("COMPLETED"),
  CONTINUOUS("CONTINUOUS"),
}
