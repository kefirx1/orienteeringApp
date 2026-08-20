package pl.dev.bkwiatkowski.feature.maps.domain.model

import pl.dev.bkwiatkowski.common.core.location.Position
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
  val session: EventSession?,
  val eventWaypoints: List<MapWaypoint>,
)

data class EventSession(
  val id: String,
  val startedAt: LocalDateTime,
  val userCanJoin: Boolean,
  val finishedAt: LocalDateTime?,
)

enum class EventStatus {
  PLANNED,
  IN_PROGRESS,
  COMPLETED,
  CONTINUOUS,
}

enum class UserSessionStatus {
  JOINED,
  NOT_JOINED,
  FINISHED,
}

data class MapWaypoint(
  val id: Int,
  val label: String,
  val position: Position,
)
