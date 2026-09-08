package pl.dev.bkwiatkowski.orienteeringapp.core.device

import pl.dev.bkwiatkowski.common.core.device.SessionIdProvider
import java.util.UUID

class SessionIdProviderImpl : SessionIdProvider {

  private val sessionId: String = UUID.randomUUID().toString()

  override fun getSessionId(): String = sessionId
}
