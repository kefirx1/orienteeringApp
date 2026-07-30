package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.feature.event.domain.interactor.EventBackendInteractor
import pl.dev.bkwiatkowski.technical.backend.domain.model.WebsocketWaypointVisit
import pl.dev.bkwiatkowski.technical.backend.domain.repository.SessionWebSocketRepository
import java.time.LocalDateTime

@Module
@InstallIn(SingletonComponent::class)
object EventSetupModule {

  @Provides
  fun provideEventBackendInteractor(
    sessionWebSocketRepository: SessionWebSocketRepository,
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
  }
}