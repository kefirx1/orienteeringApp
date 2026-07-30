package pl.dev.bkwiatkowski.common.network

import io.ktor.client.plugins.websocket.ClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.http.takeFrom
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pl.dev.bkwiatkowski.common.core.config.EnvironmentConfig
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either

interface WebSocketManager {
  val incoming: Flow<String>

  suspend fun connect(path: String): Either<DomainError, Unit>
  suspend fun send(text: String): Either<DomainError, Unit>
  suspend fun close(): Either<DomainError, Unit>
}

class WebSocketManagerImpl(
  private val environmentConfig: EnvironmentConfig,
  private val httpClientFactory: HttpClientFactory,
) : WebSocketManager {

  val client by lazy {
    httpClientFactory.createWebSocketClient()
  }

  private val _incoming = MutableSharedFlow<String>(
    replay = 0,
    extraBufferCapacity = 64,
  )
  override val incoming: Flow<String> = _incoming

  @Volatile
  private var currentSession: ClientWebSocketSession? = null

  private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
  private val mutex = Mutex()

  override suspend fun connect(path: String): Either<DomainError, Unit> = either {
    mutex.withLock {
      currentSession?.let { session ->
        either {
          currentSession = null
          session.close()
        }
      }

      val baseUrl = environmentConfig.baseUrl
        .replace("http://", "ws://")
        .replace("https://", "wss://")

      val normalizedPath = when {
        path.isEmpty() -> "/"
        path.startsWith("/") -> path
        else -> "/$path"
      }

      val targetUrl = baseUrl.trimEnd('/') + normalizedPath

      currentSession = client.webSocketSession {
        url {
          takeFrom(urlString = targetUrl)
        }
      }

      scope.launch {
        try {
          for (frame in currentSession!!.incoming) {
            try {
              when (frame) {
                is Frame.Text -> _incoming.tryEmit(value = frame.readText())
                is Frame.Binary -> _incoming.tryEmit(value = frame.readBytes().toString(Charsets.UTF_8))
                else -> {}
              }
            } catch (e: Exception) {
              Log.i(
                tag = Tag(this@WebSocketManagerImpl),
                message = "ws: incoming processing error: ${e.message}",
              )
            }
          }
        } catch (e: Exception) {
          Log.i(
            tag = Tag(this@WebSocketManagerImpl),
            message = "ws: incoming loop ended: ${e.message}",
          )
        } finally {
          mutex.withLock {
            currentSession?.let { session ->
              either {
                currentSession = null
                session.close()
              }
            }
          }
        }
      }
    }
  }

  override suspend fun send(text: String): Either<DomainError, Unit> = either {
    currentSession?.send(Frame.Text(text)) ?: raise(
      error = DomainError.Custom(IllegalStateException("WebSocket session is not connected")),
    )
  }

  override suspend fun close(): Either<DomainError, Unit> = either {
    mutex.withLock {
      currentSession?.let { session ->
        either {
          currentSession = null
          session.close()
        }
      }
    }
  }
}
