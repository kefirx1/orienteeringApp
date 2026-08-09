package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.event.domain.interactor.EventBackendInteractor
import pl.dev.bkwiatkowski.feature.event.domain.model.EventSession
import pl.dev.bkwiatkowski.feature.event.domain.model.EventStatus
import pl.dev.bkwiatkowski.feature.event.domain.model.EventType
import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileMap
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEEventStatus
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEEventType
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEMapWaypoint
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEMobileMap
import pl.dev.bkwiatkowski.technical.backend.domain.model.EventSessionResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventDetailResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.WebsocketWaypointVisit
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendEventsRepository
import pl.dev.bkwiatkowski.technical.backend.domain.repository.SessionWebSocketRepository
import java.time.LocalDateTime

@Module
@InstallIn(SingletonComponent::class)
object EventSetupModule {

  @Provides
  fun provideEventBackendInteractor(
    sessionWebSocketRepository: SessionWebSocketRepository,
    backendEventsRepository: BackendEventsRepository,
  ): EventBackendInteractor = object : EventBackendInteractor {
    override fun observeSession(): Flow<String> =
      sessionWebSocketRepository.incoming

    override suspend fun openSession(sessionUuid: String): Either<DomainError, Unit> =
      sessionWebSocketRepository.openSession(sessionUuid = sessionUuid)

    override suspend fun closeSession(): Either<DomainError, Unit> =
      sessionWebSocketRepository.closeSession()

    override suspend fun sendMessage(
      waypointId: Int,
      visitedAt: LocalDateTime,
    ): Either<DomainError, Unit> =
      sessionWebSocketRepository.sendMessage(
        message = WebsocketWaypointVisit(
          waypointId = waypointId,
          visitedAt = visitedAt,
        ),
      )

    override suspend fun getMobileEventDetails(eventId: Int): Either<DomainError, MobileEventDetails> = either {
      backendEventsRepository.getMobileEventDetails(eventId).mapRight { response ->
        response.toFeature().getRight()
      }.getRight()
    }

    fun MobileEventDetailResponse.toFeature(): Either<DomainError, MobileEventDetails> = either {
      MobileEventDetails(
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
        session = session?.toFeature()
          ?: raise(error = DomainError.Custom(IllegalStateException("Session is null"))),
        eventWaypoints = eventWaypoints.map { it.toFeature() },
      )
    }

    fun BEMobileMap.toFeature(): MobileMap = MobileMap(
      id = id,
      name = name,
      description = description,
      imageData = imageData,
      waypoints = waypoints.map { it.toFeature() }
    )

    fun BEMapWaypoint.toFeature(): MapWaypoint = MapWaypoint(
      id = id,
      label = label,
      position = position,
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

    fun EventSessionResponse.toFeature(): EventSession = EventSession(
      id = id,
      startedAt = startedAt,
      userCanJoin = userCanJoin,
      finishedAt = finishedAt
    )
  }
}