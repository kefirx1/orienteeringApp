package pl.dev.bkwiatkowski.common.core.validators

import java.time.LocalDateTime

interface DateValidator {
  fun addRule(rule: DateValidatorRule): DateValidator
  fun validate(value: LocalDateTime): ValidationResult
}
