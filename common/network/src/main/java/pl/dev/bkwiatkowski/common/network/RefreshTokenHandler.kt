package pl.dev.bkwiatkowski.common.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.providers.BearerTokens
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either

interface RefreshTokenHandler {
  suspend fun refreshToken(
    client: HttpClient,
  ): Either<DomainError, BearerTokens>
}