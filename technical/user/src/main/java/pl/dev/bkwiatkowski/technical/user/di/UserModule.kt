package pl.dev.bkwiatkowski.technical.user.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.storage.provider.DataStoreProvider
import pl.dev.bkwiatkowski.technical.user.data.repository.UserRepository
import pl.dev.bkwiatkowski.technical.user.data.repository.UserRepositoryImpl
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
}
