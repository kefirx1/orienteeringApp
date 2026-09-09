package pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendItem
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendsListData
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendshipStatus
import pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.FriendsDashboardMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.FriendsDashboardMapperImpl
import pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.FriendsDashboardVM
import java.time.LocalDateTime

class FriendsDashboardPreviewProvider : ViewModelPreviewProvider<FriendsDashboardVM, FriendsDashboardVM.ScreenData, FriendsDashboardMapper.Params>() {
  override val mapper: FriendsDashboardMapper = FriendsDashboardMapperImpl()

  override val values: Sequence<FriendsDashboardVM> = sequenceOf(
    object : FriendsDashboardVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = FriendsDashboardVM.State.Active.Content(
          stateContent = FriendsDashboardVM.StateContent(
            searchText = "Test",
            searchTextValidation = ValidationState.Valid,
            friendsData = FriendsListData(
              friends = listOf(
                FriendItem(
                  friendId = 1,
                  username = "JanKowalski",
                  createdAt = LocalDateTime.of(2024, 6, 1, 12, 0),
                  status = FriendshipStatus.ACCEPTED,
                  friendStatus = FriendshipStatus.NOT_ACCEPTED,
                ),
                FriendItem(
                  friendId = 2,
                  username = "AnnaNowak",
                  createdAt = LocalDateTime.of(2024, 6, 2, 14, 30),
                  status = FriendshipStatus.NOT_ACCEPTED,
                  friendStatus = FriendshipStatus.ACCEPTED,
                ),
                FriendItem(
                  friendId = 3,
                  username = "PiotrWiśniewski",
                  createdAt = LocalDateTime.of(2024, 6, 3, 9, 15),
                  status = FriendshipStatus.ACCEPTED,
                  friendStatus = FriendshipStatus.ACCEPTED,
                ),
              ),
            )
          )
        ))),
      )
    },

    object : FriendsDashboardVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = FriendsDashboardVM.State.Active.Content(
          stateContent = FriendsDashboardVM.StateContent(
            searchText = "Test",
            searchTextValidation = ValidationState.Valid,
            friendsData = FriendsListData.EMPTY,
          )
        ))),
      )
    },
  )

  private fun getMapperParams(state: FriendsDashboardVM.State): FriendsDashboardMapper.Params =
    FriendsDashboardMapper.Params(
      state = state,
      onBackClick = {},
      onSearchTextChanged = {},
      onSearchClick = {},
      onAcceptFriendClick = {},
      onRemoveFriendClick = {},
    )
}
