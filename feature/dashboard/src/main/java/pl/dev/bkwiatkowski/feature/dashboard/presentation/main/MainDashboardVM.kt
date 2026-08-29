package pl.dev.bkwiatkowski.feature.dashboard.presentation.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.error.ErrorDataMapper
import pl.dev.bkwiatkowski.common.core.error.ErrorScreenData
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.FabData
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
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

    sealed interface Offline : State {
      data class StateData(
        val userName: String,
        val continueEventId: Int? = null,
        val continueSessionUuid: String? = null,
        val userCanJoin: Boolean,
      )

      data class Content(
        val stateData: StateData,
      ) : Offline

      data class Error(
        val errorScreenData: ErrorScreenData,
        val stateData: StateData,
      ) : Offline
    }
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object ExitApp : Navigation
      data object GoToSettings : Navigation
      data object GoToMap : Navigation
      data object GoToNewRuns: Navigation
      data object GoToMyProfile: Navigation
      data object GoToFriends: Navigation
      data class OpenEventSession(
        val eventId: Int,
        val sessionUuid: String,
      ) : Navigation
    }

    data object NewRun : Action
    data object ToSettings : Action
    data object GoToFriends : Action
    data object CheckNewRuns : Action
    data object ToMyProfile : Action
    data object LoadData : Action
    data class OpenSavedEvent(
      val eventId: Int?,
      val sessionUuid: String?,
    ) : Action
    data class ShowError(
      val errorData: ErrorScreenData,
    ): Action
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

    data class Offline(
      override val onBackClick: () -> Unit,
      val topBarData: TopAppBarData,
      val welcomeLabel: String,
      val welcomeDescription: String,
      val refreshStateButton: SmallButtonData,
      val continueLastRunButton: LargeButtonData.Primary?,
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
                  when (error) {
                    is DomainError.NoNetwork -> {
                      val last = dashboardInteractor.getLastActiveSavedEvent().getRightOrNull()
                      val userCanJoin = last?.session?.userCanJoin == true

                      MainDashboardVM.State.Offline.Content(
                        stateData = MainDashboardVM.State.Offline.StateData(
                          userName = dashboardInteractor.getUserName().getRightOr(default = ""),
                          continueEventId = last?.id,
                          continueSessionUuid = last?.session?.id,
                          userCanJoin = userCanJoin,
                        ),
                      ).override()
                    }
                    else -> {
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
        is MainDashboardVM.State.Offline.Content -> {
          when (action) {
            is MainDashboardVM.Action.Back -> {
              MainDashboardVM.Action.Navigation.ExitApp.emit()
            }
            is MainDashboardVM.Action.NewRun -> {
              MainDashboardVM.Action.Navigation.GoToMap.emit()
            }
            is MainDashboardVM.Action.OpenSavedEvent -> {
              if (action.eventId == null || action.sessionUuid == null) {
                MainDashboardVM.Action.ShowError(
                  errorData = errorDataMapper(
                    params = ErrorDataMapper.Params(
                      error = DomainError.Custom(IllegalStateException("EventId or sessionUuid is null")),
                      onCloseClick = { dispatchAction(MainDashboardVM.Action.Back) },
                    ),
                  ),
                )
                return@launch
              }

              MainDashboardVM.Action.Navigation.OpenEventSession(
                eventId = action.eventId,
                sessionUuid = action.sessionUuid,
              ).emit()
            }
            is MainDashboardVM.Action.LoadData -> {
              MainDashboardVM.State.Initial.override()
            }
            is MainDashboardVM.Action.ShowError -> {
              MainDashboardVM.State.Error(
                errorScreenData = action.errorData,
              ).override()
            }
            else -> {}
          }
        }
        is MainDashboardVM.State.Offline.Error -> {
          when (action) {
            is MainDashboardVM.Action.Back -> {
              MainDashboardVM.State.Offline.Content(
                stateData = currentState.stateData,
              ).override()
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
      is MainDashboardVM.State.Offline -> {}
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
      },
      onContinueLastRunClick = { eventId, sessionUuid ->
        dispatchAction(MainDashboardVM.Action.OpenSavedEvent(eventId = eventId, sessionUuid = sessionUuid))
      },
      onRefreshState = {
        dispatchAction(MainDashboardVM.Action.LoadData)
      }
    )
  )
}
