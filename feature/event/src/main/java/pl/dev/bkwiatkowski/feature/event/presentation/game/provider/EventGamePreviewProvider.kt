package pl.dev.bkwiatkowski.feature.event.presentation.game.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.event.domain.model.SessionWaypointDetail
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameMapper
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameMapperImpl
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameVM
import java.time.LocalDateTime

class EventGamePreviewProvider : ViewModelPreviewProvider<EventGameVM, EventGameVM.ScreenData, EventGameMapper.Params>() {
  override val mapper: EventGameMapper = EventGameMapperImpl(
    dateFormatter = mock.dateFormatter,
  )

  override val values: Sequence<EventGameVM> = sequenceOf(
    object : EventGameVM {
      override val screenData = MutableStateFlow(
        value = mapper(
          params = getMapperParams(
            state = EventGameVM.State.Active(
              visitedWaypoints = listOf(
                SessionWaypointDetail(
                  waypointId = 1,
                  visitedAt = LocalDateTime.of(2024, 6, 1, 12, 0),
                ),
                SessionWaypointDetail(
                  waypointId = 2,
                  visitedAt = LocalDateTime.of(2024, 6, 1, 12, 0),
                ),
              ),
            ),
          ),
        ),
      )
    },
    object : EventGameVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = EventGameVM.State.Empty)),
      )
    }
  )

  private fun getMapperParams(state: EventGameVM.State): EventGameMapper.Params =
    EventGameMapper.Params(
      state = state,
      onBackClick = {},
    )
}