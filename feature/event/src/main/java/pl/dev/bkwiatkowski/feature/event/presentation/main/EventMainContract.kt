package pl.dev.bkwiatkowski.feature.event.presentation.main

import pl.dev.bkwiatkowski.common.core.location.Position
import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails

interface EventMainContract {
  suspend fun setEventDetails(eventDetails: MobileEventDetails)
  suspend fun setCurrentUserPosition(position: Position)
  suspend fun setWaypointVisited(waypoint: MapWaypoint)
  suspend fun setCurrentWaypoint(waypoint: MapWaypoint?)
}