package pl.dev.bkwiatkowski.technical.backend.data.repository

import kotlinx.coroutines.flow.Flow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.storage.JsonSerializer
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.network.WebSocketManager
import pl.dev.bkwiatkowski.technical.backend.data.mapper.BackendMapper.toDto
import pl.dev.bkwiatkowski.technical.backend.domain.model.WebsocketWaypointVisit
import pl.dev.bkwiatkowski.technical.backend.domain.repository.SessionWebSocketRepository

class SessionWebSocketRepositoryImpl(
  private val webSocketManager: WebSocketManager,
  private val jsonSerializer: JsonSerializer,
) : SessionWebSocketRepository {

  override val incoming: Flow<String> =
    webSocketManager.incoming

  override suspend fun openSession(sessionUuid: String): Either<DomainError, Unit> =
    webSocketManager.connect(
      path = "/api/mobile/events/sessions/$sessionUuid/ws",
    )

  override suspend fun closeSession() = webSocketManager.close()

  override suspend fun sendMessage(message: WebsocketWaypointVisit): Either<DomainError, Unit> = either {
    val dto = message.toDto()
    val text = jsonSerializer.serialize(data = dto).getRight()
    webSocketManager.send(text = text).getRight()
  }
}
