package pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.FriendsDashboardMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.FriendsDashboardMapperImpl
import pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.FriendsDashboardVM

class FriendsDashboardPreviewProvider : ViewModelPreviewProvider<FriendsDashboardVM, FriendsDashboardVM.ScreenData, FriendsDashboardMapper.Params>() {
  override val mapper: FriendsDashboardMapper = FriendsDashboardMapperImpl()

  override val values: Sequence<FriendsDashboardVM> = sequenceOf(
    object : FriendsDashboardVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = FriendsDashboardVM.State.Active(
          content = FriendsDashboardVM.StateContent(
            searchText = "Test",
            searchTextValidation = ValidationState.Valid,
          )
        ))),
      )
    }
  )

  private fun getMapperParams(state: FriendsDashboardVM.State): FriendsDashboardMapper.Params =
    FriendsDashboardMapper.Params(
      state = state,
      onBackClick = {},
      onSearchTextChanged = {},
      onSearchClick = {},
    )
}
