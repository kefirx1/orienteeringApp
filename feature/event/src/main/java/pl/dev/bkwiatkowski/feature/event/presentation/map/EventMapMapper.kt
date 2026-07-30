package pl.dev.bkwiatkowski.feature.event.presentation.map

import pl.dev.bkwiatkowski.common.core.usecase.Mapper

interface EventMapMapper : Mapper<EventMapMapper.Params, EventMapVM.ScreenData> {
  data class Params(
    val state: EventMapVM.State,
    val onBackClick: () -> Unit,
  )
}

class EventMapMapperImpl : EventMapMapper {
  override fun invoke(params: EventMapMapper.Params): EventMapVM.ScreenData =
    when (params.state) {
      is EventMapVM.State.Active -> EventMapVM.ScreenData.Main(
        onBackClick = params.onBackClick,
        title = "Map"
      )
    }
}
