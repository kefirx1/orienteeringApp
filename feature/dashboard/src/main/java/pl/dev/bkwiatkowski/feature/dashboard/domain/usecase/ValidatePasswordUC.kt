package pl.dev.bkwiatkowski.feature.dashboard.domain.usecase

import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.validators.TextValidator
import pl.dev.bkwiatkowski.common.core.validators.TextValidatorRule
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult

interface ValidatePasswordUC : UseCase<ValidatePasswordUC.Params, ValidationResult> {
  data class Params(
    val password: String,
  ) : UseCase.Params
}

class ValidatePasswordUCImpl(
  private val textValidator: TextValidator,
) : ValidatePasswordUC {
  override suspend fun invoke(params: ValidatePasswordUC.Params): ValidationResult =
    textValidator
      .addRule(rule = TextValidatorRule.Required)
      .addRule(rule = TextValidatorRule.AtLeastOneDigit)
      .addRule(rule = TextValidatorRule.AtLeastOneSpecialCharacters)
      .addRule(rule = TextValidatorRule.AtLeastOneLowercaseLetter)
      .addRule(rule = TextValidatorRule.AtLeastOneUppercaseLetter)
      .addRule(rule = TextValidatorRule.MinLength(minLength = 8))
      .validate(value = params.password)
}
