package pl.dev.bkwiatkowski.feature.event.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainMapper
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainMapperImpl
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapMapper
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapMapperImpl
import pl.dev.bkwiatkowski.common.ui.image.BitmapReader
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameMapper
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameMapperImpl

@Module
@InstallIn(SingletonComponent::class)
object EventModule {

  @Provides
  fun provideEventMainMapper(): EventMainMapper = EventMainMapperImpl()

  @Provides
  fun provideEventMapMapper(bitmapReader: BitmapReader): EventMapMapper = EventMapMapperImpl(bitmapReader = bitmapReader)

  @Provides
  fun provideEventGameMapper(): EventGameMapper = EventGameMapperImpl()
}
