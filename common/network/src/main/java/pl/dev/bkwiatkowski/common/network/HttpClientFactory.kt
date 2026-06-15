package pl.dev.bkwiatkowski.common.network

import android.R.attr.scheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import pl.dev.bkwiatkowski.common.core.config.EnvironmentConfig
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.network.serialization.LocalDateTimeSerializer

interface HttpClientFactory {
  fun create(): HttpClient
}

class HttpClientFactoryImpl(
  private val environmentConfig: EnvironmentConfig,
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

    return HttpClient(engineFactory = Android) {
      defaultRequest {
        url(urlString = baseUrl)
      }
      install(plugin = Logging) {
        logger = object : Logger {
          override fun log(message: String) {
            Log.i(tag = Tag(this@HttpClientFactoryImpl), message = message)
          }
        }
        level = LogLevel.HEADERS
      }
      install(plugin = ContentNegotiation) {
        json(json = jsonBuilder)
      }
      install(plugin = Resources)
    }
  }
}