package pl.dev.bkwiatkowski.feature.event.presentation.success

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.event.domain.model.FinishSessionResponse
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameVM

interface SuccessEventVM {
  sealed interface State {
    data class Active(
      val eventName: String,
      val finishResponse: FinishSessionResponse,
    ) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
    }

    data object Back : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class Main(
      override val onBackClick: () -> Unit,
      val topAppBarData: TopAppBarData,
      val description: String,
      val startDateTime: String,
      val finishDateTime: String,
      val waypoints: List<EventGameVM.WaypointData>,
      val closButtonData: LargeButtonData.Primary,
    ) : ScreenData
  }

  data class SetupData(
    val eventName: String,
    val finishSessionResponse: FinishSessionResponse,
  )

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel(assistedFactory = SuccessEventVMImpl.Factory::class)
class SuccessEventVMImpl @AssistedInject constructor(
  @Assisted private val setupData: SuccessEventVM.SetupData,
  private val mapper: SuccessEventMapper,
) : CustomViewModel<SuccessEventVM.State, SuccessEventVM.ScreenData, SuccessEventVM.Action.Navigation>(
  initialStateValue = SuccessEventVM.State.Active(
    finishResponse = setupData.finishSessionResponse,
    eventName = setupData.eventName,
  ),
), SuccessEventVM {

  override val screenData: StateFlow<SuccessEventVM.ScreenData> = _screenData

  @AssistedFactory
  interface Factory : CustomViewModelFactory<SuccessEventVM.SetupData, SuccessEventVMImpl>

  init {
    initState()
  }

  fun dispatchAction(action: SuccessEventVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is SuccessEventVM.State.Active -> when (action) {
          is SuccessEventVM.Action.Back -> {
            SuccessEventVM.Action.Navigation.Back.emit()
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: SuccessEventVM.State) {}

  override fun mapScreenData(): SuccessEventVM.ScreenData = mapper(
    params = SuccessEventMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(SuccessEventVM.Action.Back) },
    ),
  )
}
