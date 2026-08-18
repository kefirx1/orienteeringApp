package pl.dev.bkwiatkowski.technical.backend.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendSettingsRepository

interface ChangePasswordUC : EitherUseCase<ChangePasswordUC.Params, Unit> {
  data class Params(
    val oldPassword: String,
    val newPassword: String,
  ) : UseCase.Params
}

class ChangePasswordUCImpl(
  private val backendSettingsRepository: BackendSettingsRepository,
) : ChangePasswordUC {
  override suspend fun invoke(params: ChangePasswordUC.Params): Either<DomainError, Unit> =
    backendSettingsRepository.changePassword(
      oldPassword = params.oldPassword,
      newPassword = params.newPassword,
    )
}
