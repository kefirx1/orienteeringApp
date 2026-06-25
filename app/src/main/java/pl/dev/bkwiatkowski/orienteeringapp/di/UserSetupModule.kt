package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.network.Token
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignInRequest
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignUpRequest
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.RegisterUserUC
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.RemoteLoginUserUC
import pl.dev.bkwiatkowski.technical.user.domain.interactor.UserBackendInteractor
import pl.dev.bkwiatkowski.technical.user.domain.model.TokenData
import java.time.LocalDateTime

@Module
@InstallIn(SingletonComponent::class)
object UserSetupModule {

  @Provides
  fun provideLoginBackendInteractor(
    registerUserUC: RegisterUserUC,
    remoteLoginUserUC: RemoteLoginUserUC,
  ): UserBackendInteractor = object : UserBackendInteractor {
    override suspend fun registerUser(
      username: String,
      email: String,
      password: String,
      phoneNumber: String?,
      dateOfBirth: LocalDateTime,
    ): Either<DomainError, TokenData> = registerUserUC(
      params = RegisterUserUC.RegisterUserParams(
        request = MobileSignUpRequest(
          username = username,
          email = email,
          password = password,
          phoneNumber = phoneNumber,
          dateOfBirth = dateOfBirth,
        ),
      ),
    ).mapRight { response ->
      TokenData(
        accessToken = Token(
          token = response.accessToken,
          expireAtTimestamp = response.accessTokenExpiresTimestamp
        ),
        refreshToken = Token(
          token = response.refreshToken,
          expireAtTimestamp = response.refreshTokenExpiresTimestamp
        ),
      )
    }

    override suspend fun loginUser(
      username: String,
      password: String
    ): Either<DomainError, TokenData> = remoteLoginUserUC(
      params = RemoteLoginUserUC.Params(
        request = MobileSignInRequest(
          username = username,
          password = password
        )
      ),
    ).mapRight { response ->
      TokenData(
        accessToken = Token(
          token = response.accessToken,
          expireAtTimestamp = response.accessTokenExpiresTimestamp
        ),
        refreshToken = Token(
          token = response.refreshToken,
          expireAtTimestamp = response.refreshTokenExpiresTimestamp
        ),
      )
    }
  }
}