package pl.dev.bkwiatkowski.technical.backend.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.storage.JsonSerializer
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.network.WebSocketManager
import pl.dev.bkwiatkowski.technical.backend.data.WebsocketWaypointVisitResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.mapper.BackendMapper.toDomain
import pl.dev.bkwiatkowski.technical.backend.data.mapper.BackendMapper.toDto
import pl.dev.bkwiatkowski.technical.backend.domain.model.WebsocketWaypointVisit
import pl.dev.bkwiatkowski.technical.backend.domain.model.WebsocketWaypointVisitResponse
import pl.dev.bkwiatkowski.technical.backend.domain.repository.SessionWebSocketRepository

class SessionWebSocketRepositoryImpl(
  private val webSocketManager: WebSocketManager,
  private val jsonSerializer: JsonSerializer,
) : SessionWebSocketRepository {

  override val incoming: Flow<WebsocketWaypointVisitResponse> =
    webSocketManager.incoming.mapNotNull { response ->
      jsonSerializer.deserialize<WebsocketWaypointVisitResponseDto>(
        serializedData = response,
        type = WebsocketWaypointVisitResponseDto::class.java,
      ).mapRight { response ->
        response.toDomain()
      }.getRightOrNull() ?: return@mapNotNull null
    }

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
