package pl.dev.bkwiatkowski.feature.event.presentation.main.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainVM
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainMapper
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainMapperImpl

class EventMainPreviewProvider : ViewModelPreviewProvider<EventMainVM, EventMainVM.ScreenData, EventMainMapper.Params>() {
  override val mapper: EventMainMapper = EventMainMapperImpl()

  override val values: Sequence<EventMainVM> = sequenceOf(
    object : EventMainVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = EventMainVM.State.Active(title = "Events"))),
      )
    }
  )

  private fun getMapperParams(state: EventMainVM.State): EventMainMapper.Params =
    EventMainMapper.Params(
      state = state,
      onBackClick = {},
    )
}
