package pl.dev.bkwiatkowski.technical.user.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.security.CryptoManager
import pl.dev.bkwiatkowski.common.core.security.generator.AesKeyGenerator
import pl.dev.bkwiatkowski.common.core.security.generator.SecureRandomGenerator
import pl.dev.bkwiatkowski.common.core.security.provider.MasterKeyProvider
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.storage.provider.DataStoreProvider
import pl.dev.bkwiatkowski.technical.user.data.repository.UserRepository
import pl.dev.bkwiatkowski.technical.user.data.repository.UserRepositoryImpl
import pl.dev.bkwiatkowski.technical.user.domain.interactor.UserBackendInteractor
import pl.dev.bkwiatkowski.technical.user.domain.usecase.CreateNewUserUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.CreateNewUserUCImpl
import pl.dev.bkwiatkowski.technical.user.domain.usecase.GetUserNameUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.GetUserNameUCImpl
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
    secureRandomGenerator: SecureRandomGenerator,
    aesKeyGenerator: AesKeyGenerator,
    masterKeyProvider: MasterKeyProvider,
    cryptoManager: CryptoManager,
    base64Coder: Base64Coder,
    userRepository: UserRepository,
  ): CreateNewUserUC = CreateNewUserUCImpl(
    userBackendInteractor = userBackendInteractor,
    secureRandomGenerator = secureRandomGenerator,
    aesKeyGenerator = aesKeyGenerator,
    masterKeyProvider = masterKeyProvider,
    cryptoManager = cryptoManager,
    base64Coder = base64Coder,
    userRepository = userRepository,
  )
}
