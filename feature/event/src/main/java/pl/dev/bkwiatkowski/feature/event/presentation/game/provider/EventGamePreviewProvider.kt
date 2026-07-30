package pl.dev.bkwiatkowski.feature.event.presentation.game.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameMapper
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameMapperImpl
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameVM

class EventGamePreviewProvider : ViewModelPreviewProvider<EventGameVM, EventGameVM.ScreenData, EventGameMapper.Params>() {
  override val mapper: EventGameMapper = EventGameMapperImpl()

  override val values: Sequence<EventGameVM> = sequenceOf(
    object : EventGameVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = EventGameVM.State.Active)),
      )
    }
  )

  private fun getMapperParams(state: EventGameVM.State): EventGameMapper.Params =
    EventGameMapper.Params(
      state = state,
      onBackClick = {},
    )
}