package pl.dev.bkwiatkowski.orienteeringapp.core.network

import pl.dev.bkwiatkowski.common.core.network.SessionManager
import pl.dev.bkwiatkowski.common.core.network.Token

class SessionManagerImpl : SessionManager {
  private var accessToken: Token? = null
  private var refreshToken: Token? = null

  override fun getAccessToken(): Token? = accessToken

  override fun getRefreshToken(): Token? = refreshToken

  override fun saveTokens(accessToken: Token, refreshToken: Token) {
    this.accessToken = accessToken
    this.refreshToken = refreshToken
  }

  override fun clear() {
    accessToken = null
    refreshToken = null
  }
}