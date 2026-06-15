package pl.dev.bkwiatkowski.common.core.error

sealed interface DomainError {
  data class Custom(
    val e: Throwable? = null,
  ) : DomainError

  data class Network(
    val code: Code,
    val message: String? = null,
  ) : DomainError {
    enum class Code(val value: Int) {
      BAD_REQUEST(400),
      UNAUTHORIZED(401),
      FORBIDDEN(403),
      NOT_FOUND(404),
      METHOD_NOT_ALLOWED(405),
      CONFLICT(409),
      TOO_MANY_REQUESTS(429),
      UNPROCESSABLE_ENTITY(422),
      INTERNAL_SERVER_ERROR(500),
      BAD_GATEWAY(502),
      SERVICE_UNAVAILABLE(503),
      GATEWAY_TIMEOUT(504),
      UNKNOWN(-1),
      ;

      companion object {
        fun fromValue(value: Int): Code = entries.firstOrNull { it.value == value } ?: UNKNOWN
      }
    }
  }
}