package pl.dev.bkwiatkowski.common.core.network

import kotlinx.coroutines.flow.Flow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either

enum class NetworkStatus {
  CONNECTED,
  DISCONNECTED,
}

interface NetworkMonitor {
  fun monitor(): Flow<NetworkStatus>

  suspend fun register(): Either<DomainError, Unit>

  suspend fun unregister() : Either<DomainError, Unit>
}
