package pl.dev.bkwiatkowski.orienteeringapp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.network.Token
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.network.RefreshTokenHandler
import pl.dev.bkwiatkowski.technical.backend.api.RefreshMobileToken
import pl.dev.bkwiatkowski.technical.backend.data.MobileRefreshTokenRequestDto
import pl.dev.bkwiatkowski.technical.backend.data.MobileSignInResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.mapper.BackendMapper.toDomain
import pl.dev.bkwiatkowski.technical.user.domain.model.TokenData
import pl.dev.bkwiatkowski.technical.user.domain.repository.SessionRepository

class RefreshTokenHandlerImpl(
  private val sessionRepository: SessionRepository,
) : RefreshTokenHandler {

  override suspend fun refreshToken(
    client: HttpClient,
  ): Either<DomainError, BearerTokens> = either {
    val refreshToken = sessionRepository.getRefreshToken().getRight()

    val response = client.post(resource = RefreshMobileToken) {
      setBody(MobileRefreshTokenRequestDto(refreshToken = refreshToken.token))
    }

    if (response.status.value !in 200..299) {
      raise(error = DomainError.Custom(e = IllegalStateException("Failed to refresh token, status code: ${response.status.value}")))
    }

    val newTokens = response.body<MobileSignInResponseDto>().toDomain()

    sessionRepository.saveNewTokenData(
      tokenData = TokenData(
        accessToken = Token(
          token = newTokens.accessToken,
          expireAtTimestamp = newTokens.accessTokenExpiresTimestamp,
        ),
        refreshToken = Token(
          token = newTokens.refreshToken,
          expireAtTimestamp = newTokens.refreshTokenExpiresTimestamp,
        ),
      ),
    )

    BearerTokens(
      accessToken = newTokens.accessToken,
      refreshToken = newTokens.refreshToken,
    )
  }
}