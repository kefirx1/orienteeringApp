package pl.dev.bkwiatkowski.technical.user.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.network.SessionManager
import pl.dev.bkwiatkowski.common.core.security.CryptoManager
import pl.dev.bkwiatkowski.common.core.security.generator.AesKeyGenerator
import pl.dev.bkwiatkowski.common.core.security.generator.SecureRandomGenerator
import pl.dev.bkwiatkowski.common.core.security.provider.AppSecretKeyProvider
import pl.dev.bkwiatkowski.common.core.security.provider.MasterKeyProvider
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.storage.provider.DataStoreProvider
import pl.dev.bkwiatkowski.technical.user.data.repository.SessionRepositoryImpl
import pl.dev.bkwiatkowski.technical.user.data.repository.UserRepositoryImpl
import pl.dev.bkwiatkowski.technical.user.domain.interactor.UserBackendInteractor
import pl.dev.bkwiatkowski.technical.user.domain.repository.SessionRepository
import pl.dev.bkwiatkowski.technical.user.domain.repository.UserRepository
import pl.dev.bkwiatkowski.technical.user.domain.usecase.CreateNewLocalUserUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.CreateNewLocalUserUCImpl
import pl.dev.bkwiatkowski.technical.user.domain.usecase.CreateNewUserUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.CreateNewUserUCImpl
import pl.dev.bkwiatkowski.technical.user.domain.usecase.GetUserNameUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.GetUserNameUCImpl
import pl.dev.bkwiatkowski.technical.user.domain.usecase.HasValidRefreshTokenUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.HasValidRefreshTokenUCImpl
import pl.dev.bkwiatkowski.technical.user.domain.usecase.LoginUserLocalUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.LoginUserLocalUCImpl
import pl.dev.bkwiatkowski.technical.user.domain.usecase.LoginUserRemoteUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.LoginUserRemoteUCImpl
import pl.dev.bkwiatkowski.technical.user.domain.usecase.LoginUserToAppUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.LoginUserToAppUCImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

  @Provides
  @Singleton
  fun provideUserRepository(
    dataStoreProvider: DataStoreProvider,
  ): UserRepository = UserRepositoryImpl(
    dataStoreProvider = dataStoreProvider,
  )

  @Provides
  fun provideGetUserNameUC(
    userRepository: UserRepository,
  ): GetUserNameUC = GetUserNameUCImpl(
    userRepository = userRepository,
  )

  @Provides
  fun provideCreateNewUserUC(
    userBackendInteractor: UserBackendInteractor,
    createNewLocalUserUC: CreateNewLocalUserUC,
    sessionRepository: SessionRepository,
  ): CreateNewUserUC = CreateNewUserUCImpl(
    userBackendInteractor = userBackendInteractor,
    createNewLocalUserUC = createNewLocalUserUC,
    sessionRepository = sessionRepository
  )

  @Provides
  fun provideCreateNewLocalUserUC(
    secureRandomGenerator: SecureRandomGenerator,
    aesKeyGenerator: AesKeyGenerator,
    masterKeyProvider: MasterKeyProvider,
    cryptoManager: CryptoManager,
    base64Coder: Base64Coder,
    userRepository: UserRepository,
    appSecretKeyProvider: AppSecretKeyProvider,
  ): CreateNewLocalUserUC = CreateNewLocalUserUCImpl(
    secureRandomGenerator = secureRandomGenerator,
    aesKeyGenerator = aesKeyGenerator,
    masterKeyProvider = masterKeyProvider,
    cryptoManager = cryptoManager,
    base64Coder = base64Coder,
    userRepository = userRepository,
    appSecretKeyProvider = appSecretKeyProvider,
  )

  @Provides
  fun provideLoginUserToAppUC(
    loginUserRemoteUC: LoginUserRemoteUC,
    loginUserLocalUC: LoginUserLocalUC,
  ): LoginUserToAppUC = LoginUserToAppUCImpl(
    loginUserRemoteUC = loginUserRemoteUC,
    loginUserLocalUC = loginUserLocalUC,
  )

  @Provides
  fun provideLoginUserRemoteUC(
    sessionRepository: SessionRepository,
    backendInteractor: UserBackendInteractor,
  ): LoginUserRemoteUC = LoginUserRemoteUCImpl(
    sessionRepository = sessionRepository,
    backendInteractor = backendInteractor,
  )

  @Provides
  fun provideLoginUserLocalUC(
    userRepository: UserRepository,
    cryptoManager: CryptoManager,
    appSecretKeyProvider: AppSecretKeyProvider,
    masterKeyProvider: MasterKeyProvider,
    base64Coder: Base64Coder,
  ): LoginUserLocalUC = LoginUserLocalUCImpl(
    userRepository = userRepository,
    cryptoManager = cryptoManager,
    masterKeyProvider = masterKeyProvider,
    base64Coder = base64Coder,
    appSecretKeyProvider = appSecretKeyProvider,
  )

  @Provides
  @Singleton
  fun provideSessionRepository(
    dataStoreProvider: DataStoreProvider,
    sessionManager: SessionManager,
  ): SessionRepository = SessionRepositoryImpl(
    dataStoreProvider = dataStoreProvider,
    sessionManager = sessionManager,
  )

  @Provides
  fun provideHasValidRefreshTokenUC(
    sessionRepository: SessionRepository,
  ): HasValidRefreshTokenUC = HasValidRefreshTokenUCImpl(
    sessionRepository = sessionRepository,
  )
}
