package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.activityconnector.ActivityConnector
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.storage.JsonSerializer
import pl.dev.bkwiatkowski.common.loader.LoaderManager
import pl.dev.bkwiatkowski.common.loader.LoaderManagerImpl
import pl.dev.bkwiatkowski.common.loader.domain.RunWithLoaderUCImpl
import pl.dev.bkwiatkowski.common.storage.coder.Base64CoderImpl
import pl.dev.bkwiatkowski.common.storage.serializer.JsonSerializerImpl
import pl.dev.bkwiatkowski.orienteeringapp.core.lifecycle.ActivityConnectorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

  @Provides
  @Singleton
  fun provideLoaderManager(): LoaderManager = LoaderManagerImpl()

  @Provides
  fun provideRunWithLoaderUC(
    loaderManager: LoaderManager,
  ): RunWithLoaderUC = RunWithLoaderUCImpl(
    loaderManager = loaderManager,
  )

  @Provides
  @Singleton
  fun provideActivityConnector(): ActivityConnector = ActivityConnectorImpl()

  @Provides
  @Singleton
  fun provideBase64Coder(): Base64Coder = Base64CoderImpl()

  @Provides
  @Singleton
  fun provideJsonSerializer(): JsonSerializer = JsonSerializerImpl()
}