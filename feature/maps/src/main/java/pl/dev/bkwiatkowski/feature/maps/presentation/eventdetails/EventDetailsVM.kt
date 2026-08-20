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
import pl.dev.bkwiatkowski.common.core.error.ErrorDataMapper
import pl.dev.bkwiatkowski.common.core.error.ErrorScreenData
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
import pl.dev.bkwiatkowski.feature.maps.domain.model.SessionParticipant
import pl.dev.bkwiatkowski.feature.maps.domain.model.UserSessionStatus

interface EventDetailsVM {
  sealed interface State {
    sealed interface Loading : State {
      data object Content : Loading
      data class Error(
        val errorScreenData: ErrorScreenData,
      ) : Loading
    }

    sealed interface Initialized : State {
      data class InitializedNoSession(
        val event: MobileEventDetails,
      ) : Initialized

      sealed interface NotJoined : Initialized {
        data class Content(
          val event: MobileEventDetails,
          val session: EventSession,
          val deniedForever: Boolean,
        ) : NotJoined

        data class Error(
          val errorScreenData: ErrorScreenData,
          val content: Content,
        ) : NotJoined
      }

      data class InitializedAlreadyJoined(
        val event: MobileEventDetails,
        val session: EventSession,
      ) : Initialized

      data class InitializedFinished(
        val event: MobileEventDetails,
        val session: EventSession,
        val sessionParticipants: List<SessionParticipant>,
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

    data class MainFinished(
      override val onBackClick: () -> Unit,
      val event: MobileEventDetails,
      val topAppBarData: TopAppBarData,
      val startDateTime: String,
      val map: Bitmap?,
      val userSessionSectionLabel: String,
      val userSessionSection: List<UserSessionSection>,
    ) : ScreenData {
      data class UserSessionSection(
        val joinTime: String,
        val finishTime: String,
      )
    }

    data class Loading(
      override val onBackClick: () -> Unit,
    ) : ScreenData

    data class ErrorScreen(
      override val onBackClick: () -> Unit,
      val errorData: ErrorScreenData,
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
  private val errorDataMapper: ErrorDataMapper,
  private val openAppSettingsIntentUC: OpenAppSettingsIntentUC,
  snackbarHost: SnackbarHostImpl,
  ) : CustomViewModel<EventDetailsVM.State, EventDetailsVM.ScreenData, EventDetailsVM.Action.Navigation>(
    initialStateValue = EventDetailsVM.State.Loading.Content,
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
        is EventDetailsVM.State.Loading.Content -> when (action) {
          is EventDetailsVM.Action.Back -> {
            EventDetailsVM.Action.Navigation.Back.emit()
          }
          else -> {}
        }
        is EventDetailsVM.State.Loading.Error -> when (action) {
          is EventDetailsVM.Action.Back -> EventDetailsVM.State.Loading.Content.override()
          else -> {}
        }
        is EventDetailsVM.State.Initialized.InitializedFinished -> when (action) {
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
        is EventDetailsVM.State.Initialized.NotJoined.Content -> when (action) {
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
                EventDetailsVM.State.Initialized.NotJoined.Error(
                  errorScreenData = errorDataMapper(
                    params = ErrorDataMapper.Params(
                      error = error,
                      onCloseClick = { dispatchAction(EventDetailsVM.Action.Back) },
                    )
                  ),
                  content = currentState,
                ).override()
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
        is EventDetailsVM.State.Initialized.NotJoined.Error -> when (action) {
          is EventDetailsVM.Action.Back -> EventDetailsVM.State.Initialized.NotJoined.Content(
            event = currentState.content.event,
            session = currentState.content.session,
            deniedForever = currentState.content.deniedForever,
          ).override()
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
      is EventDetailsVM.State.Loading.Content -> either {
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
              val userSessionStatus = mapsBackendInteractor.checkUserInEventSession(
                sessionUuid = details.session.id,
              ).getRight()

              when (userSessionStatus) {
                UserSessionStatus.JOINED -> EventDetailsVM.State.Initialized.InitializedAlreadyJoined(
                  event = details,
                  session = details.session,
                ).override()

                UserSessionStatus.NOT_JOINED -> EventDetailsVM.State.Initialized.NotJoined.Content(
                  event = details,
                  session = details.session,
                  deniedForever = false,
                ).override()

                UserSessionStatus.FINISHED -> {
                  val sessionParticipants = mapsBackendInteractor.getFinishedSessionParticipantsForUser(
                    sessionUuid = details.session.id,
                  ).getRight()

                  EventDetailsVM.State.Initialized.InitializedFinished(
                    event = details,
                    session = details.session,
                    sessionParticipants = sessionParticipants,
                  ).override()
                }
              }
            }
          }
        }
      }.onLeft { error ->
        EventDetailsVM.State.Loading.Error(
          errorScreenData = errorDataMapper(
            params = ErrorDataMapper.Params(
              error = error,
              onCloseClick = { dispatchAction(EventDetailsVM.Action.Back) },
            )
          ),
        ).override()
      }
      is EventDetailsVM.State.Loading.Error -> {}
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
