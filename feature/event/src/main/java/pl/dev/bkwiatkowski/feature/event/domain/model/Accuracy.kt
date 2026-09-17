package pl.dev.bkwiatkowski.feature.event.domain.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
enum class Accuracy {
  STRONG,
  WEAK,
  VERY_WEAK,
}