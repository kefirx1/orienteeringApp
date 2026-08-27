package pl.dev.bkwiatkowski.feature.event.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.camera.domain.usecase.TakePictureAndCompressUC
import pl.dev.bkwiatkowski.common.core.security.provider.MasterKeyProvider
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.storage.file.LocalFileManager
import pl.dev.bkwiatkowski.common.core.storage.provider.DataStoreProvider
import pl.dev.bkwiatkowski.common.core.storage.provider.DatabaseProvider
import pl.dev.bkwiatkowski.common.core.time.DateFormatter
import pl.dev.bkwiatkowski.common.storage.converter.LocalDateTimeConverter
import pl.dev.bkwiatkowski.common.ui.image.BitmapReader
import pl.dev.bkwiatkowski.feature.event.data.repository.EventRepositoryImpl
import pl.dev.bkwiatkowski.feature.event.domain.interactor.EventBackendInteractor
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository
import pl.dev.bkwiatkowski.feature.event.domain.usecase.ConfirmWaypointUC
import pl.dev.bkwiatkowski.feature.event.domain.usecase.ConfirmWaypointUCImpl
import pl.dev.bkwiatkowski.feature.event.domain.usecase.FindWaypointFromUserLocationUC
import pl.dev.bkwiatkowski.feature.event.domain.usecase.FindWaypointFromUserLocationUCImpl
import pl.dev.bkwiatkowski.feature.event.domain.usecase.FinishSessionUC
import pl.dev.bkwiatkowski.feature.event.domain.usecase.FinishSessionUCImpl
import pl.dev.bkwiatkowski.feature.event.domain.usecase.GetEventDetailsUC
import pl.dev.bkwiatkowski.feature.event.domain.usecase.GetEventDetailsUCImpl
import pl.dev.bkwiatkowski.feature.event.domain.usecase.GetSessionWaypointsUC
import pl.dev.bkwiatkowski.feature.event.domain.usecase.GetSessionWaypointsUCImpl
import pl.dev.bkwiatkowski.feature.event.domain.usecase.ObserveSessionUC
import pl.dev.bkwiatkowski.feature.event.domain.usecase.ObserveSessionUCImpl
import pl.dev.bkwiatkowski.feature.event.domain.usecase.PublishWaypointVisitUC
import pl.dev.bkwiatkowski.feature.event.domain.usecase.PublishWaypointVisitUCImpl
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameMapper
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameMapperImpl
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainMapper
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainMapperImpl
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapMapper
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapMapperImpl
import pl.dev.bkwiatkowski.feature.event.presentation.success.SuccessEventMapper
import pl.dev.bkwiatkowski.feature.event.presentation.success.SuccessEventMapperImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EventModule {

  @Provides
  fun provideEventMainMapper(): EventMainMapper = EventMainMapperImpl()

  @Provides
  fun provideEventMapMapper(bitmapReader: BitmapReader): EventMapMapper = EventMapMapperImpl(bitmapReader = bitmapReader)

  @Provides
  fun provideEventGameMapper(
    dateFormatter: DateFormatter,
  ): EventGameMapper = EventGameMapperImpl(
    dateFormatter = dateFormatter,
  )

  @Provides
  fun provideCompareUserLocationUC(): FindWaypointFromUserLocationUC = FindWaypointFromUserLocationUCImpl()

  @Provides
  fun provideSuccessEventMapper(
    dateFormatter: DateFormatter,
  ): SuccessEventMapper = SuccessEventMapperImpl(
    dateFormatter = dateFormatter,
  )

  @Provides
  @Singleton
  fun provideEventRepository(
    databaseProvider: DatabaseProvider,
    masterKeyProvider: MasterKeyProvider,
    localFileManager: LocalFileManager,
    localDateTimeConverter: LocalDateTimeConverter,
    dataStoreProvider: DataStoreProvider,
  ): EventRepository = EventRepositoryImpl(
    databaseProvider = databaseProvider,
    masterKeyProvider = masterKeyProvider,
    localFileManager = localFileManager,
    localDateTimeConverter = localDateTimeConverter,
    dataStoreProvider = dataStoreProvider,
  )

  @Provides
  fun provideConfirmWaypointUC(
    takePictureAndCompressUC: TakePictureAndCompressUC,
    eventRepository: EventRepository,
    eventBackendInteractor: EventBackendInteractor,
    base64Coder: Base64Coder,
  ): ConfirmWaypointUC = ConfirmWaypointUCImpl(
    takePictureAndCompressUC = takePictureAndCompressUC,
    eventRepository = eventRepository,
    eventBackendInteractor = eventBackendInteractor,
    base64Coder = base64Coder,
  )

  @Provides
  fun provideFinishSessionUC(
    eventRepository: EventRepository,
    eventBackendInteractor: EventBackendInteractor,
    base64Coder: Base64Coder,
  ): FinishSessionUC = FinishSessionUCImpl(
    eventRepository = eventRepository,
    eventBackendInteractor = eventBackendInteractor,
    base64Coder = base64Coder,
  )

  @Provides
  fun provideObserveSessionUC(
    eventBackendInteractor: EventBackendInteractor,
    eventRepository: EventRepository,
  ): ObserveSessionUC = ObserveSessionUCImpl(
    eventBackendInteractor = eventBackendInteractor,
    eventRepository = eventRepository,
  )

  @Provides
  fun providePublishWaypointVisitUC(
    eventRepository: EventRepository,
  ): PublishWaypointVisitUC = PublishWaypointVisitUCImpl(
    eventRepository = eventRepository,
  )

  @Provides
  fun provideGetEventDetailsUC(
    eventRepository: EventRepository,
    eventBackendInteractor: EventBackendInteractor,
  ): GetEventDetailsUC = GetEventDetailsUCImpl(
    eventRepository = eventRepository,
    eventBackendInteractor = eventBackendInteractor,
  )

  @Provides
  fun provideGetSessionWaypointsUC(
    eventRepository: EventRepository,
    eventBackendInteractor: EventBackendInteractor,
  ): GetSessionWaypointsUC = GetSessionWaypointsUCImpl(
    eventRepository = eventRepository,
    eventBackendInteractor = eventBackendInteractor,
  )
}
