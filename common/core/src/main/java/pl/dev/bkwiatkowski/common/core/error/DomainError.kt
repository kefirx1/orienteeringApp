package pl.dev.bkwiatkowski.common.core.error

sealed interface DomainError {
  data class Custom(
    val e: Throwable? = null,
  ): DomainError
}