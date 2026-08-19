package pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.SessionsData
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.UserSessionData
import pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.UserProfileDashboardVM
import pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.UserProfileDashboardMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.UserProfileDashboardMapperImpl
import java.time.LocalDateTime

class UserProfileDashboardPreviewProvider : ViewModelPreviewProvider<UserProfileDashboardVM, UserProfileDashboardVM.ScreenData, UserProfileDashboardMapper.Params>() {
  override val mapper: UserProfileDashboardMapper = UserProfileDashboardMapperImpl(
    dateFormatter = mock.dateFormatter,
  )

  override val values: Sequence<UserProfileDashboardVM> = sequenceOf(
    object : UserProfileDashboardVM {
      override val screenData = MutableStateFlow(
        value = mapper(
          params = getMapperParams(
            state = UserProfileDashboardVM.State.Initialized(
              sessionsData = SessionsData(
                sessions = listOf(
                  UserSessionData(
                    sessionUuid = "session-uuid-1",
                    startedAt = LocalDateTime.of(2024, 6, 1, 10, 0),
                    visitedWaypointsCount = 5,
                    mapName = "Map 1",
                    eventName = "Event 1",
                    finishedAt = LocalDateTime.of(2024, 6, 1, 12, 0)
                  ),
                ),
              ),
              userName = "John Doe"
            )
          )
        ),
      )
    }
  )

  private fun getMapperParams(state: UserProfileDashboardVM.State): UserProfileDashboardMapper.Params =
    UserProfileDashboardMapper.Params(
      state = state,
      onBackClick = {},
    )
}
