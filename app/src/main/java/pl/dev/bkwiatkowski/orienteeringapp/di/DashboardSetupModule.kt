package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.feature.dashboard.domain.interactor.DashboardMobileInteractor
import pl.dev.bkwiatkowski.technical.mobile.domain.usecase.FetchMobileSettingsUC

@Module
@InstallIn(SingletonComponent::class)
object DashboardSetupModule {

  @Provides
  fun provideDashboardMobileInteractor(
    fetchMobileSettingsUC: FetchMobileSettingsUC,
  ): DashboardMobileInteractor =
    object : DashboardMobileInteractor {
      override suspend fun fetchMobileSettings(): Either<DomainError, Unit> =
        fetchMobileSettingsUC(UseCase.Params.Empty)

    }
}