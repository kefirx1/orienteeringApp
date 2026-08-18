package pl.dev.bkwiatkowski.feature.dashboard.presentation.friends

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import javax.inject.Inject

interface FriendsDashboardMapper : Mapper<FriendsDashboardMapper.Params, FriendsDashboardVM.ScreenData> {
  data class Params(
    val state: FriendsDashboardVM.State,
    val onBackClick: () -> Unit,
  )
}

class FriendsDashboardMapperImpl @Inject constructor() : FriendsDashboardMapper {
  override fun invoke(params: FriendsDashboardMapper.Params): FriendsDashboardVM.ScreenData =
    when (params.state) {
      FriendsDashboardVM.State.Initial -> FriendsDashboardVM.ScreenData.Empty(
        onBackClick = params.onBackClick,
      )
      FriendsDashboardVM.State.Active -> {
        FriendsDashboardVM.ScreenData.Main(
          onBackClick = params.onBackClick,
          topBarData = TopAppBarData.BackAndTitle(
            title = "Znajomi",
            onNavigationIconClick = params.onBackClick,
          ),
        )
      }
    }
}
