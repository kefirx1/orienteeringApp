package pl.dev.bkwiatkowski.common.core.network

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Token(
  val token: String,
  val expireAtTimestamp: Long,
)