package pl.dev.bkwiatkowski.common.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import pl.dev.bkwiatkowski.common.core.config.EnvironmentConfig
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.network.SessionManager
import pl.dev.bkwiatkowski.common.network.serialization.LocalDateTimeSerializer

interface HttpClientFactory {
  fun create(): HttpClient
  fun createWebSocketClient(): HttpClient
}

class HttpClientFactoryImpl(
  private val environmentConfig: EnvironmentConfig,
  private val sessionManager: SessionManager,
  private val refreshTokenHandler: RefreshTokenHandler,
) : HttpClientFactory {
  override fun create(): HttpClient {
    val jsonBuilder = Json {
      encodeDefaults = true
      prettyPrint = true
      isLenient = true
      ignoreUnknownKeys = true
      serializersModule = SerializersModule {
        contextual(LocalDateTimeSerializer)
      }
    }

    val baseUrl = environmentConfig.baseUrl
    return buildClient(
      installWebSockets = false,
      jsonBuilder = jsonBuilder,
      baseUrl = baseUrl,
    )
  }

  override fun createWebSocketClient(): HttpClient {
    val jsonBuilder = Json {
      encodeDefaults = true
      prettyPrint = true
      isLenient = true
      ignoreUnknownKeys = true
      serializersModule = SerializersModule {
        contextual(LocalDateTimeSerializer)
      }
    }

    val baseUrl = environmentConfig.baseUrl

    return buildClient(
      installWebSockets = true,
      jsonBuilder = jsonBuilder,
      baseUrl = baseUrl,
    )
  }

  private fun buildClient(installWebSockets: Boolean, jsonBuilder: Json, baseUrl: String): HttpClient {
    return HttpClient(engineFactory = OkHttp) {
      defaultRequest {
        url(urlString = baseUrl)
        contentType(ContentType.Application.Json)
      }
      if (installWebSockets) {
        install(WebSockets)
      }
      install(plugin = Auth) {
        bearer {
          sendWithoutRequest { request ->
            !request.url.encodedPath.endsWith("/login")
          }
          loadTokens {
            val accessToken = sessionManager.getAccessToken()?.token
            val refreshToken = sessionManager.getRefreshToken()?.token

            if (accessToken == null && refreshToken == null) return@loadTokens null

            BearerTokens(
              accessToken = accessToken ?: "",
              refreshToken = refreshToken,
            )
          }
          refreshTokens {
            refreshTokenHandler.refreshToken(
              client = this.client,
            ).getRightOrNull()
          }
        }
      }
      install(plugin = Logging) {
        logger = object : Logger {
          override fun log(message: String) {
            Log.i(tag = Tag(this@HttpClientFactoryImpl), message = message)
          }
        }
        level = LogLevel.BODY
      }
      install(plugin = ContentNegotiation) {
        json(json = jsonBuilder)
      }
      install(plugin = Resources)
    }
  }
}