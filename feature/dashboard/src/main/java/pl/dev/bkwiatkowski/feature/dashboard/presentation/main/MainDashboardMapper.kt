package pl.dev.bkwiatkowski.feature.dashboard.presentation.main

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.R
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.FabData
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.card.ActionCardData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendshipStatus

interface MainDashboardMapper : Mapper<MainDashboardMapper.Params, MainDashboardVM.ScreenData> {
  data class Params(
    val state: MainDashboardVM.State,
    val onBackClick: () -> Unit,
    val onSettingsClick: () -> Unit,
    val onNewRunClick: () -> Unit,
    val onGoToFriendsClick: () -> Unit,
    val onCheckNewEventClick: (Int) -> Unit,
    val onMyProfileClick: () -> Unit,
    val onContinueLastEventClick: (Int?, String?) -> Unit,
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
        topBarData = TopAppBarData.Empty,
        welcomeLabel = "Witaj ${params.state.userName}!",
        welcomeDescription = "Czas na nową trasę sprawdź ostatnio dodane nowe trasy i podejmij wyzwanie ich przejścia",
        friendsCardTitle = "Znajomi",
        friendsCardEmptyState = "Tutaj pojawią się statystyki Twoich znajomych, gdy dodasz ich do listy",
        friendsData = params.state.friendsData.friends.map { friendItem ->
          MainDashboardVM.ScreenData.Main.FriendStatsData(
            friendName = friendItem.username,
            friendDescriptionLabel = when (friendItem.friendStatus) {
              FriendshipStatus.ACCEPTED -> when (friendItem.status) {
                FriendshipStatus.ACCEPTED -> null
                FriendshipStatus.NOT_ACCEPTED -> "Zakceptuj zaproszenie"
              }
              FriendshipStatus.NOT_ACCEPTED -> "Oczekuje na akceptację"
            },
          )
        },
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
        checkNewRunsButton = params.state.lastNewEventId?.let { id ->
          SmallButtonData.Secondary(
            text = "Sprawdź",
            onClick = { params.onCheckNewEventClick(id) },
          )
        },
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
        welcomeDescription = if (params.state.stateData.noNetwork) {
          "Brak połączenia z internetem. Sprawdź połączenie i spróbuj ponownie"
        } else {
          "Nie można pobrać danych z serwera. Spróbuj ponownie później"
        },
        refreshStateButton = SmallButtonData.Secondary(
          text = "Odśwież",
          onClick = params.onRefreshState,
        ),
        continueLastRunButton = LargeButtonData.Primary(
          text = "Kontynuuj ostatni bieg",
          onClick = { params.onContinueLastEventClick(params.state.stateData.continueEventId, params.state.stateData.continueSessionUuid) },
        ).takeIf { params.state.stateData.userCanJoin }
      )
      is MainDashboardVM.State.Offline.Error -> MainDashboardVM.ScreenData.ErrorScreen(
        onBackClick = params.onBackClick,
        errorData = params.state.errorScreenData,
      )
    }
}
