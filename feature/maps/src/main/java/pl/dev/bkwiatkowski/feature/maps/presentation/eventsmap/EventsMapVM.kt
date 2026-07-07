package pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import javax.inject.Inject

interface EventsMapVM {
  sealed interface State {
    data object Initialized : State
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
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class EventsMapVMImpl @Inject constructor(
  private val mapper: EventsMapMapper,
) : CustomViewModel<EventsMapVM.State, EventsMapVM.ScreenData, EventsMapVM.Action.Navigation>(
  initialStateValue = EventsMapVM.State.Initialized,
), EventsMapVM {

  override val screenData: StateFlow<EventsMapVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: EventsMapVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is EventsMapVM.State.Initialized -> when (action) {
          is EventsMapVM.Action.Back -> {
            EventsMapVM.Action.Navigation.Back.emit()
          }

          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: EventsMapVM.State) {}

  override fun mapScreenData(): EventsMapVM.ScreenData = mapper(
    params = EventsMapMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(EventsMapVM.Action.Back) },
    ),
  )
}

