package pl.dev.bkwiatkowski.feature.event.presentation.map.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapMapper
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapMapperImpl
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapVM

class EventMapPreviewProvider : ViewModelPreviewProvider<EventMapVM, EventMapVM.ScreenData, EventMapMapper.Params>() {
  override val mapper: EventMapMapper = EventMapMapperImpl()

  override val values: Sequence<EventMapVM> = sequenceOf(
    object : EventMapVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = EventMapVM.State.Active)),
      )
    }
  )

  private fun getMapperParams(state: EventMapVM.State): EventMapMapper.Params =
    EventMapMapper.Params(
      state = state,
      onBackClick = {},
    )
}