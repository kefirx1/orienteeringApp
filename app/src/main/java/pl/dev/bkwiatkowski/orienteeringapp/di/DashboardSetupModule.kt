package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.dashboard.domain.interactor.DashboardInteractor
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.SessionsData
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.UserSessionData
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.ChangePasswordUC
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.GetUserSessionsUC
import pl.dev.bkwiatkowski.technical.mobile.domain.repository.MobileSettingsRepository
import pl.dev.bkwiatkowski.technical.mobile.domain.usecase.FetchMobileSettingsUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.GetUserNameUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.LogoutUC

@Module
@InstallIn(SingletonComponent::class)
object DashboardSetupModule {

  @Provides
  fun provideDashboardMobileInteractor(
    fetchMobileSettingsUC: FetchMobileSettingsUC,
    getUserNameUC: GetUserNameUC,
    logoutUC: LogoutUC,
    changePasswordUC: ChangePasswordUC,
    getUserSessionsUC: GetUserSessionsUC,
    mobileSettingsRepository: MobileSettingsRepository,
  ): DashboardInteractor =
    object : DashboardInteractor {
      override suspend fun fetchMobileSettings(): Either<DomainError, Unit> =
        fetchMobileSettingsUC(UseCase.Params.Empty)

      override suspend fun getUserName(): Either<DomainError, String> =
        getUserNameUC(UseCase.Params.Empty)

      override suspend fun logout(): Either<DomainError, Unit> =
        logoutUC(params = UseCase.Params.Empty)

      override suspend fun changePassword(oldPassword: String, newPassword: String): Either<DomainError, Unit> =
        changePasswordUC(params = ChangePasswordUC.Params(oldPassword = oldPassword, newPassword = newPassword))

      override suspend fun getUserSessions(): Either<DomainError, SessionsData> = either {
        val settings = mobileSettingsRepository.getMobileSettings().getRight()
        val beList =
          getUserSessionsUC(params = GetUserSessionsUC.Params(userId = settings.userId)).getRight()

        SessionsData(
          sessions = beList.map { be ->
            UserSessionData(
              sessionUuid = be.sessionUuid,
              startedAt = be.startedAt,
              visitedWaypointsCount = be.visitedWaypointsCount,
              mapName = be.mapName,
              eventName = be.eventName,
              finishedAt = be.finishedAt,
            )
          },
        )
      }
    }
}