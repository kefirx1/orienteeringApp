package pl.dev.bkwiatkowski.feature.login.domain.usecase

import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.validators.TextValidator
import pl.dev.bkwiatkowski.common.core.validators.TextValidatorRule
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult

interface ValidateEmailUC: UseCase<ValidateEmailUC.Params, ValidationResult> {
  data class Params(
    val email: String,
  ): UseCase.Params
}

class ValidateEmailUCImpl(
  private val textValidator: TextValidator,
): ValidateEmailUC {
  override suspend fun invoke(params: ValidateEmailUC.Params): ValidationResult =
    textValidator
      .addRule(rule = TextValidatorRule.Required)
      .addRule(rule = TextValidatorRule.EmailPatter)
      .validate(value = params.email)
}