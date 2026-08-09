package pl.dev.bkwiatkowski.feature.event.presentation.main.provider

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.event.domain.model.EventSession
import pl.dev.bkwiatkowski.feature.event.domain.model.EventStatus
import pl.dev.bkwiatkowski.feature.event.domain.model.EventType
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileMap
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainContract
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainMapper
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainMapperImpl
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainVM
import java.time.LocalDateTime

class EventMainPreviewProvider : ViewModelPreviewProvider<EventMainVM, EventMainVM.ScreenData, EventMainMapper.Params>() {
  override val mapper: EventMainMapper = EventMainMapperImpl()

  override val values: Sequence<EventMainVM> = sequenceOf(
    object : EventMainVM {
      override fun onBackClick() = Unit
      override fun onGameClick() = Unit
      override fun onMapClick() = Unit
      override fun setupContract(contract: EventMainContract) = Unit

      override var lifecycleOwner: LifecycleOwner = mock.lifecycleOwner

      override val nestedNavAction: SharedFlow<EventMainVM.Action.NestedNavigation> =
        MutableSharedFlow()
      override val screenData = MutableStateFlow(
        value = mapper(
          params = getMapperParams(
            state = EventMainVM.State.Active(
              stateData = EventMainVM.StateData(
                currentTab = EventMainVM.StateData.CurrentTab.MAP,
                details = MobileEventDetails(
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
                ),
              ),
            ),
          ),
        ),
      )
    },
    object : EventMainVM {
      override fun onBackClick() = Unit
      override fun onGameClick() = Unit
      override fun onMapClick() = Unit
      override fun setupContract(contract: EventMainContract) = Unit

      override var lifecycleOwner: LifecycleOwner = mock.lifecycleOwner

      override val nestedNavAction: SharedFlow<EventMainVM.Action.NestedNavigation> =
        MutableSharedFlow()
      override val screenData = MutableStateFlow(
        value = mapper(
          params = getMapperParams(
            state = EventMainVM.State.PermissionDenied(
              isDeniedForever = false,
            ),
          ),
        ),
      )
    },
    object : EventMainVM {
      override fun onBackClick() = Unit
      override fun onGameClick() = Unit
      override fun onMapClick() = Unit
      override fun setupContract(contract: EventMainContract) = Unit

      override var lifecycleOwner: LifecycleOwner = mock.lifecycleOwner

      override val nestedNavAction: SharedFlow<EventMainVM.Action.NestedNavigation> =
        MutableSharedFlow()
      override val screenData = MutableStateFlow(
        value = mapper(
          params = getMapperParams(
            state = EventMainVM.State.PermissionDenied(
              isDeniedForever = true,
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
      onOpenSettingsClick = {},
      onRequestPermissionClick = {},
    )
}
