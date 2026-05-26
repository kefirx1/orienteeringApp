package pl.dev.bkwiatkowski.feature.login.domain.usecase

import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.validators.TextValidator
import pl.dev.bkwiatkowski.common.core.validators.TextValidatorRule
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult

interface ValidateUserNameUC: UseCase<ValidateUserNameUC.Params, ValidationResult> {
  data class Params(
    val userName: String,
  ): UseCase.Params
}

class ValidateUserNameUCImpl(
  private val textValidator: TextValidator,
): ValidateUserNameUC {
  override suspend fun invoke(params: ValidateUserNameUC.Params): ValidationResult =
    textValidator
      .addRule(rule = TextValidatorRule.Required)
      .validate(value = params.userName)
}