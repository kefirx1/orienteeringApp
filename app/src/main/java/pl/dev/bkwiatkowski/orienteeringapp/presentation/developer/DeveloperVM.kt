package pl.dev.bkwiatkowski.orienteeringapp.presentation.developer

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import javax.inject.Inject

interface DeveloperVM {
  sealed interface State {
    data object Initial : State
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
class DeveloperVMImpl @Inject constructor(
) : CustomViewModel<DeveloperVM.State, DeveloperVM.ScreenData, DeveloperVM.Action.Navigation>(
  initialStateValue = DeveloperVM.State.Initial,
), DeveloperVM {

  override val screenData: StateFlow<DeveloperVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: DeveloperVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is DeveloperVM.State.Initial -> when (action) {
          is DeveloperVM.Action.Back -> DeveloperVM.Action.Navigation.Back.emit()
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: DeveloperVM.State) {}

  override fun mapScreenData(): DeveloperVM.ScreenData =
    when (state.value) {
      DeveloperVM.State.Initial -> DeveloperVM.ScreenData.Main(
        onBackClick = { dispatchAction(DeveloperVM.Action.Back) },
      )
    }
}
