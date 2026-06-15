package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.feature.login.domain.interactor.LoginBackendInteractor
import pl.dev.bkwiatkowski.feature.login.domain.interactor.LoginUserInteractor
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignUpRequest
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.RegisterUserUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.GetUserNameUC
import java.time.LocalDateTime

@Module
@InstallIn(SingletonComponent::class)
object LoginSetupModule {

  @Provides
  fun provideLoginUserInteractor(
    getUserNameUC: GetUserNameUC,
  ): LoginUserInteractor = object : LoginUserInteractor {
    override suspend fun getSavedUserName() = getUserNameUC(params = UseCase.Params.Empty)
  }

  @Provides
  fun provideLoginBackendInteractor(
    registerUserUC: RegisterUserUC,
  ): LoginBackendInteractor = object : LoginBackendInteractor {
    override suspend fun registerUser(
      username: String,
      email: String,
      password: String,
      phoneNumber: String?,
      dateOfBirth: LocalDateTime,
    ): Either<DomainError, Unit> = registerUserUC(
      params = RegisterUserUC.RegisterUserParams(
        request = MobileSignUpRequest(
          username = username,
          email = email,
          password = password,
          phoneNumber = phoneNumber,
          dateOfBirth = dateOfBirth,
        ),
      ),
    )
  }
}