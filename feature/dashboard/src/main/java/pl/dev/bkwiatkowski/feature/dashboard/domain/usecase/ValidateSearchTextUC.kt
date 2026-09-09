package pl.dev.bkwiatkowski.feature.dashboard.domain.usecase

import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.validators.TextValidator
import pl.dev.bkwiatkowski.common.core.validators.TextValidatorRule
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult

interface ValidateSearchTextUC : UseCase<ValidateSearchTextUC.Params, ValidationResult> {
  data class Params(
    val searchText: String,
  ) : UseCase.Params
}

class ValidateSearchTextUCImpl(
  private val textValidator: TextValidator,
) : ValidateSearchTextUC {
  override suspend fun invoke(params: ValidateSearchTextUC.Params): ValidationResult =
    textValidator
      .addRule(rule = TextValidatorRule.Required)
      .validate(value = params.searchText)
}
