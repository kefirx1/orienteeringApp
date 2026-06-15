package pl.dev.bkwiatkowski.feature.login.domain.usecase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.validators.TextValidator
import pl.dev.bkwiatkowski.common.core.validators.TextValidatorRule
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult
interface ValidateConfirmPasswordUC : UseCase<ValidateConfirmPasswordUC.Params, ValidationResult> {
  data class Params(
    val password: String,
    val confirmPassword: String,
  ) : UseCase.Params
}
class ValidateConfirmPasswordUCImpl(
  private val textValidator: TextValidator,
) : ValidateConfirmPasswordUC {
  override suspend fun invoke(params: ValidateConfirmPasswordUC.Params): ValidationResult =
    textValidator
      .addRule(rule = TextValidatorRule.Required)
      .addRule(rule = TextValidatorRule.EqualPasswords(otherPassword = params.password))
      .validate(value = params.confirmPassword)
}
