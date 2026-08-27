package pl.dev.bkwiatkowski.feature.event.presentation.map

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.error.ErrorDataMapper
import pl.dev.bkwiatkowski.common.core.error.ErrorScreenData
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.event.domain.usecase.PublishWaypointVisitUC
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.icon.ZoomImageData
import pl.dev.bkwiatkowski.feature.event.domain.model.FinishSessionResponse
import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.usecase.ConfirmWaypointUC
import pl.dev.bkwiatkowski.feature.event.domain.usecase.FinishSessionUC

interface EventMapVM {
  sealed interface State {
    sealed interface Loading : State {
      data object Content : Loading
      data class Error(
        val errorScreenData: ErrorScreenData,
      ) : Loading
    }

    sealed interface Active : State {
      data class Content(
        val eventDetails: MobileEventDetails,
        val currentWaypoint: MapWaypoint?,
        val alreadyConfirmedWaypointId: Int? = null,
        val visitedWrongWaypoint: Boolean = false,
        val nextWaypoint: MapWaypoint?,
      ) : Active

      data class Error(
        val errorScreenData: ErrorScreenData,
        val content: Content,
      ) : Active
    }
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

    data class ErrorScreen(
      override val onBackClick: () -> Unit,
      val errorData: ErrorScreenData,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel(assistedFactory = EventMapVMImpl.Factory::class)
class EventMapVMImpl @AssistedInject constructor(
  @Assisted private val contract: EventMapContract,
  private val mapper: EventMapMapper,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val errorDataMapper: ErrorDataMapper,
  private val confirmWaypointUC: ConfirmWaypointUC,
  private val finishSessionUC: FinishSessionUC,
  private val publishWaypointVisitUC: PublishWaypointVisitUC,
) : CustomViewModel<EventMapVM.State, EventMapVM.ScreenData, EventMapVM.Action.Navigation>(
  initialStateValue = EventMapVM.State.Loading.Content,
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
        is EventMapVM.State.Loading.Content -> when (action) {
          is EventMapVM.Action.Back -> EventMapVM.Action.Navigation.Back.emit()
          else -> {}
        }

        is EventMapVM.State.Loading.Error -> when (action) {
          is EventMapVM.Action.Back -> EventMapVM.State.Loading.Content.override()
          else -> {}
        }

        is EventMapVM.State.Active.Content -> when (action) {
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
                confirmWaypointUC(
                  params = ConfirmWaypointUC.Params(
                    sessionUuid = currentState.eventDetails.session.id,
                    waypointId = currentState.currentWaypoint.id,
                  ),
                ).onRight { result ->
                  currentState.copy(
                    alreadyConfirmedWaypointId = currentState.currentWaypoint.id,
                  ).mutate()

                  when (result) {
                    is ConfirmWaypointUC.Result.Success -> {}
                    is ConfirmWaypointUC.Result.BackendFailed -> publishWaypointVisitUC(
                      params = PublishWaypointVisitUC.Params(
                        waypointId = currentState.currentWaypoint.id,
                        visitedAt = result.visitedAt,
                      ),
                    ).getRight()
                  }
                }.getRight()
              }.onLeft { error ->
                EventMapVM.State.Active.Error(
                  errorScreenData = errorDataMapper(
                    params = ErrorDataMapper.Params(
                      error = error,
                      onCloseClick = { dispatchAction(EventMapVM.Action.Back) },
                    )
                  ),
                  content = currentState,
                ).override()
              }
            }
          }
          is EventMapVM.Action.CompleteEvent -> {
            either {
               val response = finishSessionUC(
                 params = FinishSessionUC.Params(
                   sessionUuid = currentState.eventDetails.session.id,
                   eventId = currentState.eventDetails.id,
                 ),
               ).getRight()

              EventMapVM.Action.Navigation.Completed(response = response).emit()
            }.onLeft { error ->
              EventMapVM.State.Active.Error(
                errorScreenData = errorDataMapper(
                  params = ErrorDataMapper.Params(
                    error = error,
                    onCloseClick = { dispatchAction(EventMapVM.Action.Back) },
                  )
                ),
                content = currentState,
              ).override()
            }
          }
          else -> {}
        }
        is EventMapVM.State.Active.Error -> when (action) {
          is EventMapVM.Action.Back -> EventMapVM.State.Active.Content(
            eventDetails = currentState.content.eventDetails,
            currentWaypoint = currentState.content.currentWaypoint,
            alreadyConfirmedWaypointId = currentState.content.alreadyConfirmedWaypointId,
            visitedWrongWaypoint = currentState.content.visitedWrongWaypoint,
            nextWaypoint = currentState.content.nextWaypoint,
          ).override()
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: EventMapVM.State) {
    when (newState) {
      is EventMapVM.State.Loading.Content -> either {
        val details = contract.getEventDetails().getRight()

        EventMapVM.State.Active.Content(
          eventDetails = details,
          currentWaypoint = null,
          nextWaypoint = contract.getNextWaypoint(),
        ).override()
      }.onLeft { error ->
        EventMapVM.State.Loading.Error(
          errorScreenData = errorDataMapper(
            params = ErrorDataMapper.Params(
              error = error,
              onCloseClick = { dispatchAction(EventMapVM.Action.Back) },
            )
          ),
        ).override()
      }
      is EventMapVM.State.Active.Content -> {
        viewModelScope.launch {
          contract.currentWaypointMonitor().collect { waypoint ->
            val nextWaypoint = contract.getNextWaypoint()

            when {
              nextWaypoint == null -> {}
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
            if (nextWaypoint == null) {
              dispatchAction(EventMapVM.Action.CompleteEvent)
            } else {
              dispatchAction(
                EventMapVM.Action.UpdateNextWaypoint(
                  nextWaypoint = nextWaypoint,
                ),
              )
            }
          }
        }
      }
      is EventMapVM.State.Loading.Error -> {}
      is EventMapVM.State.Active.Error -> {}
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
