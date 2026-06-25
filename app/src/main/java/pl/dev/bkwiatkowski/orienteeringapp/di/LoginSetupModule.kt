package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.feature.login.domain.interactor.LoginUserInteractor
import pl.dev.bkwiatkowski.technical.user.domain.usecase.CreateNewLocalUserUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.CreateNewUserUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.HasValidRefreshTokenUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.LoginUserLocalUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.LoginUserRemoteUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.LoginUserToAppUC
import java.time.LocalDateTime

@Module
@InstallIn(SingletonComponent::class)
object LoginSetupModule {

  @Provides
  fun provideLoginUserInteractor(
    createNewLocalUserUC: CreateNewLocalUserUC,
    createNewUserUC: CreateNewUserUC,
    loginUserRemoteUC: LoginUserRemoteUC,
    loginUserLocalUC: LoginUserLocalUC,
    hasValidRefreshTokenUC: HasValidRefreshTokenUC,
  ): LoginUserInteractor = object : LoginUserInteractor {
    override suspend fun initMasterKey(): Either<DomainError, Unit> =
      loginUserLocalUC(params = UseCase.Params.Empty)

    override suspend fun createNewUser(
      username: String,
      email: String,
      password: String,
      phoneNumber: String?,
      dateOfBirth: LocalDateTime,
    ): Either<DomainError, Unit> =
      createNewUserUC(
        params = CreateNewUserUC.Params(
          username = username,
          email = email,
          password = password,
          phoneNumber = phoneNumber,
          dateOfBirth = dateOfBirth,
        )
      )

    override suspend fun createNewLocalUser(
      username: String,
    ): Either<DomainError, Unit> =
      createNewLocalUserUC(
        params = CreateNewLocalUserUC.Params(
          username = username,
        )
      )

    override suspend fun loginUserRemote(
      username: String,
      password: String
    ): Either<DomainError, Unit> =
      loginUserRemoteUC(
        params = LoginUserRemoteUC.Params(
          userName = username,
          password = password,
        )
      )

    override suspend fun hasValidRefreshToken(): Either<DomainError, Boolean> =
      hasValidRefreshTokenUC(params = UseCase.Params.Empty)
  }
}