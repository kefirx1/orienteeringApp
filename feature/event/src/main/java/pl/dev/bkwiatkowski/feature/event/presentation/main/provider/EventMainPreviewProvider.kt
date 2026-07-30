package pl.dev.bkwiatkowski.feature.event.presentation.main.provider

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainMapper
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainMapperImpl
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainVM

class EventMainPreviewProvider : ViewModelPreviewProvider<EventMainVM, EventMainVM.ScreenData, EventMainMapper.Params>() {
  override val mapper: EventMainMapper = EventMainMapperImpl()

  override val values: Sequence<EventMainVM> = sequenceOf(
    object : EventMainVM {
      override fun onBackClick() = Unit
      override fun onGameClick() = Unit
      override fun onMapClick() = Unit

      override val nestedNavAction: SharedFlow<EventMainVM.Action.NestedNavigation> =
        MutableSharedFlow()
      override val screenData = MutableStateFlow(
        value = mapper(
          params = getMapperParams(
            state = EventMainVM.State.Active(
              stateData = EventMainVM.StateData(
                currentTab = EventMainVM.StateData.CurrentTab.MAP
              ),
            ),
          ),
        ),
      )
    }
  )

  private fun getMapperParams(state: EventMainVM.State): EventMainMapper.Params =
    EventMainMapper.Params(
      state = state,
      onBackClick = {},
      onOpenMapClick = {},
      onOpenGameClick = {},
    )
}
