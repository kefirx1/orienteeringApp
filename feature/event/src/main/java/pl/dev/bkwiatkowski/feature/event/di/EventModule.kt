package pl.dev.bkwiatkowski.feature.event.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainMapper
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainMapperImpl

@Module
@InstallIn(SingletonComponent::class)
object EventModule {

  @Provides
  fun provideEventMainMapper(): EventMainMapper = EventMainMapperImpl()
}
