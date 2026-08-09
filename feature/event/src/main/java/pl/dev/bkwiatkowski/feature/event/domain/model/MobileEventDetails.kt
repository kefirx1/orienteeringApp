package pl.dev.bkwiatkowski.feature.event.domain.model

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
  val finishedAt: LocalDateTime?,
  val allowOfflineTracking: Boolean?,
  val session: EventSession,
  val eventWaypoints: List<MapWaypoint>,
)

data class EventSession(
  val id: String,
  val startedAt: LocalDateTime,
  val userCanJoin: Boolean,
  val finishedAt: LocalDateTime?,
)

enum class EventType {
  ONLINE,
  OFFLINE,
}

enum class EventStatus {
  PLANNED,
  IN_PROGRESS,
  COMPLETED,
  CONTINUOUS,
}

data class MobileMap(
  val id: Int,
  val name: String,
  val description: String,
  val imageData: String,
  val waypoints: List<MapWaypoint>,
)

data class MapWaypoint(
  val id: Int,
  val label: String,
  val position: Position,
)
