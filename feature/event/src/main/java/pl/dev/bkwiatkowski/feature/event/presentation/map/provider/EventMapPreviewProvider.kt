package pl.dev.bkwiatkowski.feature.event.presentation.map.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.event.domain.model.EventSession
import pl.dev.bkwiatkowski.feature.event.domain.model.EventStatus
import pl.dev.bkwiatkowski.feature.event.domain.model.EventType
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileMap
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapMapper
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapMapperImpl
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapVM
import java.time.LocalDateTime

class EventMapPreviewProvider : ViewModelPreviewProvider<EventMapVM, EventMapVM.ScreenData, EventMapMapper.Params>() {
  override val mapper: EventMapMapper by lazy {
    EventMapMapperImpl(bitmapReader = mock.bitmapReader)
  }

  override val values: Sequence<EventMapVM> = sequenceOf(
    object : EventMapVM {
      override val screenData = MutableStateFlow(
        value = mapper(
          params = getMapperParams(
            state = EventMapVM.State.Active.Content(
              eventDetails = getEventDetails(),
              visitedWrongWaypoint = false,
              currentWaypoint = null,
              nextWaypoint = null,
            ),
          ),
        ),
      )
    },
    object : EventMapVM {
      override val screenData = MutableStateFlow(
        value = mapper(
          params = getMapperParams(
            state = EventMapVM.State.Active.Content(
              eventDetails = getEventDetails(),
              visitedWrongWaypoint = true,
              currentWaypoint = null,
              nextWaypoint = null,
            ),
          ),
        ),
      )
    },
  )

  private fun getEventDetails() = MobileEventDetails(
    id = 1,
    map = MobileMap(
      id = 1,
      name = "Test Map",
      description = "Test Map Description",
      imageData = "",
      waypoints = emptyList(),
    ),
    name = "Laski Arboretum",
    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt",
    createdAt = LocalDateTime.of(2026, 4, 23, 10, 0),
    startDate = LocalDateTime.of(2026, 4, 30, 10, 0),
    startLocationX = 52.2297f,
    startLocationY = 21.0122f,
    eventStatus = EventStatus.IN_PROGRESS,
    eventType = EventType.ONLINE,
    finishedAt = null,
    allowOfflineTracking = true,
    session = EventSession(
      id = "1",
      startedAt = LocalDateTime.of(2026, 4, 30, 10, 0),
      userCanJoin = true,
      finishedAt = null
    ),
    eventWaypoints = emptyList(),
  )

  private fun getMapperParams(state: EventMapVM.State): EventMapMapper.Params =
    EventMapMapper.Params(
      state = state,
      onBackClick = {},
      onCheckWaypointClick = {},
    )
}