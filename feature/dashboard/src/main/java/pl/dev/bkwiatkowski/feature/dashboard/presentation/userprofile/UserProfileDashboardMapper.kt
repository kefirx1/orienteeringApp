package pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import javax.inject.Inject

interface UserProfileDashboardMapper : Mapper<UserProfileDashboardMapper.Params, UserProfileDashboardVM.ScreenData> {
  data class Params(
    val state: UserProfileDashboardVM.State,
    val onBackClick: () -> Unit,
  )
}

class UserProfileDashboardMapperImpl @Inject constructor() : UserProfileDashboardMapper {
  override fun invoke(params: UserProfileDashboardMapper.Params): UserProfileDashboardVM.ScreenData =
    when (params.state) {
      UserProfileDashboardVM.State.Initialized -> UserProfileDashboardVM.ScreenData.Main(
        onBackClick = params.onBackClick,
        topBarData = TopAppBarData.BackAndTitle(
          title = "Profil użytkownika",
          onNavigationIconClick = params.onBackClick,
        ),
      )
    }
}
