package pl.dev.bkwiatkowski.technical.user.domain.model

import pl.dev.bkwiatkowski.common.core.network.Token

data class TokenData(
  val accessToken: Token,
  val refreshToken: Token,
)
