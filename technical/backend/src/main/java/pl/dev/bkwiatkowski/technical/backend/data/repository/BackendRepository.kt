package pl.dev.bkwiatkowski.technical.backend.data.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.network.CallMediator
import pl.dev.bkwiatkowski.common.network.HttpClientFactory
import pl.dev.bkwiatkowski.technical.backend.api.RegisterMobileUser
import pl.dev.bkwiatkowski.technical.backend.data.mapper.BackendMapper.toDto
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignUpRequest
import kotlin.getValue

interface BackendRepository {
  suspend fun registerUser(
    request: MobileSignUpRequest,
  ): Either<DomainError, Unit>
}

class BackendRepositoryImpl(
  private val callMediator: CallMediator,
  private val clientFactory: HttpClientFactory,
) : BackendRepository {

  private val client by lazy {
    clientFactory.create()
  }

  override suspend fun registerUser(
    request: MobileSignUpRequest,
  ): Either<DomainError, Unit> = callMediator<RegisterMobileUser> {
    client.post(resource = RegisterMobileUser) {
      header(HttpHeaders.ContentType, "application/json")
      setBody(request.toDto())
    }.body()
  }.mapRight { }
}

