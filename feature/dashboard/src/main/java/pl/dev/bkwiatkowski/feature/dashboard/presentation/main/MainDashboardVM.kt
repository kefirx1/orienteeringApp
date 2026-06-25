package pl.dev.bkwiatkowski.feature.dashboard.presentation.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.feature.dashboard.domain.interactor.DashboardMobileInteractor
import javax.inject.Inject

interface MainDashboardVM {
  sealed interface State {
    data object Initial : State
    data object Active : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object ExitApp : Navigation
    }

    data object DataLoaded : Action
    data object Back : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class Initial(
      override val onBackClick: () -> Unit,
    ) : ScreenData

    data class Main(
      override val onBackClick: () -> Unit,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class MainDashboardVMImpl @Inject constructor(
  private val mapper: MainDashboardMapper,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val dashboardMobileInteractor: DashboardMobileInteractor,
) : CustomViewModel<MainDashboardVM.State, MainDashboardVM.ScreenData, MainDashboardVM.Action.Navigation>(
  initialStateValue = MainDashboardVM.State.Initial,
), MainDashboardVM {

  override val screenData: StateFlow<MainDashboardVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: MainDashboardVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is MainDashboardVM.State.Initial -> {
          when (action) {
            is MainDashboardVM.Action.Back -> {
              MainDashboardVM.Action.Navigation.ExitApp.emit()
            }
            is MainDashboardVM.Action.DataLoaded -> {
              MainDashboardVM.State.Active.override()
            }
            else -> {}
          }
        }

        is MainDashboardVM.State.Active -> {
          when (action) {
            is MainDashboardVM.Action.Back -> {
              MainDashboardVM.Action.Navigation.ExitApp.emit()
            }
            else -> {}
          }
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: MainDashboardVM.State) {
    when (newState) {
      is MainDashboardVM.State.Initial -> {
        runWithLoaderUC {
          dashboardMobileInteractor.fetchMobileSettings().fold(
            onRight = {
              dispatchAction(MainDashboardVM.Action.DataLoaded)
            },
            onLeft = {
              //todo check offline mode
            }
          )
        }
      }

      is MainDashboardVM.State.Active -> {}
    }
  }

  override fun mapScreenData(): MainDashboardVM.ScreenData = mapper(
    params = MainDashboardMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(MainDashboardVM.Action.Back) },
    ),
  )
}
