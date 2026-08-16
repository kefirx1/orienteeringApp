package pl.dev.bkwiatkowski.feature.event.presentation.success

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import javax.inject.Inject

interface SuccessEventMapper : Mapper<SuccessEventMapper.Params, SuccessEventVM.ScreenData> {
  data class Params(
    val state: SuccessEventVM.State,
    val onBackClick: () -> Unit,
  )
}

class SuccessEventMapperImpl @Inject constructor() : SuccessEventMapper {
  override fun invoke(params: SuccessEventMapper.Params): SuccessEventVM.ScreenData =
    when (params.state) {
      is SuccessEventVM.State.Active -> SuccessEventVM.ScreenData.Main(
        onBackClick = params.onBackClick,
      )
    }
}
