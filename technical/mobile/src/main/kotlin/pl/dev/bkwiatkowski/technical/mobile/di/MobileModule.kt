package pl.dev.bkwiatkowski.technical.mobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.technical.mobile.data.repository.MobileSettingsRepositoryImpl
import pl.dev.bkwiatkowski.technical.mobile.domain.interactor.MobileBackendInteractor
import pl.dev.bkwiatkowski.technical.mobile.domain.repository.MobileSettingsRepository
import pl.dev.bkwiatkowski.technical.mobile.domain.usecase.FetchMobileSettingsUC
import pl.dev.bkwiatkowski.technical.mobile.domain.usecase.FetchMobileSettingsUCImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MobileModule {

  @Provides
  @Singleton
  fun provideMobileSettingsRepository(): MobileSettingsRepository =
    MobileSettingsRepositoryImpl()

  @Provides
  fun provideFetchMobileSettingsUC(
    mobileSettingsRepository: MobileSettingsRepository,
    mobileBackendInteractor: MobileBackendInteractor
  ): FetchMobileSettingsUC =
    FetchMobileSettingsUCImpl(
      mobileSettingsRepository = mobileSettingsRepository,
      mobileBackendInteractor = mobileBackendInteractor,
    )
}