package pl.dev.bkwiatkowski.feature.maps.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.time.DateFormatter
import pl.dev.bkwiatkowski.common.ui.image.BitmapReader
import pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap.EventsMapMapper
import pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap.EventsMapMapperImpl
import pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails.EventDetailsMapper
import pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails.EventDetailsMapperImpl

@Module
@InstallIn(SingletonComponent::class)
object MapsModule {

  @Provides
  fun provideEventsMapMapper(): EventsMapMapper = EventsMapMapperImpl()

  @Provides
  fun provideEventDetailsMapper(
    bitmapReader: BitmapReader,
    dateFormatter: DateFormatter,
  ): EventDetailsMapper = EventDetailsMapperImpl(
    bitmapReader = bitmapReader,
    dateFormatter = dateFormatter,
  )
}
