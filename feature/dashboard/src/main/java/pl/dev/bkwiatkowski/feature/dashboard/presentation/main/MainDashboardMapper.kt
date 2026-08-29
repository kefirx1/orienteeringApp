package pl.dev.bkwiatkowski.feature.dashboard.presentation.main

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.R
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.FabData
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.card.ActionCardData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData

interface MainDashboardMapper : Mapper<MainDashboardMapper.Params, MainDashboardVM.ScreenData> {
  data class Params(
    val state: MainDashboardVM.State,
    val onBackClick: () -> Unit,
    val onNotificationsClick: () -> Unit,
    val onSettingsClick: () -> Unit,
    val onNewRunClick: () -> Unit,
    val onGoToFriendsClick: () -> Unit,
    val onCheckNewRunsClick: () -> Unit,
    val onMyProfileClick: () -> Unit,
    val onContinueLastRunClick: (Int?, String?) -> Unit,
    val onRefreshState: () -> Unit,
  )
}

class MainDashboardMapperImpl : MainDashboardMapper {
  override fun invoke(params: MainDashboardMapper.Params): MainDashboardVM.ScreenData =
    when (params.state) {
      is MainDashboardVM.State.Error -> MainDashboardVM.ScreenData.ErrorScreen(
        onBackClick = params.onBackClick,
        errorData = params.state.errorScreenData,
      )
      MainDashboardVM.State.Initial -> MainDashboardVM.ScreenData.Initial(
        onBackClick = params.onBackClick,
      )
      is MainDashboardVM.State.Active -> MainDashboardVM.ScreenData.Main(
        onBackClick = params.onBackClick,
        topBarData = TopAppBarData.Action(
          onActionIconClick = params.onNotificationsClick,
          actionIconId = R.drawable.baseline_circle_notifications_24,
        ),
        welcomeLabel = "Witaj ${params.state.userName}!",
        welcomeDescription = "Czas na nową trasę sprawdź ostatnio dodane nowe trasy i podejmij wyzwanie ich przejścia",
        friendsCardTitle = "Znajomi",
        friendsCardEmptyState = "Tutaj pojawią się statystyki Twoich znajomych, gdy dodasz ich do listy znajomych",
        friendsData = params.state.friendsData,
        settingsCard = ActionCardData(
          onClick = params.onSettingsClick,
          text = "Ustawienia",
        ),
        myProfileCard = ActionCardData(
          onClick = params.onMyProfileClick,
          text = "Mój profil",
        ),
        goToFriendsButton = SmallButtonData.Secondary(
          text = when {
            params.state.friendsData.friends.isEmpty() -> "Dodaj znajomych"
            else -> "Sprawdź znajomych"
          },
          onClick = params.onGoToFriendsClick,
        ),
        checkNewRunsButton = SmallButtonData.Secondary(
          text = "Sprawdź",
          onClick = params.onCheckNewRunsClick,
        ),
        newRunFab = FabData(
          contentDescription = "Rozpocznij nowy bieg",
          onFabClick = params.onNewRunClick,
          fabIconResId = R.drawable.outline_directions_run_24
        )
      )
      is MainDashboardVM.State.Offline.Content -> MainDashboardVM.ScreenData.Offline(
        onBackClick = params.onBackClick,
        topBarData = TopAppBarData.Empty,
        welcomeLabel = "Witaj ${params.state.stateData.userName}!",
        welcomeDescription = "Nie masz połączenia z internetem, niektóre funkcje mogą być niedostępne",
        refreshStateButton = SmallButtonData.Secondary(
          text = "Odśwież",
          onClick = params.onRefreshState,
        ),
        continueLastRunButton = LargeButtonData.Primary(
          text = "Kontynuuj ostatni bieg",
          onClick = { params.onContinueLastRunClick(params.state.stateData.continueEventId, params.state.stateData.continueSessionUuid) },
        ).takeIf { params.state.stateData.userCanJoin }
      )
      is MainDashboardVM.State.Offline.Error -> MainDashboardVM.ScreenData.ErrorScreen(
        onBackClick = params.onBackClick,
        errorData = params.state.errorScreenData,
      )
    }
}
