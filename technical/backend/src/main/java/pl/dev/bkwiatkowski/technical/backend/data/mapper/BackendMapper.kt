package pl.dev.bkwiatkowski.technical.backend.data.mapper

import pl.dev.bkwiatkowski.common.core.location.Position
import pl.dev.bkwiatkowski.technical.backend.data.EventSessionResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.MapWaypointDto
import pl.dev.bkwiatkowski.technical.backend.data.MobileEventDetailResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.MobileEventListResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.MobileMapDto
import pl.dev.bkwiatkowski.technical.backend.data.MobileSettingsResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.MobileSignInRequestDto
import pl.dev.bkwiatkowski.technical.backend.data.MobileSignInResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.MobileSignUpRequestDto
import pl.dev.bkwiatkowski.technical.backend.data.UploadImageResponse
import pl.dev.bkwiatkowski.technical.backend.data.WebsocketWaypointVisitDto
import pl.dev.bkwiatkowski.technical.backend.data.WebsocketWaypointVisitResponseDto
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEEventStatus
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEEventType
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEMapWaypoint
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEMobileMap
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEUploadImageResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.EventSessionResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventDetailResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventListResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSettingsResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignInRequest
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignInResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignUpRequest
import pl.dev.bkwiatkowski.technical.backend.domain.model.WebsocketWaypointVisit
import pl.dev.bkwiatkowski.technical.backend.domain.model.WebsocketWaypointVisitResponse

object BackendMapper {

  fun MobileSignUpRequest.toDto(): MobileSignUpRequestDto =
    MobileSignUpRequestDto(
      username = username,
      email = email,
      password = password,
      phoneNumber = phoneNumber,
      dateOfBirth = dateOfBirth,
    )

  fun MobileSignInRequest.toDto(): MobileSignInRequestDto =
    MobileSignInRequestDto(
      username = username,
      password = password,
    )

  fun MobileSignInResponseDto.toDomain(): MobileSignInResponse =
    MobileSignInResponse(
      accessToken = accessToken,
      refreshToken = refreshToken,
      accessTokenExpiresTimestamp = accessTokenExpiresTimestamp,
      refreshTokenExpiresTimestamp = refreshTokenExpiresTimestamp,
    )

  fun MobileSettingsResponseDto.toDomain(): MobileSettingsResponse =
    MobileSettingsResponse(
      serverLocalDateTime = serverLocalDateTime,
    )

  fun MobileMapDto.toDomain(): BEMobileMap =
    BEMobileMap(
      id = id,
      name = name,
      description = description,
      imageData = imageData,
      waypoints = mapWaypoints?.map { it.toDomain() } ?: emptyList()
    )

  fun MobileEventListResponseDto.toDomain(): MobileEventListResponse =
    MobileEventListResponse(
      id = id,
      map = map.toDomain(),
      name = name,
      description = description,
      createdAt = createdAt,
      startDate = startDate,
      startLocationX = startLocationX,
      startLocationY = startLocationY,
      createdByUsername = createdByUsername,
      eventType = when (eventType) {
        MobileEventListResponseDto.EventType.ONLINE -> BEEventType.ONLINE
        MobileEventListResponseDto.EventType.OFFLINE -> BEEventType.OFFLINE
      },
    )

  fun MobileEventDetailResponseDto.toDomain(): MobileEventDetailResponse =
    MobileEventDetailResponse(
      id = id,
      map = map.toDomain(),
      name = name,
      description = description,
      createdAt = createdAt,
      startDate = startDate,
      startLocationX = startLocationX,
      startLocationY = startLocationY,
      eventStatus = when (status) {
        MobileEventDetailResponseDto.Status.PLANNED -> BEEventStatus.PLANNED
        MobileEventDetailResponseDto.Status.IN_PROGRESS -> BEEventStatus.IN_PROGRESS
        MobileEventDetailResponseDto.Status.COMPLETED -> BEEventStatus.COMPLETED
        MobileEventDetailResponseDto.Status.CONTINUOUS -> BEEventStatus.CONTINUOUS
      },
      eventType = when (eventType) {
        MobileEventDetailResponseDto.EventType.ONLINE -> BEEventType.ONLINE
        MobileEventDetailResponseDto.EventType.OFFLINE -> BEEventType.OFFLINE
      },
      finishedAt = finishedAt,
      allowOfflineTracking = allowOfflineTracking,
      session = session?.toDomain(),
      eventWaypoints = eventWaypoints?.map { it.toDomain() } ?: emptyList()
    )

  fun MapWaypointDto.toDomain(): BEMapWaypoint =
    BEMapWaypoint(
      id = id,
      label = label,
      position = Position(
        latitude =  coordinateY.toDouble(),
        longitude = coordinateX.toDouble(),
      ),
    )

  fun EventSessionResponseDto.toDomain(): EventSessionResponse =
    EventSessionResponse(
      id = id,
      startedAt = startedAt,
      finishedAt = finishedAt,
      userCanJoin = userCanJoin,
    )

  fun WebsocketWaypointVisit.toDto(): WebsocketWaypointVisitDto =
    WebsocketWaypointVisitDto(
      waypointId = waypointId,
      visitedAt = visitedAt,
      imagePath = imagePath,
    )

  fun UploadImageResponse.toDomain(): BEUploadImageResponse =
    BEUploadImageResponse(
      path = path,
    )

  fun WebsocketWaypointVisitResponseDto.toDomain(): WebsocketWaypointVisitResponse =
    WebsocketWaypointVisitResponse(
      waypointId = waypointId,
    )
}