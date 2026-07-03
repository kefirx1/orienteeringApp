package pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import javax.inject.Inject

interface UserProfileDashboardVM {
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
      val topBarData: TopAppBarData,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class UserProfileDashboardVMImpl @Inject constructor(
  private val mapper: UserProfileDashboardMapper,
) : CustomViewModel<UserProfileDashboardVM.State, UserProfileDashboardVM.ScreenData, UserProfileDashboardVM.Action.Navigation>(
  initialStateValue = UserProfileDashboardVM.State.Initialized,
), UserProfileDashboardVM {

  override val screenData: StateFlow<UserProfileDashboardVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: UserProfileDashboardVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is UserProfileDashboardVM.State.Initialized -> when (action) {
          is UserProfileDashboardVM.Action.Back -> UserProfileDashboardVM.Action.Navigation.Back.emit()
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: UserProfileDashboardVM.State) {
    when (newState) {
      is UserProfileDashboardVM.State.Initialized -> {}
    }
  }

  override fun mapScreenData(): UserProfileDashboardVM.ScreenData = mapper(
    params = UserProfileDashboardMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(UserProfileDashboardVM.Action.Back) },
    ),
  )
}

