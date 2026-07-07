package pl.dev.bkwiatkowski.feature.maps.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap.EventsMapMapper
import pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap.EventsMapMapperImpl

@Module
@InstallIn(SingletonComponent::class)
object MapsModule {

  @Provides
  fun provideEventsMapMapper(): EventsMapMapper = EventsMapMapperImpl()
}
