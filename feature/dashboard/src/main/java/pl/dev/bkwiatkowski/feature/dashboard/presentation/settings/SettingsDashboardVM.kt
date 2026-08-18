package pl.dev.bkwiatkowski.feature.dashboard.presentation.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.component.card.ActionCardData
import pl.dev.bkwiatkowski.common.ui.component.dialog.DialogData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.dashboard.domain.interactor.DashboardInteractor
import javax.inject.Inject

interface SettingsDashboardVM {
  sealed interface State {
    data object Initialized : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
      data class OpenLogoutDialog(
        val dialogData: DialogData,
      ) : Navigation
      data object Logout : Navigation
    }

    data object Logout : Action
    data object Back : Action
    data object OpenLogout : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class Main(
      override val onBackClick: () -> Unit,
      val topBarData: TopAppBarData,
      val logoutCard: ActionCardData,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class SettingsDashboardVMImpl @Inject constructor(
  private val mapper: SettingsDashboardMapper,
  private val dialogMapper: SettingsDialogMapper,
  private val dashboardInteractor: DashboardInteractor,
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
          is SettingsDashboardVM.Action.OpenLogout -> showDialog(
            dialogType = SettingsDialogMapper.DialogType.Logout,
          )
          is SettingsDashboardVM.Action.Logout -> {
            dashboardInteractor.logout().onLeft {
              Log.e(
                tag = Tag(this@SettingsDashboardVMImpl),
                message = "Logout failed",
              )
            }.onRight {
              SettingsDashboardVM.Action.Navigation.Logout.emit()
            }
          }
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
      onLogoutClick = { dispatchAction(SettingsDashboardVM.Action.OpenLogout) },
    ),
  )

  private suspend fun showDialog(dialogType: SettingsDialogMapper.DialogType) {
    SettingsDashboardVM.Action.Navigation.OpenLogoutDialog(
      dialogData = dialogMapper(
        params = SettingsDialogMapper.Params(
          type = dialogType,
          onLogoutClick = {
            dispatchAction(SettingsDashboardVM.Action.Logout)
          }
        )
      )
    ).emit()
  }
}
