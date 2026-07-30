package pl.dev.bkwiatkowski.feature.event.presentation.game

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import javax.inject.Inject

interface EventGameVM {
  sealed interface State {
    data object Active : State
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
      val title: String,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class EventGameVMImpl @Inject constructor(
  private val mapper: EventGameMapper,
) : CustomViewModel<EventGameVM.State, EventGameVM.ScreenData, EventGameVM.Action.Navigation>(
  initialStateValue = EventGameVM.State.Active,
), EventGameVM {

  override val screenData: StateFlow<EventGameVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: EventGameVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is EventGameVM.State.Active -> when (action) {
          is EventGameVM.Action.Back -> EventGameVM.Action.Navigation.Back.emit()
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: EventGameVM.State) {
    when (newState) {

      is EventGameVM.State.Active -> {}
    }
  }

  override fun mapScreenData(): EventGameVM.ScreenData = mapper(
    params = EventGameMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(EventGameVM.Action.Back) },
    ),
  )
}
