package pl.dev.bkwiatkowski.feature.event.presentation.main

import pl.dev.bkwiatkowski.common.core.usecase.Mapper

interface EventMainMapper : Mapper<EventMainMapper.Params, EventMainVM.ScreenData> {
  data class Params(
    val state: EventMainVM.State,
    val onBackClick: () -> Unit,
    val onOpenMapClick: () -> Unit,
    val onOpenGameClick: () -> Unit,
  )
}

class EventMainMapperImpl : EventMainMapper {
  override fun invoke(params: EventMainMapper.Params): EventMainVM.ScreenData =
    when (params.state) {
      is EventMainVM.State.Initial -> EventMainVM.ScreenData.Loading
      is EventMainVM.State.Active -> EventMainVM.ScreenData.Main(
        onBackClick = params.onBackClick,
        onOpenMapClick = params.onOpenMapClick,
        onOpenGameClick = params.onOpenGameClick,
        title = "tytul",
        currentTab = params.state.stateData.currentTab,
        tabs = listOf(
          EventMainVM.ScreenData.Main.TabData(
            title = "Mapa",
            onClick = params.onOpenMapClick,
          ),
          EventMainVM.ScreenData.Main.TabData(
            title = "Gra",
            onClick = params.onOpenGameClick,
          ),
        )
      )
    }
}
