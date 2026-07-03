package pl.dev.bkwiatkowski.feature.dashboard.presentation.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import javax.inject.Inject

interface SettingsDashboardVM {
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
class SettingsDashboardVMImpl @Inject constructor(
  private val mapper: SettingsDashboardMapper,
) : CustomViewModel<SettingsDashboardVM.State, SettingsDashboardVM.ScreenData, SettingsDashboardVM.Action.Navigation>(
  initialStateValue = SettingsDashboardVM.State.Initialized,
), SettingsDashboardVM {

  override val screenData: StateFlow<SettingsDashboardVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: SettingsDashboardVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is SettingsDashboardVM.State.Initialized -> when (action) {
          is SettingsDashboardVM.Action.Back -> SettingsDashboardVM.Action.Navigation.Back.emit()
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: SettingsDashboardVM.State) {
    when (newState) {
      is SettingsDashboardVM.State.Initialized -> {}
    }
  }

  override fun mapScreenData(): SettingsDashboardVM.ScreenData = mapper(
    params = SettingsDashboardMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(SettingsDashboardVM.Action.Back) },
    ),
  )
}
