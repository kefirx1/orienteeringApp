package pl.dev.bkwiatkowski.technical.flags.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class FeatureFlag(
  val value: String,
  val debug: Boolean,
  val prod: Boolean,
) {
  DEBUG_LOCATION(
    value = "debug_location",
    debug = true,
    prod = false
  ),
}
