package pl.dev.bkwiatkowski.feature.dashboard.domain.interactor

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.SessionsData

interface DashboardInteractor {
  suspend fun fetchMobileSettings(): Either<DomainError, Unit>
  suspend fun getUserName(): Either<DomainError, String>
  suspend fun logout(): Either<DomainError, Unit>
  suspend fun changePassword(
    oldPassword: String,
    newPassword: String,
  ): Either<DomainError, Unit>
  suspend fun getUserSessions(): Either<DomainError, SessionsData>
  suspend fun getLastActiveSavedEvent(): Either<DomainError, MobileEventDetails>
}