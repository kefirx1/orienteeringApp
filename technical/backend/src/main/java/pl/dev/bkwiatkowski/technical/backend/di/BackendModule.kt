package pl.dev.bkwiatkowski.technical.backend.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.network.CallMediator
import pl.dev.bkwiatkowski.common.network.HttpClientFactory
import pl.dev.bkwiatkowski.common.network.WebSocketManager
import pl.dev.bkwiatkowski.common.core.storage.JsonSerializer
import pl.dev.bkwiatkowski.technical.backend.data.repository.BackendAuthenticationRepositoryImpl
import pl.dev.bkwiatkowski.technical.backend.data.repository.BackendSettingsRepositoryImpl
import pl.dev.bkwiatkowski.technical.backend.data.repository.BackendEventsRepositoryImpl
import pl.dev.bkwiatkowski.technical.backend.data.repository.SessionWebSocketRepositoryImpl
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendAuthenticationRepository
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendSettingsRepository
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendEventsRepository
import pl.dev.bkwiatkowski.technical.backend.domain.repository.SessionWebSocketRepository
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.GetMobileSettingsUC
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.GetMobileSettingsUCImpl
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.RegisterUserUC
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.RegisterUserUCImpl
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.RemoteLoginUserUC
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.RemoteLoginUserUCImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BackendModule {

  @Provides
  @Singleton
  fun provideBackendRepository(
    callMediator: CallMediator,
    httpClientFactory: HttpClientFactory,
  ): BackendAuthenticationRepository = BackendAuthenticationRepositoryImpl(
    callMediator = callMediator,
    clientFactory = httpClientFactory,
  )

  @Provides
  @Singleton
  fun provideBackendSettingsRepository(
    callMediator: CallMediator,
    httpClientFactory: HttpClientFactory,
  ): BackendSettingsRepository = BackendSettingsRepositoryImpl(
    callMediator = callMediator,
    clientFactory = httpClientFactory,
  )

  @Provides
  @Singleton
  fun provideBackendEventsRepository(
    callMediator: CallMediator,
    httpClientFactory: HttpClientFactory,
  ): BackendEventsRepository = BackendEventsRepositoryImpl(
    callMediator = callMediator,
    clientFactory = httpClientFactory,
  )

  @Provides
  fun provideRegisterUserUC(
    backendAuthenticationRepository: BackendAuthenticationRepository,
  ): RegisterUserUC = RegisterUserUCImpl(
    backendAuthenticationRepository = backendAuthenticationRepository,
  )

  @Provides
  fun provideLoginUserUC(
    backendAuthenticationRepository: BackendAuthenticationRepository,
  ): RemoteLoginUserUC = RemoteLoginUserUCImpl(
    backendAuthenticationRepository = backendAuthenticationRepository,
  )

  @Provides
  fun provideGetMobileSettingsUC(
    backendSettingsRepository: BackendSettingsRepository,
  ): GetMobileSettingsUC = GetMobileSettingsUCImpl(
    backendSettingsRepository = backendSettingsRepository,
  )

  @Provides
  @Singleton
  fun provideSessionWebSocketRepository(
    webSocketManager: WebSocketManager,
    jsonSerializer: JsonSerializer,
  ): SessionWebSocketRepository = SessionWebSocketRepositoryImpl(
    webSocketManager = webSocketManager,
    jsonSerializer = jsonSerializer,
  )
}
