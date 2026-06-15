package pl.dev.bkwiatkowski.common.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponsePayload(
  val businessCode: BusinessCode,
  val message: String,
) {
  @Serializable
  enum class BusinessCode(val value: String) {
    VALIDATION_FAILED("VALIDATION_FAILED"),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS"),
    USER_NOT_FOUND("USER_NOT_FOUND"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS"),
    REFRESH_TOKEN_INVALID("REFRESH_TOKEN_INVALID"),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED"),
    REFRESH_TOKEN_REUSED("REFRESH_TOKEN_REUSED"),
    UNKNOWN("UNKNOWN");

    companion object {
      fun fromValue(value: String): BusinessCode = entries.firstOrNull { it.value == value } ?: UNKNOWN
    }
  }
}
