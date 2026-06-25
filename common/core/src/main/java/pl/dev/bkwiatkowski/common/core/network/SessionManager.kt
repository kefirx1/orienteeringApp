package pl.dev.bkwiatkowski.common.core.network

interface SessionManager {
  fun getAccessToken(): Token?
  fun getRefreshToken(): Token?
  fun saveTokens(accessToken: Token, refreshToken: Token)
  fun clear()
}