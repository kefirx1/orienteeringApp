package pl.dev.bkwiatkowski.orienteeringapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.activityconnector.ActivityConnector
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.security.CryptoManager
import pl.dev.bkwiatkowski.common.core.security.provider.MasterKeyProvider
import pl.dev.bkwiatkowski.common.core.security.provider.SecretKeyProvider
import pl.dev.bkwiatkowski.common.core.security.usecase.GenerateSaltUC
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.storage.JsonSerializer
import pl.dev.bkwiatkowski.common.core.storage.provider.DatabaseProvider
import pl.dev.bkwiatkowski.common.core.storage.provider.DataStoreProvider
import pl.dev.bkwiatkowski.common.core.validators.DateValidator
import pl.dev.bkwiatkowski.common.core.validators.TextValidator
import pl.dev.bkwiatkowski.common.loader.LoaderManager
import pl.dev.bkwiatkowski.common.loader.LoaderManagerImpl
import pl.dev.bkwiatkowski.common.loader.domain.RunWithLoaderUCImpl
import pl.dev.bkwiatkowski.common.security.CryptoManagerImpl
import pl.dev.bkwiatkowski.common.security.MasterKeyDataStore
import pl.dev.bkwiatkowski.common.security.provider.MasterKeyProviderImpl
import pl.dev.bkwiatkowski.common.security.provider.SecretKeyProviderImpl
import pl.dev.bkwiatkowski.common.security.usecase.GenerateSaltUCImpl
import pl.dev.bkwiatkowski.common.storage.coder.Base64CoderImpl
import pl.dev.bkwiatkowski.common.storage.provider.DatabaseProviderImpl
import pl.dev.bkwiatkowski.common.storage.provider.DataStoreProviderImpl
import pl.dev.bkwiatkowski.common.storage.serializer.JsonSerializerImpl
import pl.dev.bkwiatkowski.common.validators.DateValidatorImpl
import pl.dev.bkwiatkowski.common.validators.TextValidatorImpl
import pl.dev.bkwiatkowski.orienteeringapp.core.lifecycle.ActivityConnectorImpl
import pl.dev.bkwiatkowski.technical.user.data.datastore.MasterKeyCacheDataStore
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

  @Provides
  @Singleton
  fun provideTextValidator(): TextValidator = TextValidatorImpl()

  @Provides
  @Singleton
  fun provideDateValidator(): DateValidator = DateValidatorImpl()

  @Provides
  @Singleton
  fun provideSecretKeyProvider(): SecretKeyProvider = SecretKeyProviderImpl()

  @Provides
  @Singleton
  fun provideCryptoManager(
    secretKeyProvider: SecretKeyProvider,
  ): CryptoManager = CryptoManagerImpl(
    secretKeyProvider = secretKeyProvider,
  )

  @Provides
  @Singleton
  fun provideDatabaseProvider(
    context: Context,
  ): DatabaseProvider = DatabaseProviderImpl(
    context = context,
  )

  @Provides
  @Singleton
  fun provideDataStoreProvider(
    cryptoManager: CryptoManager,
    context: Context,
    jsonSerializer: JsonSerializer,
    base64Coder: Base64Coder,
  ): DataStoreProvider = DataStoreProviderImpl(
    cryptoManager = cryptoManager,
    context = context,
    jsonSerializer = jsonSerializer,
    base64Coder = base64Coder,
  )

  @Provides
  fun provideGenerateSaltUC(): GenerateSaltUC = GenerateSaltUCImpl()

  @Provides
  @Singleton
  fun provideMasterKeyDataStore(): MasterKeyDataStore = MasterKeyCacheDataStore()

  @Provides
  @Singleton
  fun provideMasterKeyProvider(
    masterKeyDataStore: MasterKeyDataStore,
  ): MasterKeyProvider = MasterKeyProviderImpl(
    masterKeyDataStore = masterKeyDataStore,
  )
}