package pl.dev.bkwiatkowski.feature.event.domain.interactor

import kotlinx.coroutines.flow.Flow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.feature.event.domain.model.FinishSessionResponse
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.UploadImageResponse
import pl.dev.bkwiatkowski.feature.event.domain.model.WaypointVisitResponse
import pl.dev.bkwiatkowski.feature.event.domain.model.WaypointsVisitedResponse
import java.time.LocalDateTime

interface EventBackendInteractor {
  fun observeSession(): Flow<WaypointVisitResponse>
  suspend fun openSession(sessionUuid: String): Either<DomainError, Unit>
  suspend fun closeSession(): Either<DomainError, Unit>
  suspend fun confirmWaypoint(
    waypointId: Int,
    visitedAt: LocalDateTime,
    imagePath: String,
  ): Either<DomainError, Unit>
  suspend fun uploadSessionImage(sessionUuid: String, imageBase64: String): Either<DomainError, UploadImageResponse>
  suspend fun getMobileEventDetails(eventId: Int): Either<DomainError, MobileEventDetails>
  suspend fun getSessionWaypoints(sessionUuid: String): Either<DomainError, WaypointsVisitedResponse>
  suspend fun finishEventSession(sessionUuid: String): Either<DomainError, FinishSessionResponse>
}