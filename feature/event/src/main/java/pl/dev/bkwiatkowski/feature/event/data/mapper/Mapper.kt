package pl.dev.bkwiatkowski.feature.event.data.mapper

import pl.dev.bkwiatkowski.common.core.location.Position
import pl.dev.bkwiatkowski.feature.event.data.model.EventSessionDto
import pl.dev.bkwiatkowski.feature.event.data.model.EventStatusDto
import pl.dev.bkwiatkowski.feature.event.data.model.EventTypeDto
import pl.dev.bkwiatkowski.feature.event.data.model.MapWaypointDto
import pl.dev.bkwiatkowski.feature.event.data.model.MobileEventDetailsDto
import pl.dev.bkwiatkowski.feature.event.data.model.MobileMapDto
import pl.dev.bkwiatkowski.feature.event.data.model.PositionDto
import pl.dev.bkwiatkowski.feature.event.domain.model.EventSession
import pl.dev.bkwiatkowski.feature.event.domain.model.EventStatus
import pl.dev.bkwiatkowski.feature.event.domain.model.EventType
import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileMap

fun MobileEventDetails.toDto(): MobileEventDetailsDto = MobileEventDetailsDto(
  id = id,
  map = MobileMapDto(
    id = map.id,
    name = map.name,
    description = map.description,
    imageData = map.imageData,
    waypoints = map.waypoints.map { mw ->
      MapWaypointDto(
        id = mw.id,
        label = mw.label,
        position = PositionDto(
          latitude = mw.position.latitude,
          longitude = mw.position.longitude,
        ),
      )
    }
  ),
  name = name,
  description = description,
  createdAt = createdAt,
  startDate = startDate,
  startLocationX = startLocationX,
  startLocationY = startLocationY,
  eventStatus = when (eventStatus) {
    EventStatus.PLANNED -> EventStatusDto.PLANNED
    EventStatus.IN_PROGRESS -> EventStatusDto.IN_PROGRESS
    EventStatus.COMPLETED -> EventStatusDto.COMPLETED
    EventStatus.CONTINUOUS -> EventStatusDto.CONTINUOUS
  },
  eventType = when (eventType) {
    EventType.ONLINE -> EventTypeDto.ONLINE
    EventType.OFFLINE -> EventTypeDto.OFFLINE
  },
  session = EventSessionDto(
    id = session.id,
    startedAt = session.startedAt,
    userCanJoin = session.userCanJoin,
    finishedAt = session.finishedAt,
  ),
  eventWaypoints = eventWaypoints.map { mw ->
    MapWaypointDto(
      id = mw.id,
      label = mw.label,
      position = PositionDto(
        latitude = mw.position.latitude,
        longitude = mw.position.longitude,
      ),
    )
  }
)

fun MobileEventDetailsDto.toDomain(): MobileEventDetails = MobileEventDetails(
  id = id,
  map = MobileMap(
    id = map.id,
    name = map.name,
    description = map.description,
    imageData = map.imageData,
    waypoints = map.waypoints.map { dto ->
      MapWaypoint(
        id = dto.id,
        label = dto.label,
        position = Position(
          latitude = dto.position.latitude,
          longitude = dto.position.longitude,
        ),
      )
    }
  ),
  name = name,
  description = description,
  createdAt = createdAt,
  startDate = startDate,
  startLocationX = startLocationX,
  startLocationY = startLocationY,
  eventStatus = when (eventStatus) {
    EventStatusDto.PLANNED -> EventStatus.PLANNED
    EventStatusDto.IN_PROGRESS -> EventStatus.IN_PROGRESS
    EventStatusDto.COMPLETED -> EventStatus.COMPLETED
    EventStatusDto.CONTINUOUS -> EventStatus.CONTINUOUS
  },
  eventType = when (eventType) {
    EventTypeDto.ONLINE -> EventType.ONLINE
    EventTypeDto.OFFLINE -> EventType.OFFLINE
  },
  session = EventSession(
    id = session.id,
    startedAt = session.startedAt,
    userCanJoin = session.userCanJoin,
    finishedAt = session.finishedAt,
  ),
  eventWaypoints = eventWaypoints.map { dto ->
    MapWaypoint(
      id = dto.id,
      label = dto.label,
      position = Position(
        latitude = dto.position.latitude,
        longitude = dto.position.longitude,
      ),
    )
  }
)
