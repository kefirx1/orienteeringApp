package pl.dev.bkwiatkowski.technical.backend.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.backend.domain.model.WebsocketWaypointVisit
import pl.dev.bkwiatkowski.technical.backend.domain.model.WebsocketWaypointVisitResponse

interface SessionWebSocketRepository {
  val incoming: Flow<WebsocketWaypointVisitResponse>

  suspend fun openSession(sessionUuid: String): Either<DomainError, Unit>
  suspend fun closeSession(): Either<DomainError, Unit>
  suspend fun sendMessage(message: WebsocketWaypointVisit): Either<DomainError, Unit>
}