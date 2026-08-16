package pl.dev.bkwiatkowski.feature.event.presentation

import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.common.core.navigation.Destination

sealed interface EventDestination : Destination {

  @Serializable
  data object EventGraph : Destination

  @Serializable
  data object EventMain : EventDestination

  @Serializable
  data object EventMap : EventDestination

  @Serializable
  data object EventGame : EventDestination

  @Serializable
  data object Success : EventDestination
}

sealed interface EventResult {
  data object Back : EventResult
}
