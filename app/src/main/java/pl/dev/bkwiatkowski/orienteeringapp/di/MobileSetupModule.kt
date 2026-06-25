package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.GetMobileSettingsUC
import pl.dev.bkwiatkowski.technical.mobile.domain.interactor.MobileBackendInteractor
import pl.dev.bkwiatkowski.technical.mobile.domain.model.MobileSettings

@Module
@InstallIn(SingletonComponent::class)
object MobileSetupModule {

  @Provides
  fun provideMobileBackendInteractor(
    getMobileSettingsUC: GetMobileSettingsUC,
  ): MobileBackendInteractor =
    object : MobileBackendInteractor {
      override suspend fun getMobileSettings(): Either<DomainError, MobileSettings> =
        getMobileSettingsUC(UseCase.Params.Empty).mapRight { response ->
          MobileSettings(
            serverLocalDateTime = response.serverLocalDateTime,
          )
        }
    }
}