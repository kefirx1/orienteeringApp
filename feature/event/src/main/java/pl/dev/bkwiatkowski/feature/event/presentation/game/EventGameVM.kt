package pl.dev.bkwiatkowski.feature.event.presentation.game

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.feature.event.domain.model.SessionWaypointDetail

interface EventGameVM {
  sealed interface State {
    data class Active(
      val visitedWaypoints: List<SessionWaypointDetail>,
    ) : State

    data object Empty : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
    }

    data object Back : Action
    data class UpdateVisitedWaypoints(
      val visited: List<SessionWaypointDetail>,
    ) : Action
  }

  data class WaypointData(
    val visitedTime: String,
    val label: String,
  )

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class Empty(
      override val onBackClick: () -> Unit,
      val emptyLabel: String,
    ) : ScreenData

    data class Main(
      override val onBackClick: () -> Unit,
      val waypoints: List<WaypointData>,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel(assistedFactory = EventGameVMImpl.Factory::class)
class EventGameVMImpl @AssistedInject constructor(
  @Assisted private val contract: EventGameContract,
  private val mapper: EventGameMapper,
) : CustomViewModel<EventGameVM.State, EventGameVM.ScreenData, EventGameVM.Action.Navigation>(
  initialStateValue = run {
    val visitedWaypoints = contract.getVisitedWaypoints()

    when {
      visitedWaypoints.isEmpty() -> EventGameVM.State.Empty
      else -> EventGameVM.State.Active(
        visitedWaypoints = visitedWaypoints,
      )
    }
  },
), EventGameVM {

  @AssistedFactory
  interface Factory : CustomViewModelFactory<EventGameContract, EventGameVMImpl>

  override val screenData: StateFlow<EventGameVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: EventGameVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is EventGameVM.State.Empty -> when (action) {
          is EventGameVM.Action.Back -> EventGameVM.Action.Navigation.Back.emit()
          is EventGameVM.Action.UpdateVisitedWaypoints -> {
            if (action.visited.isNotEmpty()) {
              EventGameVM.State.Active(
                visitedWaypoints = action.visited,
              ).override()
            }
          }
          else -> {}
        }
        is EventGameVM.State.Active -> when (action) {
          is EventGameVM.Action.Back -> EventGameVM.Action.Navigation.Back.emit()
          is EventGameVM.Action.UpdateVisitedWaypoints -> {
            if (action.visited.isEmpty()) {
              EventGameVM.State.Empty.override()
            } else {
              currentState.copy(
                visitedWaypoints = action.visited,
              ).mutate()
            }
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: EventGameVM.State) {
    when (newState) {
      is EventGameVM.State.Empty -> {
        viewModelScope.launch {
          contract.visitedWaypointsMonitor().collect { visited ->
            dispatchAction(EventGameVM.Action.UpdateVisitedWaypoints(visited = visited))
          }
        }
      }
      is EventGameVM.State.Active -> {
        viewModelScope.launch {
          contract.visitedWaypointsMonitor().collect { visited ->
            dispatchAction(EventGameVM.Action.UpdateVisitedWaypoints(visited = visited))
          }
        }
      }
    }
  }

  override fun mapScreenData(): EventGameVM.ScreenData = mapper(
    params = EventGameMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(EventGameVM.Action.Back) },
    ),
  )
}
