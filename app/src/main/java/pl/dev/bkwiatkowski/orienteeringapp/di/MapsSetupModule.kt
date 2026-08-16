package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.feature.maps.domain.interactor.MapsBackendInteractor
import pl.dev.bkwiatkowski.feature.maps.domain.model.EventSession
import pl.dev.bkwiatkowski.feature.maps.domain.model.EventStatus
import pl.dev.bkwiatkowski.feature.maps.domain.model.EventType
import pl.dev.bkwiatkowski.feature.maps.domain.model.MapWaypoint
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEventListDetails
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEvents
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileMap
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEEventStatus
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEEventType
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEMapWaypoint
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEMobileMap
import pl.dev.bkwiatkowski.technical.backend.domain.model.EventSessionResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventDetailResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventListResponse
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendEventsRepository
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEUserSessionStatus
import pl.dev.bkwiatkowski.technical.backend.domain.model.BESessionParticipant
import pl.dev.bkwiatkowski.feature.maps.domain.model.SessionParticipant
import pl.dev.bkwiatkowski.feature.maps.domain.model.UserSessionStatus

@Module
@InstallIn(SingletonComponent::class)
object MapsSetupModule {

  @Provides
  fun provideMapsBackendInteractor(
    backendEventsRepository: BackendEventsRepository,
  ): MapsBackendInteractor = object : MapsBackendInteractor {

    override suspend fun getMobileEvents() =
      backendEventsRepository.getMobileEvents().mapRight { response ->
        MobileEvents(
          events = response.map { event ->
            event.toFeature()
          }
        )
      }

    override suspend fun getMobileEventDetails(eventId: Int) =
      backendEventsRepository.getMobileEventDetails(eventId).mapRight { response ->
        response.toFeature()
      }

    override suspend fun joinEventSession(sessionUuid: String) =
      backendEventsRepository.joinEventSession(sessionUuid)

    override suspend fun checkUserInEventSession(sessionUuid: String) =
      backendEventsRepository.checkUserInEventSession(sessionUuid).mapRight { beStatus ->
        when (beStatus) {
          BEUserSessionStatus.JOINED -> UserSessionStatus.JOINED
          BEUserSessionStatus.NOT_JOINED -> UserSessionStatus.NOT_JOINED
          BEUserSessionStatus.FINISHED -> UserSessionStatus.FINISHED
        }
      }

    override suspend fun getSessionParticipantForUser(sessionUuid: String) =
      backendEventsRepository.getSessionParticipantForUser(sessionUuid = sessionUuid).mapRight { response ->
        response.toFeature()
      }

    fun BEMobileMap.toFeature(): MobileMap = MobileMap(
      id = id,
      name = name,
      description = description,
      imageData = imageData,
      waypoints = waypoints.map { it.toFeature() },
    )

    fun BEEventType.toFeature(): EventType = when (this) {
      BEEventType.ONLINE -> EventType.ONLINE
      BEEventType.OFFLINE -> EventType.OFFLINE
    }

    fun BEEventStatus.toFeature(): EventStatus = when (this) {
      BEEventStatus.PLANNED -> EventStatus.PLANNED
      BEEventStatus.IN_PROGRESS -> EventStatus.IN_PROGRESS
      BEEventStatus.COMPLETED -> EventStatus.COMPLETED
      BEEventStatus.CONTINUOUS -> EventStatus.CONTINUOUS
    }

    fun MobileEventListResponse.toFeature(): MobileEventListDetails = MobileEventListDetails(
      id = id,
      map = map.toFeature(),
      name = name,
      description = description,
      createdAt = createdAt,
      startDate = startDate,
      startLocationX = startLocationX,
      startLocationY = startLocationY,
      createdByUsername = createdByUsername,
      eventType = eventType.toFeature(),
    )

    fun MobileEventDetailResponse.toFeature(): MobileEventDetails = MobileEventDetails(
      id = id,
      map = map.toFeature(),
      name = name,
      description = description,
      createdAt = createdAt,
      startDate = startDate,
      startLocationX = startLocationX,
      startLocationY = startLocationY,
      eventStatus = eventStatus.toFeature(),
      eventType = eventType.toFeature(),
      finishedAt = finishedAt,
      allowOfflineTracking = allowOfflineTracking,
      session = session?.toFeature(),
      eventWaypoints = eventWaypoints.map { it.toFeature() },
    )

    fun BEMapWaypoint.toFeature(): MapWaypoint = MapWaypoint(
      id = id,
      label = label,
      position = position,
    )


    fun EventSessionResponse.toFeature(): EventSession = EventSession(
      id = id,
      startedAt = startedAt,
      userCanJoin = userCanJoin,
      finishedAt = finishedAt
    )

    fun BESessionParticipant.toFeature(): SessionParticipant = SessionParticipant(
      sessionUuid = this.sessionUuid,
      joinedAt = this.joinedAt,
      finishedAt = this.finishedAt,
    )
  }
}