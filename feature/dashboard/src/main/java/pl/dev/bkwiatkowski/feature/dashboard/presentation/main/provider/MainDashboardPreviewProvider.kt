package pl.dev.bkwiatkowski.feature.dashboard.presentation.main.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendItem
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendsListData
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendshipStatus
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardMapperImpl
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardVM
import java.time.LocalDateTime

class MainDashboardPreviewProvider : ViewModelPreviewProvider<MainDashboardVM, MainDashboardVM.ScreenData, MainDashboardMapper.Params>() {
  override val mapper: MainDashboardMapper = MainDashboardMapperImpl()

  override val values: Sequence<MainDashboardVM> = sequenceOf(
    object : MainDashboardVM {
      override val screenData = MutableStateFlow(
        value = mapper(
          params = getMapperParams(
            state = MainDashboardVM.State.Active(
              userName = "Blazej",
              friendsData = FriendsListData(
                friends = listOf(
                  FriendItem(
                    friendId = 1,
                    username = "JanKowalski",
                    createdAt = LocalDateTime.of(2024, 6, 1, 12, 0),
                    status = FriendshipStatus.ACCEPTED,
                    friendStatus = FriendshipStatus.NOT_ACCEPTED,
                    joinedAt = LocalDateTime.of(2024, 6, 1, 12, 0),
                    attendedEventsCount = 5,
                  ),
                  FriendItem(
                    friendId = 2,
                    username = "AnnaNowak",
                    createdAt = LocalDateTime.of(2024, 6, 2, 14, 30),
                    status = FriendshipStatus.NOT_ACCEPTED,
                    friendStatus = FriendshipStatus.ACCEPTED,
                    joinedAt = LocalDateTime.of(2024, 6, 2, 14, 30),
                    attendedEventsCount = 3,
                  ),
                  FriendItem(
                    friendId = 3,
                    username = "PiotrWiśniewski",
                    createdAt = LocalDateTime.of(2024, 6, 3, 9, 15),
                    status = FriendshipStatus.ACCEPTED,
                    friendStatus = FriendshipStatus.ACCEPTED,
                    joinedAt = LocalDateTime.of(2024, 6, 3, 9, 15),
                    attendedEventsCount = 0,
                  ),
                ),
              ),
              lastNewEventId = null,
            ),
          ),
        ),
      )
    },
    object : MainDashboardVM {
      override val screenData = MutableStateFlow(
        value = mapper(
          params = getMapperParams(
            state = MainDashboardVM.State.Active(
              userName = "Blazej",
              friendsData = FriendsListData.EMPTY,
              lastNewEventId = null,
            ),
          ),
        ),
      )
    }
  )

  private fun getMapperParams(state: MainDashboardVM.State): MainDashboardMapper.Params =
    MainDashboardMapper.Params(
      state = state,
      onBackClick = {},
      onSettingsClick = {},
      onNewRunClick = {},
      onGoToFriendsClick = {},
      onCheckNewEventClick = {},
      onMyProfileClick = {},
      onContinueLastEventClick = { _, _ -> },
      onRefreshState = {},
    )
}
