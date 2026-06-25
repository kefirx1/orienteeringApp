package pl.dev.bkwiatkowski.technical.mobile.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.technical.mobile.domain.interactor.MobileBackendInteractor
import pl.dev.bkwiatkowski.technical.mobile.domain.repository.MobileSettingsRepository

interface FetchMobileSettingsUC : EitherUseCase<UseCase.Params.Empty, Unit>

class FetchMobileSettingsUCImpl(
  private val mobileSettingsRepository: MobileSettingsRepository,
  private val mobileBackendInteractor: MobileBackendInteractor,
) : FetchMobileSettingsUC {
  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, Unit> = either {
    val settings = mobileBackendInteractor.getMobileSettings().getRight()

    mobileSettingsRepository.saveMobileSettings(settings = settings).getRight()
  }
}