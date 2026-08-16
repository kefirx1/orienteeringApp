package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
import pl.dev.bkwiatkowski.feature.event.domain.model.SessionWaypointDetail
import pl.dev.bkwiatkowski.feature.event.domain.model.UploadImageResponse
import pl.dev.bkwiatkowski.feature.event.domain.model.WaypointVisitResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEEventStatus
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEEventType
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEMapWaypoint
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEMobileMap
import pl.dev.bkwiatkowski.technical.backend.domain.model.EventSessionResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventDetailResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.WebsocketWaypointVisit
import pl.dev.bkwiatkowski.technical.backend.domain.model.WebsocketWaypointVisitResponse
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
    override fun observeSession(): Flow<WaypointVisitResponse> =
      sessionWebSocketRepository.incoming.map { it.toFeature() }

    override suspend fun openSession(sessionUuid: String): Either<DomainError, Unit> =
      sessionWebSocketRepository.openSession(sessionUuid = sessionUuid)

    override suspend fun closeSession(): Either<DomainError, Unit> =
      sessionWebSocketRepository.closeSession()

    override suspend fun confirmWaypoint(
      waypointId: Int,
      visitedAt: LocalDateTime,
      imagePath: String,
    ): Either<DomainError, Unit> =
      sessionWebSocketRepository.sendMessage(
        message = WebsocketWaypointVisit(
          waypointId = waypointId,
          visitedAt = visitedAt,
          imagePath = imagePath,
        ),
      )

    override suspend fun uploadSessionImage(
      sessionUuid: String,
      imageBase64: String
    ): Either<DomainError, UploadImageResponse> = backendEventsRepository.uploadSessionImage(
      sessionUuid = sessionUuid,
      imageBase64 = imageBase64,
    ).mapRight { response ->
      UploadImageResponse(
        path = response.path,
      )
    }

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

    fun WebsocketWaypointVisitResponse.toFeature(): WaypointVisitResponse = WaypointVisitResponse(
      lastVisitedWaypoint = SessionWaypointDetail(
        id = this.lastVisitedWaypoint.id,
        waypointId = this.lastVisitedWaypoint.waypointId,
        visitedAt = this.lastVisitedWaypoint.visitedAt,
      )
    )
  }
}