package pl.dev.bkwiatkowski.technical.backend.domain.model

data class MobileSignInRequest(
  val username: String,
  val password: String,
)
