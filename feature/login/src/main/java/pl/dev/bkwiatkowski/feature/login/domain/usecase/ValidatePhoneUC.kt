package pl.dev.bkwiatkowski.feature.login.domain.usecase

import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.validators.TextValidator
import pl.dev.bkwiatkowski.common.core.validators.TextValidatorRule
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult

interface ValidatePhoneUC: UseCase<ValidatePhoneUC.Params, ValidationResult> {
  data class Params(
    val phone: String,
  ): UseCase.Params
}

class ValidatePhoneUCImpl(
  private val textValidator: TextValidator,
): ValidatePhoneUC {
  override suspend fun invoke(params: ValidatePhoneUC.Params): ValidationResult =
    if (params.phone.isBlank()) {
      ValidationResult.Valid
    } else {
      textValidator
        .addRule(rule = TextValidatorRule.Required)
        .addRule(rule = TextValidatorRule.PhonePattern)
        .validate(value = params.phone)
    }

}
