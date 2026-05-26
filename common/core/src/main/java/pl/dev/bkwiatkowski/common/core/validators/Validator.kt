package pl.dev.bkwiatkowski.common.core.validators

interface Validator {
  fun addRule(rule: ValidatorRule): Validator
  fun validate(value: String): ValidationResult
}

interface TextValidator : Validator