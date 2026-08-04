package pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails

import android.graphics.Bitmap
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.intents.OpenAppSettingsIntentUC
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.common.permission.AppPermission
import pl.dev.bkwiatkowski.common.permission.PermissionResult
import pl.dev.bkwiatkowski.common.permission.PermissionsManager
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.common.ui.snackbar.SnackbarHost
import pl.dev.bkwiatkowski.common.ui.snackbar.SnackbarHostImpl
import pl.dev.bkwiatkowski.feature.maps.domain.interactor.MapsBackendInteractor
import pl.dev.bkwiatkowski.feature.maps.domain.model.EventSession
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEventDetails

interface EventDetailsVM {
  sealed interface State {
    data object Loading : State

    sealed interface Initialized : State {
      data class InitializedNoSession(
        val event: MobileEventDetails,
      ) : Initialized

      data class InitializedNotJoined(
        val event: MobileEventDetails,
        val session: EventSession,
        val deniedForever: Boolean,
      ) : Initialized

      data class InitializedAlreadyJoined(
        val event: MobileEventDetails,
        val session: EventSession,
      ) : Initialized
    }
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
      data class ToEventSession(
        val eventId: Int,
        val sessionUuid: String,
      ) : Navigation
    }

    data object Back : Action
    data object ShowLocationPermissionDeniedSnackbar : Action
    data object SetDeniedForeverLocationPermission : Action
    data object OpenAppSettings : Action
    data object ToEventSession : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class MainWithSession(
      override val onBackClick: () -> Unit,
      val snackbarHostState: SnackbarHostState,
      val event: MobileEventDetails,
      val topAppBarData: TopAppBarData,
      val startDateTime: String,
      val map: Bitmap?,
      val playButtonData: LargeButtonData?,
    ) : ScreenData

    data class MainNoSession(
      override val onBackClick: () -> Unit,
      val event: MobileEventDetails,
      val topAppBarData: TopAppBarData,
      val startDateTime: String,
      val map: Bitmap?,
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
  private val permissionsManager: PermissionsManager,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val openAppSettingsIntentUC: OpenAppSettingsIntentUC,
  snackbarHost: SnackbarHostImpl,
) : CustomViewModel<EventDetailsVM.State, EventDetailsVM.ScreenData, EventDetailsVM.Action.Navigation>(
  initialStateValue = EventDetailsVM.State.Loading,
), EventDetailsVM, SnackbarHost by snackbarHost {

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
        is EventDetailsVM.State.Initialized.InitializedAlreadyJoined -> when (action) {
          is EventDetailsVM.Action.Back -> {
            EventDetailsVM.Action.Navigation.Back.emit()
          }
          is EventDetailsVM.Action.ToEventSession -> {
            EventDetailsVM.Action.Navigation.ToEventSession(
              eventId = currentState.event.id,
              sessionUuid = currentState.session.id,
            ).emit()
          }
          else -> {}
        }
        is EventDetailsVM.State.Initialized.InitializedNotJoined -> when (action) {
          is EventDetailsVM.Action.Back -> {
            EventDetailsVM.Action.Navigation.Back.emit()
          }
          is EventDetailsVM.Action.ToEventSession -> {
            if (!ensureLocationPermission()) return@launch

            mapsBackendInteractor.joinEventSession(
              sessionUuid = currentState.session.id,
            ).fold(
              onRight = {
                EventDetailsVM.Action.Navigation.ToEventSession(
                  eventId = currentState.event.id,
                  sessionUuid = currentState.session.id,
                ).emit()
              },
              onLeft = { error ->
                //TODO handle error
              }
            )
          }
          is EventDetailsVM.Action.ShowLocationPermissionDeniedSnackbar -> {
            snackbarHost.showSnackbar(
              message = "Zgoda na lokalizację jest wymagana",
            )
          }
          is EventDetailsVM.Action.SetDeniedForeverLocationPermission -> {
            currentState.copy(deniedForever = true).mutate()
          }
          is EventDetailsVM.Action.OpenAppSettings -> {
            openAppSettingsIntentUC(UseCase.Params.Empty)
          }
          else -> {}
        }
        is EventDetailsVM.State.Initialized.InitializedNoSession -> when (action) {
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
      is EventDetailsVM.State.Loading -> either {
        runWithLoaderUC {
          val details = mapsBackendInteractor.getMobileEventDetails(
            eventId = setupData.eventId,
          ).getRight()

          when {
            details.session == null -> {
              EventDetailsVM.State.Initialized.InitializedNoSession(
                event = details,
              ).override()
            }
            else -> {
              val userHasAlreadyJoined = mapsBackendInteractor.checkUserInEventSession(
                sessionUuid = details.session.id,
              ).getRight()

              if (userHasAlreadyJoined) {
                EventDetailsVM.State.Initialized.InitializedAlreadyJoined(
                  event = details,
                  session = details.session,
                ).override()
              } else {
                EventDetailsVM.State.Initialized.InitializedNotJoined(
                  event = details,
                  session = details.session,
                  deniedForever = false,
                ).override()
              }
            }
          }
        }
      }.onLeft { error ->
        // TODO handle error
      }
      is EventDetailsVM.State.Initialized -> {}
    }
  }

  override fun mapScreenData(): EventDetailsVM.ScreenData = mapper(
    params = EventDetailsMapper.Params(
      state = state.value,
      snackbarHostState = snackbarHost,
      onBackClick = { dispatchAction(EventDetailsVM.Action.Back) },
      onPlayClick = { dispatchAction(EventDetailsVM.Action.ToEventSession) },
      onGoToSettingsClick = { dispatchAction(EventDetailsVM.Action.OpenAppSettings) }
    ),
  )

  private suspend fun ensureLocationPermission(): Boolean {
    val result = permissionsManager.requestPermission(
      permission = AppPermission.LOCATION,
    )

    return when (result) {
      is PermissionResult.Granted -> true
      is PermissionResult.Denied -> {
        dispatchAction(EventDetailsVM.Action.ShowLocationPermissionDeniedSnackbar)
        false
      }
      is PermissionResult.DeniedForever -> {
        dispatchAction(EventDetailsVM.Action.ShowLocationPermissionDeniedSnackbar)
        dispatchAction(EventDetailsVM.Action.SetDeniedForeverLocationPermission)
        false
      }
    }
  }
}
