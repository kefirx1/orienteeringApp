package pl.dev.bkwiatkowski.feature.dashboard.presentation.settings

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import javax.inject.Inject

interface SettingsDashboardMapper : Mapper<SettingsDashboardMapper.Params, SettingsDashboardVM.ScreenData> {
  data class Params(
    val state: SettingsDashboardVM.State,
    val onBackClick: () -> Unit,
  )
}

class SettingsDashboardMapperImpl @Inject constructor() : SettingsDashboardMapper {
  override fun invoke(params: SettingsDashboardMapper.Params): SettingsDashboardVM.ScreenData =
    when (params.state) {
      SettingsDashboardVM.State.Initialized -> SettingsDashboardVM.ScreenData.Main(
        onBackClick = params.onBackClick,
        topBarData = TopAppBarData.BackAndTitle(
          title = "Ustawienia",
          onNavigationIconClick = params.onBackClick,
        ),
      )
    }
}
