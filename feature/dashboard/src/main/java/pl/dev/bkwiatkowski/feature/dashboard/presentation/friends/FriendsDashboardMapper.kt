package pl.dev.bkwiatkowski.feature.dashboard.presentation.friends

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendsListData
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendshipStatus
import javax.inject.Inject

interface FriendsDashboardMapper : Mapper<FriendsDashboardMapper.Params, FriendsDashboardVM.ScreenData> {
  data class Params(
    val state: FriendsDashboardVM.State,
    val onBackClick: () -> Unit,
    val onSearchTextChanged: (String) -> Unit,
    val onSearchClick: () -> Unit,
    val onAcceptFriendClick: (Int) -> Unit,
    val onRemoveFriendClick: (Int) -> Unit,
  )
}

class FriendsDashboardMapperImpl @Inject constructor() : FriendsDashboardMapper {
  override fun invoke(params: FriendsDashboardMapper.Params): FriendsDashboardVM.ScreenData =
    when (params.state) {
      is FriendsDashboardVM.State.Initial.Loading -> FriendsDashboardVM.ScreenData.Empty(
        onBackClick = params.onBackClick,
      )
      is FriendsDashboardVM.State.Initial.Error -> FriendsDashboardVM.ScreenData.ErrorScreen(
        onBackClick = params.onBackClick,
        errorData = params.state.errorScreenData,
      )
      is FriendsDashboardVM.State.Active.Error -> FriendsDashboardVM.ScreenData.ErrorScreen(
        onBackClick = params.onBackClick,
        errorData = params.state.errorScreenData,
      )
      is FriendsDashboardVM.State.Active.Content -> {
        FriendsDashboardVM.ScreenData.Main(
          onBackClick = params.onBackClick,
          topBarData = TopAppBarData.BackAndTitle(
            title = "Znajomi",
            onNavigationIconClick = params.onBackClick,
          ),
          searchFieldData = TextFieldData(
            label = "Szukaj znajomego",
            hint = "Wpisz jego nazwę",
            onValueChanged = params.onSearchTextChanged,
            initialText = params.state.stateContent.searchText,
            validationState = params.state.stateContent.searchTextValidation,
          ),
          searchButtonData = LargeButtonData.Primary(
            text = "Dodaj",
            onClick = params.onSearchClick,
          ),
          friendsList = params.state.stateContent.friendsData.friends.map { friend ->
            FriendsDashboardVM.ScreenData.Main.FriendsListItem(
              acceptationLabel = "Oczekuje na akceptację zaproszenia".takeIf {
                friend.friendStatus == FriendshipStatus.NOT_ACCEPTED
              },
              username = friend.username,
              acceptFriendRequestButtonData = SmallButtonData.Primary(
                text = "Akceptuj",
                onClick = { params.onAcceptFriendClick(friend.friendId) },
              ).takeIf { friend.status == FriendshipStatus.NOT_ACCEPTED },
              removeFriendButtonData = SmallButtonData.Secondary(
                text = "Usuń",
                onClick = { params.onRemoveFriendClick(friend.friendId) },
              ),
            )
          },
          emptyLabel = "Nie dodano jeszcze znajomych, wyszukaj ich w polu powyżej",
        )
      }
    }
}
