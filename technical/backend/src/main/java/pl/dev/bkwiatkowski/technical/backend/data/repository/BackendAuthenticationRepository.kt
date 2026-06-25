package pl.dev.bkwiatkowski.technical.backend.data.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.network.CallMediator
import pl.dev.bkwiatkowski.common.network.HttpClientFactory
import pl.dev.bkwiatkowski.technical.backend.api.LoginMobileUser
import pl.dev.bkwiatkowski.technical.backend.api.RegisterMobileUser
import pl.dev.bkwiatkowski.technical.backend.data.MobileSignInResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.mapper.BackendMapper.toDomain
import pl.dev.bkwiatkowski.technical.backend.data.mapper.BackendMapper.toDto
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignInRequest
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignInResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignUpRequest
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendAuthenticationRepository

class BackendAuthenticationRepositoryImpl(
  private val callMediator: CallMediator,
  private val clientFactory: HttpClientFactory,
) : BackendAuthenticationRepository {

  private val client by lazy {
    clientFactory.create()
  }

  override suspend fun registerUser(
    request: MobileSignUpRequest,
  ): Either<DomainError, MobileSignInResponse> = callMediator<RegisterMobileUser> {
    client.post(resource = RegisterMobileUser) {
      setBody(request.toDto())
    }.body()
  }.mapRight { response -> response.body<MobileSignInResponseDto>().toDomain() }

  override suspend fun loginUser(request: MobileSignInRequest): Either<DomainError, MobileSignInResponse> =
    callMediator<LoginMobileUser> {
      client.post(resource = LoginMobileUser) {
        setBody(request.toDto())
      }.body()
    }.mapRight { response -> response.body<MobileSignInResponseDto>().toDomain() }
}

