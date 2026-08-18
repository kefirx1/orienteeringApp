package pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.FriendsDashboardVM
import pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.FriendsDashboardMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.FriendsDashboardMapperImpl

class FriendsDashboardPreviewProvider : ViewModelPreviewProvider<FriendsDashboardVM, FriendsDashboardVM.ScreenData, FriendsDashboardMapper.Params>() {
  override val mapper: FriendsDashboardMapper = FriendsDashboardMapperImpl()

  override val values: Sequence<FriendsDashboardVM> = sequenceOf(
    object : FriendsDashboardVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = FriendsDashboardVM.State.Active)),
      )
    }
  )

  private fun getMapperParams(state: FriendsDashboardVM.State): FriendsDashboardMapper.Params =
    FriendsDashboardMapper.Params(
      state = state,
      onBackClick = {},
    )
}
