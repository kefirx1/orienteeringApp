package pl.dev.bkwiatkowski.feature.event.presentation.map

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.camera.CameraManager
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.icon.ZoomImageData
import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails

interface EventMapVM {
  sealed interface State {
    data object Loading : State
    data class Active(
      val eventDetails: MobileEventDetails,
      val currentWaypoint: MapWaypoint?,
      val visitedWrongWaypoint: Boolean = false,
      val nextWaypoint: MapWaypoint?,
    ) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
    }

    data object Back : Action
    data class UpdateCurrentWaypoint(
      val waypoint: MapWaypoint?,
      val wrongWaypoint: Boolean,
    ) : Action
    data class UpdateNextWaypoint(
      val nextWaypoint: MapWaypoint?,
    ) : Action
    data object CheckWaypoint : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class Loading(
      override val onBackClick: () -> Unit,
    ) : ScreenData

    data class Main(
      override val onBackClick: () -> Unit,
      val title: String,
      val mapData: ZoomImageData?,
      val nextWaypointLabel: String,
      val wrongWaypointInfo: String?,
      val checkWaypointButton: LargeButtonData.Primary?,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel(assistedFactory = EventMapVMImpl.Factory::class)
class EventMapVMImpl @AssistedInject constructor(
  @Assisted private val contract: EventMapContract,
  private val mapper: EventMapMapper,
  private val cameraManager: CameraManager,
  private val runWithLoaderUC: RunWithLoaderUC,
) : CustomViewModel<EventMapVM.State, EventMapVM.ScreenData, EventMapVM.Action.Navigation>(
  initialStateValue = EventMapVM.State.Loading,
), EventMapVM {

  @AssistedFactory
  interface Factory : CustomViewModelFactory<EventMapContract, EventMapVMImpl>

  override val screenData: StateFlow<EventMapVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: EventMapVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is EventMapVM.State.Loading -> when (action) {
          is EventMapVM.Action.Back -> EventMapVM.Action.Navigation.Back.emit()
          else -> {}
        }
        is EventMapVM.State.Active -> when (action) {
          is EventMapVM.Action.Back -> EventMapVM.Action.Navigation.Back.emit()
          is EventMapVM.Action.UpdateCurrentWaypoint -> {
            currentState.copy(
              currentWaypoint = action.waypoint,
              visitedWrongWaypoint = action.wrongWaypoint,
            ).mutate()
          }
          is EventMapVM.Action.UpdateNextWaypoint -> {
            currentState.copy(
              nextWaypoint = action.nextWaypoint,
            ).mutate()
          }
          is EventMapVM.Action.CheckWaypoint -> {
            runWithLoaderUC {
              cameraManager.takePicture().fold(
                onRight = { uri ->
                  println(uri)
                },
                onLeft = { error ->
                  //TODO: handle error
                }
              )
            }
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: EventMapVM.State) {
    when (newState) {
      is EventMapVM.State.Loading -> either {
        val details = contract.getEventDetails().getRight()

        EventMapVM.State.Active(
          eventDetails = details,
          currentWaypoint = null,
          nextWaypoint = contract.getNextWaypoint(),
        ).override()
      }.onLeft {
        // Handle error
      }
      is EventMapVM.State.Active -> {
        viewModelScope.launch {
          contract.currentWaypointMonitor().collect { waypoint ->
            val nextWaypoint = contract.getNextWaypoint()

            when {
              nextWaypoint == null -> {
                // event completed?
              }
              waypoint == null -> {
                dispatchAction(
                  EventMapVM.Action.UpdateCurrentWaypoint(
                    waypoint = waypoint,
                    wrongWaypoint = false,
                  ),
                )
              }
              waypoint.id == nextWaypoint.id -> {
                dispatchAction(
                  EventMapVM.Action.UpdateCurrentWaypoint(
                    waypoint = waypoint,
                    wrongWaypoint = false,
                  ),
                )
              }
              waypoint.id != nextWaypoint.id -> {
                dispatchAction(
                  EventMapVM.Action.UpdateCurrentWaypoint(
                    waypoint = null,
                    wrongWaypoint = true,
                  ),
                )
              }
            }
          }
        }
        viewModelScope.launch {
          contract.nextWaypointMonitor().collect { nextWaypoint ->
            when (val current = state.value) {
              is EventMapVM.State.Active -> {
                dispatchAction(
                  EventMapVM.Action.UpdateNextWaypoint(
                    nextWaypoint = nextWaypoint,
                  ),
                )
              }
              else -> {}
            }
          }
        }
      }
    }
  }

  override fun mapScreenData(): EventMapVM.ScreenData = mapper(
    params = EventMapMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(EventMapVM.Action.Back) },
      onCheckWaypointClick = {
        dispatchAction(EventMapVM.Action.CheckWaypoint)
      },
    ),
  )
}
