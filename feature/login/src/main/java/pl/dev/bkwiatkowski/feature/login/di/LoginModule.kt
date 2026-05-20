package pl.dev.bkwiatkowski.feature.login.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.feature.login.presentation.login.mapper.LoginScreenMapper
import pl.dev.bkwiatkowski.feature.login.presentation.login.mapper.LoginScreenMapperImpl

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

  @Provides
  fun provideLoginScreenMapper(): LoginScreenMapper = LoginScreenMapperImpl()
}
