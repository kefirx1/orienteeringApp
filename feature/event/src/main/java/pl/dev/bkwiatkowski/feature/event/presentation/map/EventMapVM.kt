package pl.dev.bkwiatkowski.feature.event.presentation.map

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.common.ui.component.icon.ZoomImageData
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails

interface EventMapVM {
  sealed interface State {
    data object Loading : State
    data class Active(val eventDetails: MobileEventDetails) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
    }

    data object Back : Action
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
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel(assistedFactory = EventMapVMImpl.Factory::class)
class EventMapVMImpl @AssistedInject constructor(
  @Assisted private val contract: EventMapContract,
  private val mapper: EventMapMapper,
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
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: EventMapVM.State) {
    when (newState) {
      is EventMapVM.State.Loading -> either {
        val details = contract.getEventDetails().getRight()

        EventMapVM.State.Active(eventDetails = details).override()
      }.onLeft {
        // Handle error
      }
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
