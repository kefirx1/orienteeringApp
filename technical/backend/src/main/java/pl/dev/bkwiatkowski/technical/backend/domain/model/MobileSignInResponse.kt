package pl.dev.bkwiatkowski.technical.backend.domain.model

data class MobileSignInResponse(
  val accessToken: String,
  val refreshToken: String,
  val accessTokenExpiresTimestamp: Long,
  val refreshTokenExpiresTimestamp: Long
)
