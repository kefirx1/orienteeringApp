package pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.error.ErrorDataMapper
import pl.dev.bkwiatkowski.common.core.error.ErrorScreenData
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.common.ui.component.map.MapComponentData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.maps.domain.interactor.MapsBackendInteractor
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEvents

interface EventsMapVM {
  sealed interface State {
    data object Loading : State

    data class Error(
      val errorScreenData: ErrorScreenData,
    ) : State

    data class Initialized(
      val events: MobileEvents?,
    ) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
      data class ToEventDetails(
        val eventId: Int,
        val isFromDashboard: Boolean = false,
      ) : Navigation
    }
    data object RetryLoad : Action
    data object Back : Action
    data class ToEventDetails(
      val eventId: Int,
    ) : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class Main(
      override val onBackClick: () -> Unit,
      val barData: TopAppBarData,
      val mapComponentData: MapComponentData,
    ) : ScreenData

    data class Loading(
      override val onBackClick: () -> Unit,
    ) : ScreenData

    data class ErrorScreen(
      override val onBackClick: () -> Unit,
      val errorData: ErrorScreenData,
    ) : ScreenData
  }

  data class SetupData(
    val eventId: Int?,
  )

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel(assistedFactory = EventsMapVMImpl.Factory::class)
class EventsMapVMImpl @AssistedInject constructor(
  @Assisted private val setupData: EventsMapVM.SetupData,
  private val mapper: EventsMapMapper,
  private val mapsBackendInteractor: MapsBackendInteractor,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val errorDataMapper: ErrorDataMapper,
) : CustomViewModel<EventsMapVM.State, EventsMapVM.ScreenData, EventsMapVM.Action.Navigation>(
  initialStateValue = EventsMapVM.State.Loading,
), EventsMapVM {

  override val screenData: StateFlow<EventsMapVM.ScreenData> = _screenData

  @AssistedFactory
  interface Factory : CustomViewModelFactory<EventsMapVM.SetupData, EventsMapVMImpl>

  init {
    initState()
  }

  fun dispatchAction(action: EventsMapVM.Action) {
    viewModelScope.launch {
      when (state.value) {
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
        is EventsMapVM.State.Error -> when (action) {
          is EventsMapVM.Action.Back -> EventsMapVM.Action.Navigation.Back.emit()
          is EventsMapVM.Action.RetryLoad -> EventsMapVM.State.Loading.override()
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
            EventsMapVM.State.Error(
              errorScreenData = errorDataMapper(
                params = ErrorDataMapper.Params(
                  error = error,
                  onCloseClick = { dispatchAction(EventsMapVM.Action.Back) },
                  onRetryClick = { dispatchAction(EventsMapVM.Action.RetryLoad) },
                )
              ),
            ).override()
          }
        )
      }
      is EventsMapVM.State.Error -> {}
      is EventsMapVM.State.Initialized -> {
        if (setupData.eventId != null) {
          EventsMapVM.Action.Navigation.ToEventDetails(
            eventId = setupData.eventId,
            isFromDashboard = true,
          ).emit()
        }
      }
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

