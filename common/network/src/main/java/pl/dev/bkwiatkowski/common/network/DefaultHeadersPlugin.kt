package pl.dev.bkwiatkowski.common.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.util.AttributeKey
import pl.dev.bkwiatkowski.common.core.device.AppInfo
import pl.dev.bkwiatkowski.common.core.device.DeviceInfo
import pl.dev.bkwiatkowski.common.core.device.SessionIdProvider

class DefaultHeadersPlugin(
  private val deviceInfo: DeviceInfo,
  private val appInfo: AppInfo,
  private val sessionIdProvider: SessionIdProvider,
) : HttpClientPlugin<Nothing, DefaultHeadersPlugin> {

  companion object {
    private const val TAG = "DefaultHeadersPlugin"
  }

  override val key = AttributeKey<DefaultHeadersPlugin>(name = TAG)

  override fun prepare(block: Nothing.() -> Unit): DefaultHeadersPlugin {
    return this
  }

  override fun install(plugin: DefaultHeadersPlugin, scope: HttpClient) {
    scope.requestPipeline.intercept(HttpRequestPipeline.State) {
      context.header("X-App-Version", appInfo.appVersion)
      context.header("X-Android-SDK-Int", deviceInfo.androidSdkInt.toString())
      context.header("X-Device-Model", deviceInfo.deviceModel)
      context.header("X-App-Package", appInfo.packageName)
      context.header("X-Session-Id", sessionIdProvider.getSessionId())
    }
  }
}
