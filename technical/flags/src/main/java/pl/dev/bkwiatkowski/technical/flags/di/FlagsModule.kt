package pl.dev.bkwiatkowski.technical.flags.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.config.EnvironmentConfig
import pl.dev.bkwiatkowski.common.core.storage.provider.DataStoreProvider
import pl.dev.bkwiatkowski.technical.flags.data.repository.FeatureFlagRepositoryImpl
import pl.dev.bkwiatkowski.technical.flags.domain.repository.FeatureFlagRepository
import pl.dev.bkwiatkowski.technical.flags.domain.usecase.GetFeatureFlagUC
import pl.dev.bkwiatkowski.technical.flags.domain.usecase.GetFeatureFlagUCImpl
import pl.dev.bkwiatkowski.technical.flags.domain.usecase.SetFeatureFlagUC
import pl.dev.bkwiatkowski.technical.flags.domain.usecase.SetFeatureFlagUCImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FlagsModule {

  @Provides
  @Singleton
  fun provideFeatureFlagRepository(
    dataStoreProvider: DataStoreProvider,
    environmentConfig: EnvironmentConfig,
  ): FeatureFlagRepository = FeatureFlagRepositoryImpl(
    dataStoreProvider = dataStoreProvider,
    environmentConfig = environmentConfig,
  )

  @Provides
  fun provideGetFeatureFlagUC(
    featureFlagRepository: FeatureFlagRepository,
  ): GetFeatureFlagUC = GetFeatureFlagUCImpl(
    featureFlagRepository = featureFlagRepository,
  )

  @Provides
  fun provideSetFeatureFlagUC(
    featureFlagRepository: FeatureFlagRepository,
  ): SetFeatureFlagUC = SetFeatureFlagUCImpl(
    featureFlagRepository = featureFlagRepository,
  )
}
