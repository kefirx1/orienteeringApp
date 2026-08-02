package pl.dev.bkwiatkowski.orienteeringapp.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.activityconnector.ActivityConnector
import pl.dev.bkwiatkowski.common.core.config.EnvironmentConfig
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.localization.GpsManager
import pl.dev.bkwiatkowski.common.core.network.SessionManager
import pl.dev.bkwiatkowski.common.core.security.CryptoManager
import pl.dev.bkwiatkowski.common.core.security.generator.AesKeyGenerator
import pl.dev.bkwiatkowski.common.core.security.generator.SecureRandomGenerator
import pl.dev.bkwiatkowski.common.core.security.provider.AppSecretKeyProvider
import pl.dev.bkwiatkowski.common.core.security.provider.MasterKeyProvider
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.storage.JsonSerializer
import pl.dev.bkwiatkowski.common.core.storage.provider.DataStoreProvider
import pl.dev.bkwiatkowski.common.core.storage.provider.DatabaseProvider
import pl.dev.bkwiatkowski.common.core.time.DateFormatter
import pl.dev.bkwiatkowski.common.core.validators.DateValidator
import pl.dev.bkwiatkowski.common.core.validators.TextValidator
import pl.dev.bkwiatkowski.common.loader.LoaderManager
import pl.dev.bkwiatkowski.common.loader.LoaderManagerImpl
import pl.dev.bkwiatkowski.common.loader.domain.RunWithLoaderUCImpl
import pl.dev.bkwiatkowski.common.localization.GpsManagerImpl
import pl.dev.bkwiatkowski.common.network.CallMediator
import pl.dev.bkwiatkowski.common.network.CallMediatorImpl
import pl.dev.bkwiatkowski.common.network.HttpClientFactory
import pl.dev.bkwiatkowski.common.network.HttpClientFactoryImpl
import pl.dev.bkwiatkowski.common.network.RefreshTokenHandler
import pl.dev.bkwiatkowski.common.network.WebSocketManager
import pl.dev.bkwiatkowski.common.network.WebSocketManagerImpl
import pl.dev.bkwiatkowski.common.camera.CameraActivityConnector
import pl.dev.bkwiatkowski.common.camera.CameraManager
import pl.dev.bkwiatkowski.common.camera.CameraManagerImpl
import pl.dev.bkwiatkowski.common.permission.AppPermissionMapper
import pl.dev.bkwiatkowski.common.permission.AppPermissionMapperImpl
import pl.dev.bkwiatkowski.common.permission.PermissionManagerImpl
import pl.dev.bkwiatkowski.common.permission.PermissionsActivityConnector
import pl.dev.bkwiatkowski.common.permission.PermissionsManager
import pl.dev.bkwiatkowski.common.security.CryptoManagerImpl
import pl.dev.bkwiatkowski.common.security.MasterKeyDataStore
import pl.dev.bkwiatkowski.common.security.generator.AesKeyGeneratorImpl
import pl.dev.bkwiatkowski.common.security.provider.AppSecretKeyProviderImpl
import pl.dev.bkwiatkowski.common.security.provider.MasterKeyProviderImpl
import pl.dev.bkwiatkowski.common.security.provider.SecureRandomGeneratorImpl
import pl.dev.bkwiatkowski.common.storage.coder.Base64CoderImpl
import pl.dev.bkwiatkowski.common.storage.provider.DataStoreProviderImpl
import pl.dev.bkwiatkowski.common.storage.provider.DatabaseProviderImpl
import pl.dev.bkwiatkowski.common.storage.serializer.JsonSerializerImpl
import pl.dev.bkwiatkowski.common.time.DateFormatterImpl
import pl.dev.bkwiatkowski.common.ui.image.BitmapReader
import pl.dev.bkwiatkowski.common.ui.image.BitmapReaderImpl
import pl.dev.bkwiatkowski.common.validators.DateValidatorImpl
import pl.dev.bkwiatkowski.common.validators.TextValidatorImpl
import pl.dev.bkwiatkowski.orienteeringapp.config.EnvironmentConfigImpl
import pl.dev.bkwiatkowski.orienteeringapp.core.lifecycle.ActivityConnectorImpl
import pl.dev.bkwiatkowski.orienteeringapp.core.network.RefreshTokenHandlerImpl
import pl.dev.bkwiatkowski.orienteeringapp.core.network.SessionManagerImpl
import pl.dev.bkwiatkowski.technical.user.data.datastore.MasterKeyCacheDataStore
import pl.dev.bkwiatkowski.technical.user.domain.repository.SessionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

  @Provides
  @Singleton
  fun provideEnvironmentConfig(): EnvironmentConfig = EnvironmentConfigImpl()

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
  fun provideActivityConnector(
    permissionsActivityConnector: PermissionsActivityConnector,
    cameraActivityConnector: CameraActivityConnector,
  ): ActivityConnector = ActivityConnectorImpl(
    permissionsActivityConnector = permissionsActivityConnector,
    cameraActivityConnector = cameraActivityConnector,
  )

  @Provides
  @Singleton
  fun provideHttpClientFactory(
    environmentConfig: EnvironmentConfig,
    sessionManager: SessionManager,
    refreshTokenHandler: RefreshTokenHandler,
  ): HttpClientFactory = HttpClientFactoryImpl(
    environmentConfig = environmentConfig,
    sessionManager = sessionManager,
    refreshTokenHandler = refreshTokenHandler,
  )

  @Provides
  @Singleton
  fun provideRefreshTokenHandler(
    sessionRepository: SessionRepository,
  ): RefreshTokenHandler = RefreshTokenHandlerImpl(
    sessionRepository = sessionRepository,
  )

  @Provides
  @Singleton
  fun provideWebSocketManager(
    environmentConfig: EnvironmentConfig,
    httpClientFactory: HttpClientFactory,
  ): WebSocketManager = WebSocketManagerImpl(
    environmentConfig = environmentConfig,
    httpClientFactory = httpClientFactory,
  )

  @Provides
  @Singleton
  fun provideSessionManager(): SessionManager = SessionManagerImpl()

  @Provides
  @Singleton
  fun provideBase64Coder(): Base64Coder = Base64CoderImpl()

  @Provides
  @Singleton
  fun provideJsonSerializer(): JsonSerializer = JsonSerializerImpl()

  @Provides
  @Singleton
  fun provideBitmapReader(): BitmapReader = BitmapReaderImpl()

  @Provides
  @Singleton
  fun provideTextValidator(): TextValidator = TextValidatorImpl()

  @Provides
  @Singleton
  fun provideDateValidator(): DateValidator = DateValidatorImpl()

  @Provides
  @Singleton
  fun provideDateFormatter(): DateFormatter = DateFormatterImpl()

  @Provides
  @Singleton
  fun provideSecretKeyProvider(
    aesKeyGenerator: AesKeyGenerator,
  ): AppSecretKeyProvider = AppSecretKeyProviderImpl(
    aesKeyGenerator = aesKeyGenerator,
  )

  @Provides
  @Singleton
  fun providerAesKeyGenerator(): AesKeyGenerator = AesKeyGeneratorImpl()

  @Provides
  @Singleton
  fun provideCryptoManager(): CryptoManager = CryptoManagerImpl()

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
    appSecretKeyProvider: AppSecretKeyProvider,
    masterKeyProvider: MasterKeyProvider,
  ): DataStoreProvider = DataStoreProviderImpl(
    cryptoManager = cryptoManager,
    context = context,
    jsonSerializer = jsonSerializer,
    base64Coder = base64Coder,
    appSecretKeyProvider = appSecretKeyProvider,
    masterKeyProvider = masterKeyProvider,
  )

  @Provides
  fun provideSecureRandomProvider(): SecureRandomGenerator = SecureRandomGeneratorImpl()

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

  @Provides
  @Singleton
  fun provideCallMediator(
    jsonSerializer: JsonSerializer,
  ): CallMediator = CallMediatorImpl(
    jsonSerializer = jsonSerializer,
  )

  @Provides
  @Singleton
  fun provideGpsManager(
    context: Context,
  ): GpsManager = GpsManagerImpl(
    context = context,
    locationProvider = LocationServices.getFusedLocationProviderClient(context),
  )

  @Provides
  fun provideAppPermissionMapper(): AppPermissionMapper = AppPermissionMapperImpl()

  @Provides
  @Singleton
  fun providePermissionsManager(
    appPermissionMapper: AppPermissionMapper,
  ) = PermissionManagerImpl(
    appPermissionMapper = appPermissionMapper,
  )

  @Provides
  @Singleton
  fun provideCameraManager(
    context: Context,
  ): CameraManager = CameraManagerImpl(
    context = context,
  )
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonBinder {

  @Binds
  abstract fun bindPermissionsManager(
    permissionManagerImpl: PermissionManagerImpl,
  ): PermissionsManager

  @Binds
  abstract fun bindPermissionsActivityConnector(
    permissionManagerImpl: PermissionManagerImpl,
  ): PermissionsActivityConnector

  @Binds
  abstract fun bindCameraActivityConnector(
    cameraManagerImpl: CameraManagerImpl,
  ): CameraActivityConnector
}