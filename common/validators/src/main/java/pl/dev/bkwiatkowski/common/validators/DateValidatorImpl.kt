package pl.dev.bkwiatkowski.common.validators

import pl.dev.bkwiatkowski.common.core.validators.DateValidator
import pl.dev.bkwiatkowski.common.core.validators.DateValidatorRule
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult
import java.time.LocalDateTime

class DateValidatorImpl : DateValidator {
  private val rules = mutableListOf<DateValidatorRule>()

  override fun addRule(rule: DateValidatorRule): DateValidator {
    rules.add(rule)
    return this
  }

  override fun validate(value: LocalDateTime): ValidationResult {
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
