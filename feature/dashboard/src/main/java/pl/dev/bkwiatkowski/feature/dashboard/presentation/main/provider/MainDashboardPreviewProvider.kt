package pl.dev.bkwiatkowski.feature.dashboard.presentation.main.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardVM
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardMapperImpl

class MainDashboardPreviewProvider : ViewModelPreviewProvider<MainDashboardVM, MainDashboardVM.ScreenData, MainDashboardMapper.Params>() {
  override val mapper: MainDashboardMapper = MainDashboardMapperImpl()

  override val values: Sequence<MainDashboardVM> = sequenceOf(
    object : MainDashboardVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = MainDashboardVM.State.Active)),
      )
    }
  )

  private fun getMapperParams(state: MainDashboardVM.State): MainDashboardMapper.Params =
    MainDashboardMapper.Params(
      state = state,
      onBackClick = {},
    )
}
