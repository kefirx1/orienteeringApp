package pl.dev.bkwiatkowski.feature.dashboard.presentation.main

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import javax.inject.Inject

interface MainDashboardMapper : Mapper<MainDashboardMapper.Params, MainDashboardVM.ScreenData> {
  data class Params(
    val state: MainDashboardVM.State,
    val onBackClick: () -> Unit,
  )
}

class MainDashboardMapperImpl : MainDashboardMapper {
  override fun invoke(params: MainDashboardMapper.Params): MainDashboardVM.ScreenData =
    when (params.state) {
      MainDashboardVM.State.Initial -> MainDashboardVM.ScreenData.Initial(
        onBackClick = params.onBackClick,
      )
      MainDashboardVM.State.Active -> MainDashboardVM.ScreenData.Main(
        onBackClick = params.onBackClick,
      )
    }
}
