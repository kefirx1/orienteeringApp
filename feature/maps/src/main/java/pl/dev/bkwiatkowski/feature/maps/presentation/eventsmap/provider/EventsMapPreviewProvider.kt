package pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap.EventsMapVM
import pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap.EventsMapMapper
import pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap.EventsMapMapperImpl

class EventsMapPreviewProvider : ViewModelPreviewProvider<EventsMapVM, EventsMapVM.ScreenData, EventsMapMapper.Params>() {
  override val mapper: EventsMapMapper = EventsMapMapperImpl()

  override val values: Sequence<EventsMapVM> = sequenceOf(
    object : EventsMapVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = EventsMapVM.State.Initialized)),
      )
    }
  )

  private fun getMapperParams(state: EventsMapVM.State): EventsMapMapper.Params =
    EventsMapMapper.Params(
      state = state,
      onBackClick = {},
    )
}
