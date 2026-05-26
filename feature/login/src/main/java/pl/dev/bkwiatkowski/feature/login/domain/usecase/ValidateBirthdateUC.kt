package pl.dev.bkwiatkowski.feature.login.domain.usecase

import pl.dev.bkwiatkowski.common.core.validators.ValidationResult
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.validators.DateValidator
import pl.dev.bkwiatkowski.common.core.validators.DateValidatorRule
import java.time.LocalDateTime

interface ValidateBirthdateUC: UseCase<ValidateBirthdateUC.Params, ValidationResult> {
  data class Params(
    val birthdate: LocalDateTime,
  ): UseCase.Params
}

class ValidateBirthdateUCImpl(
  private val dateValidator: DateValidator,
): ValidateBirthdateUC {
  override suspend fun invoke(params: ValidateBirthdateUC.Params): ValidationResult =
    dateValidator
      .addRule(rule = DateValidatorRule.NotFuture)
      .validate(value = params.birthdate)
}
