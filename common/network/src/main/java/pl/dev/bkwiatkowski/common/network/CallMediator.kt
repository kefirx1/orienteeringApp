package pl.dev.bkwiatkowski.common.network

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.network.NetworkMonitor
import pl.dev.bkwiatkowski.common.core.network.NetworkStatus
import pl.dev.bkwiatkowski.common.core.storage.JsonSerializer
import pl.dev.bkwiatkowski.common.core.usecase.DefaultEitherException
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.network.model.ErrorResponsePayload
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

interface CallMediator {
  suspend operator fun <T> invoke(
    call: suspend () -> HttpResponse,
  ): Either<DomainError, HttpResponse>
}

class CallMediatorImpl(
  private val networkMonitor: NetworkMonitor,
  private val jsonSerializer: JsonSerializer,
) : CallMediator {

  override suspend fun <T> invoke(call: suspend () -> HttpResponse): Either<DomainError, HttpResponse> = either {
    try {
      val response = call()

      if (response.status.value !in 200..299) {
        raise(error = handleCodeError(response = response))
      }

      response
    } catch (e: CancellationException) {
      throw e
    } catch (e: ResponseException) {
      raise(error = handleCodeError(e = e))
    } catch (_: UnknownHostException) {
      raise(error = DomainError.UnavailableServer)
    } catch (_: ConnectException) {
      if (networkMonitor.getCurrentStatus() == NetworkStatus.CONNECTED) {
        raise(error = DomainError.UnavailableServer)
      } else {
        raise(error = DomainError.NoNetwork)
      }
    } catch (_: SocketTimeoutException) {
      raise(error = DomainError.UnavailableServer)
    } catch (e: DefaultEitherException) {
      raise(error = e.error)
    } catch (e: Exception) {
      raise(error = DomainError.Custom(e))
    }
  }

  private suspend fun handleCodeError(e: ResponseException): DomainError {
    return handleCodeError(response = e.response)
  }

  private suspend fun handleCodeError(response: HttpResponse): DomainError {
    val code = DomainError.Network.Code.fromValue(response.status.value)
    val body = runCatching { response.bodyAsText() }
      .getOrNull()
      ?.trim()
      ?.takeIf { it.isNotEmpty() }
    val message = body?.let(::extractMessageFromBody)

    return DomainError.Network(
      code = code,
      message = message,
    )
  }

  private fun extractMessageFromBody(
    body: String,
  ): String? = runCatching {
    jsonSerializer
      .deserialize<ErrorResponsePayload>(serializedData = body, type = ErrorResponsePayload::class.java)
      .fold(
        onLeft = { null },
        onRight = { errorResponse ->
          errorResponse.message.takeIf { errorResponse.showMessage }
        },
      )
  }.getOrElse {
    null
  }
}