package pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.UserProfileDashboardVM
import pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.UserProfileDashboardMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.UserProfileDashboardMapperImpl

class UserProfileDashboardPreviewProvider : ViewModelPreviewProvider<UserProfileDashboardVM, UserProfileDashboardVM.ScreenData, UserProfileDashboardMapper.Params>() {
  override val mapper: UserProfileDashboardMapper = UserProfileDashboardMapperImpl()

  override val values: Sequence<UserProfileDashboardVM> = sequenceOf(
    object : UserProfileDashboardVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = UserProfileDashboardVM.State.Initialized)),
      )
    }
  )

  private fun getMapperParams(state: UserProfileDashboardVM.State): UserProfileDashboardMapper.Params =
    UserProfileDashboardMapper.Params(
      state = state,
      onBackClick = {},
    )
}
