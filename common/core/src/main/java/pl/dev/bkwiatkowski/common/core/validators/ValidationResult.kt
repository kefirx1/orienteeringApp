package pl.dev.bkwiatkowski.common.core.validators

sealed interface ValidationResult {
  data object Valid : ValidationResult
  data class Invalid(val message: String) : ValidationResult
}