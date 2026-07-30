package pl.dev.bkwiatkowski.feature.event.presentation.map

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import javax.inject.Inject

interface EventMapVM {
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
class EventMapVMImpl @Inject constructor(
  private val mapper: EventMapMapper,
) : CustomViewModel<EventMapVM.State, EventMapVM.ScreenData, EventMapVM.Action.Navigation>(
  initialStateValue = EventMapVM.State.Active,
), EventMapVM {

  override val screenData: StateFlow<EventMapVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: EventMapVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is EventMapVM.State.Active -> when (action) {
          is EventMapVM.Action.Back -> EventMapVM.Action.Navigation.Back.emit()
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: EventMapVM.State) {
    when (newState) {
      is EventMapVM.State.Active -> {}
    }
  }

  override fun mapScreenData(): EventMapVM.ScreenData = mapper(
    params = EventMapMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(EventMapVM.Action.Back) },
    ),
  )
}
