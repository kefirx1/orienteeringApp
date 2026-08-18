package pl.dev.bkwiatkowski.common.network

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.storage.JsonSerializer
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
  private val jsonSerializer: JsonSerializer,
) : CallMediator {

  override suspend fun <T> invoke(call: suspend () -> HttpResponse): Either<DomainError, HttpResponse> = either {
    try {
      val response = call()

      if (response.status.value !in 200..299) {
        raise(error = handleError(response = response))
      }

      response
    } catch (e: ResponseException) {
      raise(error = handleError(e = e))
    } catch (_: UnknownHostException) {
      raise(error = DomainError.NoNetwork)
    } catch (_: ConnectException) {
      raise(error = DomainError.NoNetwork)
    } catch (_: SocketTimeoutException) {
      raise(error = DomainError.NoNetwork)
    } catch (e: Exception) {
      raise(error = DomainError.Custom(e))
    }
  }

  private suspend fun handleError(e: ResponseException): DomainError {
    return handleError(response = e.response)
  }

  private suspend fun handleError(response: HttpResponse): DomainError {
    val code = DomainError.Network.Code.fromValue(response.status.value)
    val body = runCatching { response.bodyAsText() }
      .getOrNull()
      ?.trim()
      ?.takeIf { it.isNotEmpty() }
    val message = body?.let(::extractMessageFromBody) ?: body ?: response.status.description

    return DomainError.Network(
      code = code,
      message = message,
    )
  }

  private fun extractMessageFromBody(body: String): String? = runCatching {
    jsonSerializer
      .deserialize<ErrorResponsePayload>(serializedData = body, type = ErrorResponsePayload::class.java)
      .fold(
        onLeft = { null },
        onRight = { errorResponse ->
          "${errorResponse.businessCode}: ${errorResponse.message}"
        },
      )
  }.getOrElse {
    null
  }
}