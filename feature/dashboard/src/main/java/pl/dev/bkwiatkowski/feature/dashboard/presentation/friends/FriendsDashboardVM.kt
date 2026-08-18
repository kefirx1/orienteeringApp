package pl.dev.bkwiatkowski.feature.dashboard.presentation.friends

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import javax.inject.Inject

interface FriendsDashboardVM {
  sealed interface State {
    data object Initial : State

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

    data class Empty(
      override val onBackClick: () -> Unit,
    ) : ScreenData

    data class Main(
      override val onBackClick: () -> Unit,
      val topBarData: TopAppBarData,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class FriendsDashboardVMImpl @Inject constructor(
  private val mapper: FriendsDashboardMapper,
) : CustomViewModel<FriendsDashboardVM.State, FriendsDashboardVM.ScreenData, FriendsDashboardVM.Action.Navigation>(
  initialStateValue = FriendsDashboardVM.State.Initial,
), FriendsDashboardVM {

  override val screenData: StateFlow<FriendsDashboardVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: FriendsDashboardVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is FriendsDashboardVM.State.Initial -> when (action) {
          is FriendsDashboardVM.Action.Back -> {
            FriendsDashboardVM.Action.Navigation.Back.emit()
          }
          else -> {}
        }

        is FriendsDashboardVM.State.Active -> when (action) {
          is FriendsDashboardVM.Action.Back -> {
            FriendsDashboardVM.Action.Navigation.Back.emit()
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: FriendsDashboardVM.State) {
    when (newState) {
      is FriendsDashboardVM.State.Initial -> {}
      is FriendsDashboardVM.State.Active -> {}
    }
  }

  override fun mapScreenData(): FriendsDashboardVM.ScreenData = mapper(
    params = FriendsDashboardMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(FriendsDashboardVM.Action.Back) },
    ),
  )
}
