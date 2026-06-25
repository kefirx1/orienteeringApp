package pl.dev.bkwiatkowski.technical.user.domain.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.network.Token
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.user.domain.model.TokenData

interface SessionRepository {
  suspend fun saveNewTokenData(tokenData: TokenData): Either<DomainError, Unit>
  suspend fun getRefreshToken(): Either<DomainError, Token>
  suspend fun getAccessToken(): Either<DomainError, Token>
  suspend fun clearAccessToken(): Either<DomainError, Unit>
  suspend fun clearAllTokens(): Either<DomainError, Unit>
}