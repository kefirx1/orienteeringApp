package pl.dev.bkwiatkowski.feature.dashboard.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardMapperImpl

@Module
@InstallIn(SingletonComponent::class)
object DashboardModule {

  @Provides
  fun provideDashboardMapper(): MainDashboardMapper = MainDashboardMapperImpl()
}