package pl.dev.bkwiatkowski.feature.event.domain.interactor

import kotlinx.coroutines.flow.Flow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import java.time.LocalDateTime

interface EventBackendInteractor {
  fun observeSession(): Flow<String>
  suspend fun openSession(sessionUuid: String): Either<DomainError, Unit>
  suspend fun closeSession(): Either<DomainError, Unit>
  suspend fun confirmWaypoint(
    waypointId: Int,
    visitedAt: LocalDateTime,
  ): Either<DomainError, Unit>
  suspend fun getMobileEventDetails(eventId: Int): Either<DomainError, MobileEventDetails>
}