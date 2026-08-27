package pl.dev.bkwiatkowski.feature.event.data.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.common.core.location.Position
import pl.dev.bkwiatkowski.common.storage.serializer.LocalDateTimeSerializer
import pl.dev.bkwiatkowski.feature.event.domain.model.EventSession
import pl.dev.bkwiatkowski.feature.event.domain.model.EventStatus
import pl.dev.bkwiatkowski.feature.event.domain.model.EventType
import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileMap
import java.time.LocalDateTime

@Keep
@Serializable
data class MobileEventDetailsDto(
  @SerialName("id") val id: Int,
  @SerialName("map") val map: MobileMapDto,
  @SerialName("name") val name: String,
  @SerialName("description") val description: String,
  @Serializable(with = LocalDateTimeSerializer::class)
  @SerialName("createdAt") val createdAt: LocalDateTime,
  @Serializable(with = LocalDateTimeSerializer::class)
  @SerialName("startDate") val startDate: LocalDateTime,
  @SerialName("startLocationX") val startLocationX: Float,
  @SerialName("startLocationY") val startLocationY: Float,
  @SerialName("eventStatus") val eventStatus: EventStatusDto,
  @SerialName("eventType") val eventType: EventTypeDto,
  @SerialName("session") val session: EventSessionDto,
  @SerialName("eventWaypoints") val eventWaypoints: List<MapWaypointDto>,
)

@Keep
@Serializable
data class EventSessionDto(
  @SerialName("id") val id: String,
  @Serializable(with = LocalDateTimeSerializer::class)
  @SerialName("startedAt") val startedAt: LocalDateTime,
  @SerialName("userCanJoin") val userCanJoin: Boolean,
  @Serializable(with = LocalDateTimeSerializer::class)
  @SerialName("finishedAt") val finishedAt: LocalDateTime? = null,
)

@Keep
@Serializable
data class MobileMapDto(
  @SerialName("id") val id: Int,
  @SerialName("name") val name: String,
  @SerialName("description") val description: String,
  @SerialName("imageData") val imageData: String,
  @SerialName("waypoints") val waypoints: List<MapWaypointDto>,
)

@Keep
@Serializable
data class MapWaypointDto(
  @SerialName("id") val id: Int,
  @SerialName("label") val label: String,
  @SerialName("position") val position: PositionDto,
)

@Keep
@Serializable
data class PositionDto(
  @SerialName("latitude") val latitude: Double,
  @SerialName("longitude") val longitude: Double,
)

@Keep
@Serializable
enum class EventStatusDto {
  PLANNED,
  IN_PROGRESS,
  COMPLETED,
  CONTINUOUS,
}

@Keep
@Serializable
enum class EventTypeDto {
  ONLINE,
  OFFLINE,
}
