package pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.error.ErrorDataMapper
import pl.dev.bkwiatkowski.common.core.error.ErrorScreenData
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.dashboard.domain.interactor.DashboardInteractor
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.SessionsData
import javax.inject.Inject

interface UserProfileDashboardVM {
  sealed interface State {

    sealed interface Initial : State {
      data object Content : Initial
      data class Error(
        val errorScreenData: ErrorScreenData,
      ) : Initial
    }

    data class Initialized(
      val sessionsData: SessionsData,
      val userName: String,
    ) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
    }

    data object RetryLoad : Action
    data object Back : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class Error(
      override val onBackClick: () -> Unit,
      val errorScreenData: ErrorScreenData,
    ) : ScreenData

    data class Empty(
      override val onBackClick: () -> Unit,
    ) : ScreenData

    data class Main(
      override val onBackClick: () -> Unit,
      val topBarData: TopAppBarData,
      val sessionsLabel: String,
      val groupedSessions: Map<String, List<UserSessionScreenData>>,
      val userName: String,
    ) : ScreenData {
      data class UserSessionScreenData(
        val eventName: String,
        val mapName: String,
        val startDate: String,
        val finishDate: String,
        val visitedWaypoints: String,
      )
    }
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class UserProfileDashboardVMImpl @Inject constructor(
  private val mapper: UserProfileDashboardMapper,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val dashboardInteractor: DashboardInteractor,
  private val errorDataMapper: ErrorDataMapper,
) : CustomViewModel<UserProfileDashboardVM.State, UserProfileDashboardVM.ScreenData, UserProfileDashboardVM.Action.Navigation>(
  initialStateValue = UserProfileDashboardVM.State.Initial.Content,
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
        is UserProfileDashboardVM.State.Initial.Content -> when (action) {
          is UserProfileDashboardVM.Action.Back -> UserProfileDashboardVM.Action.Navigation.Back.emit()
          else -> {}
        }
        is UserProfileDashboardVM.State.Initial.Error -> when (action) {
          is UserProfileDashboardVM.Action.Back -> UserProfileDashboardVM.Action.Navigation.Back.emit()
          is UserProfileDashboardVM.Action.RetryLoad -> UserProfileDashboardVM.State.Initial.Content.override()
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: UserProfileDashboardVM.State) {
    when (newState) {
      is UserProfileDashboardVM.State.Initial.Content -> {
        runWithLoaderUC {
          either {
            val userName = dashboardInteractor.getUserName().getRight()
            val sessionsData = dashboardInteractor.getUserSessions().getRight()

            UserProfileDashboardVM.State.Initialized(
              sessionsData = sessionsData,
              userName = userName,
            ).override()
          }.onLeft { error ->
            UserProfileDashboardVM.State.Initial.Error(
              errorScreenData = errorDataMapper(
                params = ErrorDataMapper.Params(
                  error = error,
                  onCloseClick = { dispatchAction(UserProfileDashboardVM.Action.Back) },
                  onRetryClick = { dispatchAction(UserProfileDashboardVM.Action.RetryLoad) },
                ),
              ),
            ).override()
          }
        }
      }
      is UserProfileDashboardVM.State.Initial.Error -> {}
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

