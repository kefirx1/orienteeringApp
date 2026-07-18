package pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.feature.maps.domain.interactor.MapsBackendInteractor
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData

interface EventDetailsVM {
  sealed interface State {
    data object Loading : State
    data class Initialized(
      val event: MobileEventDetails,
    ) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
    }

    data object Back : Action
    data object Play : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class Main(
      override val onBackClick: () -> Unit,
      val event: MobileEventDetails,
      val topAppBarData: TopAppBarData,
      val startDateTime: String,
      val map: Bitmap?,
      val playButtonData: LargeButtonData?,
    ) : ScreenData

    data class Loading(
      override val onBackClick: () -> Unit,
    ) : ScreenData
  }

  data class SetupData(
    val eventId: Int,
  )

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel(assistedFactory = EventDetailsVMImpl.Factory::class)
class EventDetailsVMImpl @AssistedInject constructor(
  @Assisted private val setupData: EventDetailsVM.SetupData,
  private val mapper: EventDetailsMapper,
  private val mapsBackendInteractor: MapsBackendInteractor,
  private val runWithLoaderUC: RunWithLoaderUC,
) : CustomViewModel<EventDetailsVM.State, EventDetailsVM.ScreenData, EventDetailsVM.Action.Navigation>(
  initialStateValue = EventDetailsVM.State.Loading,
), EventDetailsVM {

  override val screenData: StateFlow<EventDetailsVM.ScreenData> = _screenData

  @AssistedFactory
  interface Factory : CustomViewModelFactory<EventDetailsVM.SetupData, EventDetailsVMImpl>

  init {
    initState()
  }

  fun dispatchAction(action: EventDetailsVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is EventDetailsVM.State.Loading -> when (action) {
          is EventDetailsVM.Action.Back -> {
            EventDetailsVM.Action.Navigation.Back.emit()
          }
          else -> {}
        }
        is EventDetailsVM.State.Initialized -> when (action) {
          is EventDetailsVM.Action.Back -> {
            EventDetailsVM.Action.Navigation.Back.emit()
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: EventDetailsVM.State) {
    when (newState) {
      is EventDetailsVM.State.Loading -> runWithLoaderUC {
        mapsBackendInteractor.getMobileEventDetails(eventId = setupData.eventId).fold(
          onRight = { details ->
            EventDetailsVM.State.Initialized(event = details).override()
          },
          onLeft = { error ->
            //TODO handle error
          }
        )
      }
      is EventDetailsVM.State.Initialized -> {}
    }
  }

  override fun mapScreenData(): EventDetailsVM.ScreenData = mapper(
    params = EventDetailsMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(EventDetailsVM.Action.Back) },
      onPlayClick = { dispatchAction(EventDetailsVM.Action.Play) },
    ),
  )
}
