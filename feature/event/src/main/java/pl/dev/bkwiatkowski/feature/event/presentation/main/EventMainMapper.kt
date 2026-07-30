package pl.dev.bkwiatkowski.feature.event.presentation.main

import pl.dev.bkwiatkowski.common.core.usecase.Mapper

interface EventMainMapper : Mapper<EventMainMapper.Params, EventMainVM.ScreenData> {
  data class Params(
    val state: EventMainVM.State,
    val onBackClick: () -> Unit,
  )
}

class EventMainMapperImpl : EventMainMapper {
  override fun invoke(params: EventMainMapper.Params): EventMainVM.ScreenData =
    when (params.state) {
      is EventMainVM.State.Initial -> EventMainVM.ScreenData.Loading
      is EventMainVM.State.Active -> EventMainVM.ScreenData.Main(
        onBackClick = params.onBackClick,
        title = "tytul"
      )
    }
}
