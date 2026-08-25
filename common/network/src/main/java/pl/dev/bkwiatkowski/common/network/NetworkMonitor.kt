package pl.dev.bkwiatkowski.common.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.network.NetworkMonitor
import pl.dev.bkwiatkowski.common.core.network.NetworkStatus
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either

class NetworkMonitorImpl(
  private val context: Context,
) : NetworkMonitor {

  private val currentState: MutableSharedFlow<NetworkStatus> = MutableSharedFlow(
    replay = 0,
    extraBufferCapacity = 24,
  )
  private var connectivityManager: ConnectivityManager? = null

  private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: Network) {
      Log.i(tag = Tag(this@NetworkMonitorImpl), message = "network: available")
      currentState.tryEmit(value = NetworkStatus.CONNECTED)
    }

    override fun onLost(network: Network) {
      Log.i(tag = Tag(this@NetworkMonitorImpl), message = "network: lost")
      currentState.tryEmit(value = NetworkStatus.DISCONNECTED)
    }

    override fun onUnavailable() {
      Log.i(tag = Tag(this@NetworkMonitorImpl), message = "network: unavailable")
      currentState.tryEmit(value = NetworkStatus.DISCONNECTED)
    }
  }

  @SuppressLint("MissingPermission")
  override suspend fun register(): Either<DomainError, Unit> = either {
    if (connectivityManager != null) raise(error = DomainError.Custom(IllegalStateException("NetworkMonitor is already registered")))

    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    connectivityManager = cm

    val request = NetworkRequest.Builder()
      .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
      .build()

    cm.registerNetworkCallback(request, connectivityCallback)
  }.onLeft { error ->
    Log.i(tag = Tag(this@NetworkMonitorImpl), message = "network: register failed: $error")
  }

  override suspend fun unregister(): Either<DomainError, Unit> = either {
    connectivityManager?.unregisterNetworkCallback(connectivityCallback)
    connectivityManager = null
  }.onLeft { error ->
    Log.i(tag = Tag(this@NetworkMonitorImpl), message = "network: unregister failed: $error")
  }

  override fun monitor(): Flow<NetworkStatus> = currentState
}


