package pl.dev.bkwiatkowski.feature.event.presentation.game

import pl.dev.bkwiatkowski.common.core.usecase.Mapper

interface EventGameMapper : Mapper<EventGameMapper.Params, EventGameVM.ScreenData> {
  data class Params(
    val state: EventGameVM.State,
    val onBackClick: () -> Unit,
  )
}

class EventGameMapperImpl : EventGameMapper {
  override fun invoke(params: EventGameMapper.Params): EventGameVM.ScreenData =
    when (params.state) {
      is EventGameVM.State.Active -> EventGameVM.ScreenData.Main(
        onBackClick = params.onBackClick,
        title = "Game"
      )
    }
}
