package pl.dev.bkwiatkowski.common.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponsePayload(
  val showMessage: Boolean,
  val businessCode: String,
  val message: String,
)
