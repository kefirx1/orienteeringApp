package pl.dev.bkwiatkowski.feature.dashboard.presentation.settings.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.dashboard.presentation.settings.SettingsDashboardVM
import pl.dev.bkwiatkowski.feature.dashboard.presentation.settings.SettingsDashboardMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.settings.SettingsDashboardMapperImpl

class SettingsDashboardPreviewProvider : ViewModelPreviewProvider<SettingsDashboardVM, SettingsDashboardVM.ScreenData, SettingsDashboardMapper.Params>() {
  override val mapper: SettingsDashboardMapper = SettingsDashboardMapperImpl()

  override val values: Sequence<SettingsDashboardVM> = sequenceOf(
    object : SettingsDashboardVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = SettingsDashboardVM.State.Initialized)),
      )
    }
  )

  private fun getMapperParams(state: SettingsDashboardVM.State): SettingsDashboardMapper.Params =
    SettingsDashboardMapper.Params(
      state = state,
      onBackClick = {},
      onLogoutClick = {},
    )
}
