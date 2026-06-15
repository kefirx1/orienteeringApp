package pl.dev.bkwiatkowski.technical.backend.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.network.CallMediator
import pl.dev.bkwiatkowski.common.network.HttpClientFactory
import pl.dev.bkwiatkowski.technical.backend.data.repository.BackendRepository
import pl.dev.bkwiatkowski.technical.backend.data.repository.BackendRepositoryImpl
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.RegisterUserUC
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.RegisterUserUCImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BackendModule {

  @Provides
  @Singleton
  fun provideBackendRepository(
    callMediator: CallMediator,
    httpClientFactory: HttpClientFactory,
  ): BackendRepository = BackendRepositoryImpl(
    callMediator = callMediator,
    clientFactory = httpClientFactory,
  )

  @Provides
  fun provideRegisterUserUC(
    backendRepository: BackendRepository,
  ): RegisterUserUC = RegisterUserUCImpl(
    backendRepository = backendRepository,
  )
}
