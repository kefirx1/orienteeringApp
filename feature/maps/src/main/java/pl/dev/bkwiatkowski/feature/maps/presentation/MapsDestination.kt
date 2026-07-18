package pl.dev.bkwiatkowski.feature.maps.presentation

import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.common.core.navigation.Destination

sealed interface MapsDestination : Destination {

  @Serializable
  data object MapsGraph : Destination

  @Serializable
  data object EventsMap : MapsDestination

  @Serializable
  data object EventDetails : MapsDestination
}

sealed interface MapsResult {
  data object Back : MapsResult
}