package pl.dev.bkwiatkowski.feature.event.presentation.main

import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.SessionWaypointDetail

interface EventMainContract {
  sealed interface AccuracyState {
    data class WeakAccuracy(val timerExpired: Boolean) : AccuracyState
    data object VeryWeakAccuracy : AccuracyState
    data object StrongAccuracy : AccuracyState
  }

  suspend fun setEventDetails(eventDetails: MobileEventDetails)
  suspend fun setWaypointVisited(waypoint: SessionWaypointDetail)
  suspend fun setCurrentWaypoint(waypoint: MapWaypoint?)
  suspend fun setInitialVisitedWaypoints(waypoints: List<SessionWaypointDetail>)
  suspend fun setAccuracyState(state: AccuracyState)
}