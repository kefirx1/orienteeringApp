package pl.dev.bkwiatkowski.technical.backend.data.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.network.CallMediator
import pl.dev.bkwiatkowski.common.network.HttpClientFactory
import pl.dev.bkwiatkowski.technical.backend.api.GetUserSessions
import pl.dev.bkwiatkowski.technical.backend.data.UserSessionsResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.mapper.BackendMapper.toDomain
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEUserSession
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendUserRepository

class BackendUserRepositoryImpl(
  private val callMediator: CallMediator,
  private val clientFactory: HttpClientFactory,
) : BackendUserRepository {

  private val client by lazy { clientFactory.create() }

  override suspend fun getUserSessions(userId: Int): Either<DomainError, List<BEUserSession>> =
    callMediator<GetUserSessions> {
      client.get(resource = GetUserSessions(userId = userId)).body()
    }.mapRight { response ->
      response.body<UserSessionsResponseDto>().sessions.map { it.toDomain() }
    }
}
