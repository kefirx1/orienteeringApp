package pl.dev.bkwiatkowski.feature.dashboard.presentation.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.error.ErrorDataMapper
import pl.dev.bkwiatkowski.common.core.error.ErrorScreenData
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.FabData
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.card.ActionCardData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.dashboard.domain.interactor.DashboardInteractor
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendsStatsData
import pl.dev.bkwiatkowski.feature.dashboard.domain.usecase.GetFriendsStatsDataUC
import javax.inject.Inject

interface MainDashboardVM {
  sealed interface State {
    data object Initial : State

    data class Error(
      val errorScreenData: ErrorScreenData,
    ) : State

    data class Active(
      val userName: String,
      val friendsData: FriendsStatsData,
    ) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object ExitApp : Navigation
      data object GoToSettings : Navigation
      data object GoToMap : Navigation
      data object GoToNewRuns: Navigation
      data object GoToMyProfile: Navigation
      data object GoToFriends: Navigation
    }

    data object NewRun : Action
    data object ToSettings : Action
    data object GoToFriends : Action
    data object CheckNewRuns : Action
    data object ToMyProfile : Action
    data object LoadData : Action
    data object Back : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class Initial(
      override val onBackClick: () -> Unit,
    ) : ScreenData

    data class Main(
      override val onBackClick: () -> Unit,
      val topBarData: TopAppBarData,
      val welcomeLabel: String,
      val welcomeDescription: String,
      val friendsCardTitle: String,
      val friendsCardEmptyState: String,
      val friendsData: FriendsStatsData,
      val myProfileCard: ActionCardData,
      val settingsCard: ActionCardData,
      val newRunFab: FabData,
      val goToFriendsButton: SmallButtonData,
      val checkNewRunsButton: SmallButtonData,
    ) : ScreenData

    data class ErrorScreen(
      override val onBackClick: () -> Unit,
      val errorData: ErrorScreenData,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class MainDashboardVMImpl @Inject constructor(
  private val mapper: MainDashboardMapper,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val errorDataMapper: ErrorDataMapper,
  private val getFriendsStatsDataUC: GetFriendsStatsDataUC,
  private val dashboardInteractor: DashboardInteractor,
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
            is MainDashboardVM.Action.LoadData -> {
              runWithLoaderUC {
                either {
                  dashboardInteractor.fetchMobileSettings().getRight()

                  val userName = dashboardInteractor.getUserName().getRight()
                  val friendsData = getFriendsStatsDataUC(UseCase.Params.Empty)
                    .getRightOr(default = FriendsStatsData.EMPTY)

                  MainDashboardVM.State.Active(
                    userName = userName,
                    friendsData = friendsData,
                  ).override()
                }.onLeft { error ->
                  MainDashboardVM.State.Error(
                    errorScreenData = errorDataMapper(
                      params = ErrorDataMapper.Params(
                        error = error,
                        onCloseClick = { dispatchAction(MainDashboardVM.Action.Back) },
                        onRetryClick = { dispatchAction(MainDashboardVM.Action.LoadData) },
                      )
                    ),
                  ).override()
                }
              }

            }
            else -> {}
          }
        }
        is MainDashboardVM.State.Error -> when (action) {
          is MainDashboardVM.Action.Back -> MainDashboardVM.Action.Navigation.ExitApp.emit()
          is MainDashboardVM.Action.LoadData -> MainDashboardVM.State.Initial.override()
          else -> {}
        }

        is MainDashboardVM.State.Active -> {
          when (action) {
            is MainDashboardVM.Action.Back -> {
              MainDashboardVM.Action.Navigation.ExitApp.emit()
            }
            is MainDashboardVM.Action.ToSettings -> {
              MainDashboardVM.Action.Navigation.GoToSettings.emit()
            }
            is MainDashboardVM.Action.NewRun -> {
              MainDashboardVM.Action.Navigation.GoToMap.emit()
            }
            is MainDashboardVM.Action.GoToFriends -> {
              MainDashboardVM.Action.Navigation.GoToFriends.emit()
            }
            is MainDashboardVM.Action.CheckNewRuns -> {
              MainDashboardVM.Action.Navigation.GoToNewRuns.emit()
            }
            is MainDashboardVM.Action.ToMyProfile -> {
              MainDashboardVM.Action.Navigation.GoToMyProfile.emit()
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
        dispatchAction(MainDashboardVM.Action.LoadData)
      }

      is MainDashboardVM.State.Active -> {}
      is MainDashboardVM.State.Error -> {}
    }
  }

  override fun mapScreenData(): MainDashboardVM.ScreenData = mapper(
    params = MainDashboardMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(MainDashboardVM.Action.Back) },
      onNotificationsClick = {
        //todo implement notifications
      },
      onSettingsClick = {
        dispatchAction(MainDashboardVM.Action.ToSettings)
      },
      onNewRunClick = {
        dispatchAction(MainDashboardVM.Action.NewRun)
      },
      onCheckNewRunsClick = {
        dispatchAction(MainDashboardVM.Action.CheckNewRuns)
      },
      onGoToFriendsClick = {
        dispatchAction(MainDashboardVM.Action.GoToFriends)
      },
      onMyProfileClick = {
        dispatchAction(MainDashboardVM.Action.ToMyProfile)
      }
    ),
  )
}
