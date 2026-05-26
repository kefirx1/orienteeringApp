package pl.dev.bkwiatkowski.common.validators

import pl.dev.bkwiatkowski.common.core.validators.TextValidator
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult
import pl.dev.bkwiatkowski.common.core.validators.Validator
import pl.dev.bkwiatkowski.common.core.validators.ValidatorRule

class TextValidatorImpl : TextValidator {
  private val rules = mutableListOf<ValidatorRule>()

  override fun addRule(rule: ValidatorRule): Validator {
    rules.add(rule)
    return this
  }

  override fun validate(value: String): ValidationResult {
    val firstInvalid = rules.find { rule ->
      rule.check(value = value).not()
    }

    rules.clear()
    return when (firstInvalid) {
      null -> ValidationResult.Valid
      else -> ValidationResult.Invalid(message = firstInvalid.defaultMessage)
    }
  }
}
