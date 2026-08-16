package pl.dev.bkwiatkowski.feature.event.presentation

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.location.Position
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.SessionWaypointDetail
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameContract
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainContract
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapContract
import javax.inject.Inject

interface EventShared {
  data class State(
    val eventDetails: MobileEventDetails?,
    val currentUserPosition: Position?,
    val nextWaypoint: MapWaypoint?,
    val currentWaypoint: MapWaypoint?,
    val visitedWaypoints: List<SessionWaypointDetail>,
  )

  sealed interface Action {
    interface Navigation : Action

    data class SetEventDetails(
      val eventDetails: MobileEventDetails,
    ) : Action

    data class SetCurrentUserPosition(
      val position: Position,
    ) : Action

    data class SetWaypointVisited(
      val waypoint: SessionWaypointDetail,
    ) : Action

    data class SetCurrentWaypoint(
      val waypoint: MapWaypoint?,
    ) : Action
  }

  data object ScreenData
}

@HiltViewModel
class EventSharedVM @Inject constructor(
) : CustomViewModel<EventShared.State, EventShared.ScreenData, EventShared.Action.Navigation>(
  initialStateValue = EventShared.State(
    eventDetails = null,
    currentUserPosition = null,
    nextWaypoint = null,
    currentWaypoint = null,
    visitedWaypoints = emptyList(),
  ),
), EventShared, EventMainContract, EventMapContract, EventGameContract {

  override suspend fun onStateEnter(newState: EventShared.State) {}

  override fun mapScreenData(): EventShared.ScreenData = EventShared.ScreenData

  init {
    initState()
  }

  fun dispatchAction(action: EventShared.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        else -> when (action) {
          is EventShared.Action.SetEventDetails -> {
            currentState.copy(
              eventDetails = action.eventDetails,
              nextWaypoint = action.eventDetails.eventWaypoints.firstOrNull(),
            ).mutate()
          }
          is EventShared.Action.SetCurrentUserPosition -> {
            currentState.copy(currentUserPosition = action.position).mutate()
          }
          is EventShared.Action.SetWaypointVisited -> {
            val updatedVisitedWaypoints = currentState.visitedWaypoints + action.waypoint
            currentState.copy(
              visitedWaypoints = updatedVisitedWaypoints,
              nextWaypoint = currentState.eventDetails?.eventWaypoints?.firstOrNull { waypoint ->
                waypoint.id !in updatedVisitedWaypoints.map { waypoint -> waypoint.waypointId }
              },
            ).mutate()
          }
          is EventShared.Action.SetCurrentWaypoint -> {
            currentState.copy(currentWaypoint = action.waypoint).mutate()
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun getEventDetails(): Either<DomainError, MobileEventDetails> = either {
    state.value.eventDetails ?: raise(error = DomainError.Custom(NullPointerException("Event details not found")))
  }

  override suspend fun getNextWaypoint(): MapWaypoint? = state.value.nextWaypoint

  override suspend fun currentWaypointMonitor(): Flow<MapWaypoint?> = state.map { currentState ->
    currentState.currentWaypoint
  }.distinctUntilChanged()

  override suspend fun nextWaypointMonitor(): Flow<MapWaypoint?> = state.map { currentState ->
    currentState.nextWaypoint
  }.distinctUntilChanged()

  override fun getVisitedWaypoints(): List<SessionWaypointDetail> = state.value.visitedWaypoints

  override fun visitedWaypointsMonitor(): Flow<List<SessionWaypointDetail>> = state.map { currentState ->
    currentState.visitedWaypoints
  }.distinctUntilChanged()

  override suspend fun setEventDetails(eventDetails: MobileEventDetails) {
    dispatchAction(EventShared.Action.SetEventDetails(eventDetails))
  }

  override suspend fun setCurrentUserPosition(position: Position) {
    dispatchAction(EventShared.Action.SetCurrentUserPosition(position = position))
  }

  override suspend fun setWaypointVisited(waypoint: SessionWaypointDetail) {
    dispatchAction(EventShared.Action.SetWaypointVisited(waypoint = waypoint))
  }

  override suspend fun setCurrentWaypoint(waypoint: MapWaypoint?) {
    dispatchAction(EventShared.Action.SetCurrentWaypoint(waypoint = waypoint))
  }

}