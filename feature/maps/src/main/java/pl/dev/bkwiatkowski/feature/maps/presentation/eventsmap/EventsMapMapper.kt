package pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import javax.inject.Inject

interface EventsMapMapper : Mapper<EventsMapMapper.Params, EventsMapVM.ScreenData> {
  data class Params(
    val state: EventsMapVM.State,
    val onBackClick: () -> Unit,
  )
}

class EventsMapMapperImpl @Inject constructor() : EventsMapMapper {
  override fun invoke(params: EventsMapMapper.Params): EventsMapVM.ScreenData =
    when (params.state) {
      is EventsMapVM.State.Initialized -> {
        EventsMapVM.ScreenData.Main(
          onBackClick = params.onBackClick,
        )
      }
    }
}
