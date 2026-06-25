package pl.dev.bkwiatkowski.technical.user.data.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.network.SessionManager
import pl.dev.bkwiatkowski.common.core.network.Token
import pl.dev.bkwiatkowski.common.core.storage.provider.DataStoreProvider
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.technical.user.domain.model.TokenData
import pl.dev.bkwiatkowski.technical.user.domain.repository.SessionRepository

class SessionRepositoryImpl(
  private val sessionManager: SessionManager,
  private val dataStoreProvider: DataStoreProvider,
) : SessionRepository {
  companion object {
    private const val REFRESH_TOKEN_DATA_STORE_NAME = "REFRESH_TOKEN_DATA"
  }

  override suspend fun saveNewTokenData(tokenData: TokenData): Either<DomainError, Unit> = either {
    sessionManager.saveTokens(
      accessToken = tokenData.accessToken,
      refreshToken = tokenData.refreshToken,
    )

    dataStoreProvider.updateDataStoreData(
      dataStoreKey = REFRESH_TOKEN_DATA_STORE_NAME,
      data = tokenData.refreshToken,
      dataStoreKeyProvider = DataStoreProvider.DataStoreKeyProvider.MasterKey,
    ).getRight()
    Log.i(
      tag = Tag(this@SessionRepositoryImpl),
      message = "Token data saved successfully",
    )
  }

  override suspend fun getRefreshToken(): Either<DomainError, Token> = either {
    dataStoreProvider.getDataStoreData<Token>(
      dataStoreKey = REFRESH_TOKEN_DATA_STORE_NAME,
      type = Token::class.java,
      dataStoreKeyProvider = DataStoreProvider.DataStoreKeyProvider.MasterKey,
    ).getRight()
  }

  override suspend fun getAccessToken(): Either<DomainError, Token> = either {
    sessionManager.getAccessToken() ?: raise(error = DomainError.Custom(NullPointerException("Access token is null")))
  }

  override suspend fun clearAccessToken(): Either<DomainError, Unit> = either {
    sessionManager.clear()
  }

  override suspend fun clearAllTokens(): Either<DomainError, Unit> = either {
    sessionManager.clear()
    dataStoreProvider.clearDataStoreData(dataStoreKey = REFRESH_TOKEN_DATA_STORE_NAME).getRight()
  }

}