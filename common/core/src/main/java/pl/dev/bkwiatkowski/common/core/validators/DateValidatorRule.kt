package pl.dev.bkwiatkowski.common.core.validators

import java.time.LocalDateTime

interface DateValidatorRule: ValidatorRule {
  fun check(value: LocalDateTime): Boolean

  data object NotFuture : DateValidatorRule {
    override val defaultMessage: String = "Data nie może być w przyszłości"
    override fun check(value: String): Boolean = true // unused for DateValidator

    override fun check(value: LocalDateTime): Boolean {
      return value <= LocalDateTime.now()
    }
  }
}
