package pl.dev.bkwiatkowski.feature.event.presentation.map

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.camera.domain.usecase.TakePictureAndCompressUC
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.icon.ZoomImageData
import pl.dev.bkwiatkowski.feature.event.domain.interactor.EventBackendInteractor
import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.FinishSessionResponse
import java.time.LocalDateTime

interface EventMapVM {
  sealed interface State {
    data object Loading : State
    data class Active(
      val eventDetails: MobileEventDetails,
      val currentWaypoint: MapWaypoint?,
      val alreadyConfirmedWaypointId: Int? = null,
      val visitedWrongWaypoint: Boolean = false,
      val nextWaypoint: MapWaypoint?,
    ) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
      data class Completed(
        val response: FinishSessionResponse,
      ) : Navigation
    }

    data object Back : Action
    data class UpdateCurrentWaypoint(
      val waypoint: MapWaypoint?,
      val wrongWaypoint: Boolean,
    ) : Action
    data class UpdateNextWaypoint(
      val nextWaypoint: MapWaypoint?,
    ) : Action
    data object CompleteEvent : Action
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
  private val takePictureAndCompressUC: TakePictureAndCompressUC,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val eventBackendInteractor: EventBackendInteractor,
  private val base64Coder: Base64Coder,
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
            val visitedWrongWaypoint = if (currentState.alreadyConfirmedWaypointId == action.waypoint?.id) {
              false
            } else {
              action.wrongWaypoint
            }

            currentState.copy(
              currentWaypoint = action.waypoint.takeIf { !visitedWrongWaypoint },
              visitedWrongWaypoint = visitedWrongWaypoint,
              alreadyConfirmedWaypointId = null,
            ).mutate()
          }
          is EventMapVM.Action.UpdateNextWaypoint -> {
            currentState.copy(
              nextWaypoint = action.nextWaypoint,
            ).mutate()
          }
          is EventMapVM.Action.CheckWaypoint -> {
            runWithLoaderUC {
              currentState.currentWaypoint ?: return@runWithLoaderUC

              either {
                val bytes = takePictureAndCompressUC(params = UseCase.Params.Empty).getRight()

                val imageResponse = eventBackendInteractor.uploadSessionImage(
                  sessionUuid = currentState.eventDetails.session.id,
                  imageBase64 = base64Coder.encode(data = bytes).getRight(),
                ).getRight()

                eventBackendInteractor.confirmWaypoint(
                  waypointId = currentState.currentWaypoint.id,
                  visitedAt = LocalDateTime.now(),
                  imagePath = imageResponse.path,
                ).getRight()

                currentState.copy(
                  alreadyConfirmedWaypointId = currentState.currentWaypoint.id,
                ).mutate()
              }.onLeft {
                // TODO: handle error
              }
            }
          }
          is EventMapVM.Action.CompleteEvent -> {
            eventBackendInteractor.finishEventSession(
              sessionUuid = currentState.eventDetails.session.id,
            ).fold(
              onRight = { response ->
                EventMapVM.Action.Navigation.Completed(response = response).emit()
              },
              onLeft = { error ->
                // TODO: handle error
              }
            )
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
                dispatchAction(EventMapVM.Action.CompleteEvent)
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
                    waypoint = waypoint,
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
