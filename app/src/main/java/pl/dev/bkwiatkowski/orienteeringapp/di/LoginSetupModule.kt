package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.feature.login.domain.interactor.LoginUserInteractor
import pl.dev.bkwiatkowski.technical.user.domain.usecase.CreateNewUserUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.GetUserNameUC
import java.time.LocalDateTime

@Module
@InstallIn(SingletonComponent::class)
object LoginSetupModule {

  @Provides
  fun provideLoginUserInteractor(
    getUserNameUC: GetUserNameUC,
    createNewUserUC: CreateNewUserUC,
  ): LoginUserInteractor = object : LoginUserInteractor {
    override suspend fun getSavedUserName() = getUserNameUC(params = UseCase.Params.Empty)

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
  }
}