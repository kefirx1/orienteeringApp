package pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.component.map.MapComponentData
import pl.dev.bkwiatkowski.feature.maps.domain.interactor.MapsBackendInteractor
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEvents
import javax.inject.Inject

interface EventsMapVM {
  sealed interface State {
    data object Loading : State
    data class Initialized(
      val events: MobileEvents?,
    ) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
      data class ToEventDetails(
        val eventId: Int,
      ) : Navigation
    }

    data object Back : Action
    data class ToEventDetails(
      val eventId: Int,
    ) : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class Main(
      override val onBackClick: () -> Unit,
      val mapComponentData: MapComponentData,
    ) : ScreenData

    data class Loading(
      override val onBackClick: () -> Unit,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class EventsMapVMImpl @Inject constructor(
  private val mapper: EventsMapMapper,
  private val mapsBackendInteractor: MapsBackendInteractor,
  private val runWithLoaderUC: RunWithLoaderUC,
) : CustomViewModel<EventsMapVM.State, EventsMapVM.ScreenData, EventsMapVM.Action.Navigation>(
  initialStateValue = EventsMapVM.State.Loading,
), EventsMapVM {

  override val screenData: StateFlow<EventsMapVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: EventsMapVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is EventsMapVM.State.Loading -> {}
        is EventsMapVM.State.Initialized -> when (action) {
          is EventsMapVM.Action.Back -> {
            EventsMapVM.Action.Navigation.Back.emit()
          }
          is EventsMapVM.Action.ToEventDetails -> {
            EventsMapVM.Action.Navigation.ToEventDetails(eventId = action.eventId).emit()
          }

          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: EventsMapVM.State) {
    when (newState) {
      is EventsMapVM.State.Loading -> runWithLoaderUC {
        mapsBackendInteractor.getMobileEvents().fold(
          onRight = { events ->
            EventsMapVM.State.Initialized(events = events).override()
          },
          onLeft = { error ->
            // Handle error if needed
          }
        )
      }
      is EventsMapVM.State.Initialized -> {}
    }
  }

  override fun mapScreenData(): EventsMapVM.ScreenData = mapper(
    params = EventsMapMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(EventsMapVM.Action.Back) },
      onEventDetailsClick = { eventId ->
        dispatchAction(EventsMapVM.Action.ToEventDetails(eventId = eventId))
      }
    ),
  )
}

