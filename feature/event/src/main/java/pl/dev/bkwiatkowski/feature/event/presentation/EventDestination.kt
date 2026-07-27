package pl.dev.bkwiatkowski.feature.event.presentation

import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.common.core.navigation.Destination

sealed interface EventDestination : Destination {

  @Serializable
  data object EventGraph : Destination

  @Serializable
  data object EventMain : EventDestination
}

sealed interface EventResult {
  data object Back : EventResult
}
