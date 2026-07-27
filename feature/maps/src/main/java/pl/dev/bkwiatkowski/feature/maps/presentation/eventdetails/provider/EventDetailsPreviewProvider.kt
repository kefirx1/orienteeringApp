package pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.maps.domain.model.EventSession
import pl.dev.bkwiatkowski.feature.maps.domain.model.EventStatus
import pl.dev.bkwiatkowski.feature.maps.domain.model.EventType
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileMap
import pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails.EventDetailsMapper
import pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails.EventDetailsMapperImpl
import pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails.EventDetailsVM
import java.time.LocalDateTime

class EventDetailsPreviewProvider : ViewModelPreviewProvider<EventDetailsVM, EventDetailsVM.ScreenData, EventDetailsMapper.Params>() {
  override val mapper: EventDetailsMapper = EventDetailsMapperImpl(
    dateFormatter = mock.dateFormatter,
    bitmapReader = mock.bitmapReader,
  )

  override val values: Sequence<EventDetailsVM> = sequenceOf(
    object : EventDetailsVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = EventDetailsVM.State.Initialized.InitializedAlreadyJoined(
          session = EventSession(
            id = "1",
            startedAt = LocalDateTime.of(2026, 4, 30, 10, 0),
            userCanJoin = true,
            finishedAt = null
          ),
          event = MobileEventDetails(
            id = 1,
            map = MobileMap(
              id = 1,
              name = "Test Map",
              description = "Test Map Description",
              imageData = "",
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
            )
          ),
        ))),
      )
    },
    object : EventDetailsVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = EventDetailsVM.State.Initialized.InitializedNotJoined(
          session = EventSession(
            id = "1",
            startedAt = LocalDateTime.of(2026, 4, 30, 10, 0),
            userCanJoin = true,
            finishedAt = null
          ),
          event = MobileEventDetails(
            id = 1,
            map = MobileMap(
              id = 1,
              name = "Test Map",
              description = "Test Map Description",
              imageData = "",
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
            )
          ),
        ))),
      )
    },
    object : EventDetailsVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = EventDetailsVM.State.Initialized.InitializedNoSession(
          event = MobileEventDetails(
            id = 1,
            map = MobileMap(
              id = 1,
              name = "Test Map",
              description = "Test Map Description",
              imageData = "",
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
            session = null
          ),
        ))),
      )
    },
  )

  private fun getMapperParams(state: EventDetailsVM.State): EventDetailsMapper.Params =
    EventDetailsMapper.Params(
      state = state,
      onBackClick = {},
      onPlayClick = {},
    )
}
