package pl.dev.bkwiatkowski.feature.event.presentation.main

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory

interface EventMainVM {
  sealed interface State {
    data object Initial : State
    data class Active(
      val title: String,
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

    data object Loading : ScreenData {
      override val onBackClick: () -> Unit = {}
    }

    data class Main(
      override val onBackClick: () -> Unit,
      val title: String,
    ) : ScreenData
  }

  data class SetupData(
    val eventId: String,
    val sessionUuid: String,
  )

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel(assistedFactory = EventMainVMImpl.Factory::class)
class EventMainVMImpl @AssistedInject constructor(
  @Assisted private val setupData: EventMainVM.SetupData,
  private val mapper: EventMainMapper,
) : CustomViewModel<EventMainVM.State, EventMainVM.ScreenData, EventMainVM.Action.Navigation>(
  initialStateValue = EventMainVM.State.Initial,
), EventMainVM {

  override val screenData: StateFlow<EventMainVM.ScreenData> = _screenData

  @AssistedFactory
  interface Factory : CustomViewModelFactory<EventMainVM.SetupData, EventMainVMImpl>

  init {
    initState()
  }

  fun dispatchAction(action: EventMainVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is EventMainVM.State.Initial -> when (action) {
          is EventMainVM.Action.Back -> {
            EventMainVM.Action.Navigation.Back.emit()
          }
          else -> {}
        }

        is EventMainVM.State.Active -> when (action) {
          is EventMainVM.Action.Back -> {
            EventMainVM.Action.Navigation.Back.emit()
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: EventMainVM.State) {
    when (newState) {
      is EventMainVM.State.Initial -> {
        EventMainVM.State.Active(title = "Events").override()
      }
      is EventMainVM.State.Active -> {}
    }
  }

  override fun mapScreenData(): EventMainVM.ScreenData = mapper(
    params = EventMainMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(EventMainVM.Action.Back) },
    ),
  )
}
