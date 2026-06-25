package pl.dev.bkwiatkowski.feature.dashboard.domain.interactor

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either

interface DashboardMobileInteractor {
  suspend fun fetchMobileSettings(): Either<DomainError, Unit>
}