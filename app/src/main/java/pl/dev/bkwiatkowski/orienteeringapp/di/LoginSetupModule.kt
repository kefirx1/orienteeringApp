package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.feature.login.domain.interactor.LoginUserInteractor
import pl.dev.bkwiatkowski.technical.user.domain.usecase.GetUserNameUC

@Module
@InstallIn(SingletonComponent::class)
object LoginSetupModule {

  @Provides
  fun provideLoginUserInteractor(
    getUserNameUC: GetUserNameUC,
  ): LoginUserInteractor = object : LoginUserInteractor {
    override suspend fun getSavedUserName() = getUserNameUC(params = UseCase.Params.Empty)
  }
}