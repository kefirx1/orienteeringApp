package pl.dev.bkwiatkowski.feature.event.presentation.main

import android.location.Location
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.error.ErrorDataMapper
import pl.dev.bkwiatkowski.common.core.error.ErrorScreenData
import pl.dev.bkwiatkowski.common.core.intents.OpenAppSettingsIntentUC
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.localization.GpsManager
import pl.dev.bkwiatkowski.common.core.location.Position
import pl.dev.bkwiatkowski.common.core.network.NetworkMonitor
import pl.dev.bkwiatkowski.common.core.network.NetworkStatus
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.common.lifecycle.LifecycleMonitor
import pl.dev.bkwiatkowski.common.lifecycle.LifecycleMonitorImpl
import pl.dev.bkwiatkowski.common.permission.AppPermission
import pl.dev.bkwiatkowski.common.permission.PermissionResult
import pl.dev.bkwiatkowski.common.permission.PermissionsManager
import pl.dev.bkwiatkowski.common.ui.component.permissions.PermissionRequesterData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.event.domain.interactor.EventBackendInteractor
import pl.dev.bkwiatkowski.feature.event.domain.model.FinishSessionResponse
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.SessionWaypointDetail
import pl.dev.bkwiatkowski.feature.event.domain.usecase.FindWaypointFromUserLocationUC
import pl.dev.bkwiatkowski.feature.event.domain.usecase.GetEventDetailsUC
import pl.dev.bkwiatkowski.feature.event.domain.usecase.GetSessionWaypointsUC
import pl.dev.bkwiatkowski.feature.event.domain.usecase.ObserveSessionUC

interface EventMainVM {
  data class StateData(
    val currentTab: CurrentTab,
    val details: MobileEventDetails,
  ) {
    enum class CurrentTab {
      MAP,
      GAME,
    }
  }

  sealed interface State {
    sealed interface Initial : State {
      data object Content : Initial
      data class Error(
        val errorScreenData: ErrorScreenData,
      ) : Initial
    }

    data class PermissionDenied(val isDeniedForever: Boolean) : State

    sealed interface Active : State {
      data class Content(
        val stateData: StateData,
      ) : Active

      data class Error(
        val errorScreenData: ErrorScreenData,
        val content: Content,
      ) : Active
    }
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
      data class Completed(
        val response: FinishSessionResponse,
        val eventName: String,
      ) : Navigation
    }

    sealed interface NestedNavigation : Action {
      data class GoToMap(val details: MobileEventDetails) : NestedNavigation
      data class GoToGame(val details: MobileEventDetails) : NestedNavigation
    }

    data class OnCompleted(val response: FinishSessionResponse) : Action
    data class SetWaypointVisited(
      val lastWaypoint: SessionWaypointDetail,
    ) : Action
    data class CheckUserLocation(
      val newLocation: Location,
    ) : Action
    data object OpenAppSettings : Action
    data object CheckPermission : Action
    data object GoToMap : Action
    data object GoToGame : Action
    data object RetryLoad : Action
    data object Back : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class Loading(
      override val onBackClick: () -> Unit
    ) : ScreenData

    data class PermissionDenied(
      override val onBackClick: () -> Unit,
      val topAppBarData: TopAppBarData,
      val permissionRequesterData: PermissionRequesterData,
    ) : ScreenData

    data class Main(
      override val onBackClick: () -> Unit,
      val onOpenMapClick: () -> Unit,
      val onOpenGameClick: () -> Unit,
      val topAppBarData: TopAppBarData,
      val currentTab: StateData.CurrentTab,
      val tabs: List<TabData>,
    ) : ScreenData {
      data class TabData(
        val title: String,
        val onClick: () -> Unit,
      )
    }

    data class ErrorScreen(
      override val onBackClick: () -> Unit,
      val errorData: ErrorScreenData,
    ) : ScreenData
  }

  data class SetupData(
    val eventId: Int,
    val sessionUuid: String,
  )

  fun onMapClick()
  fun onGameClick()
  fun onBackClick()
  fun onCompleted(response: FinishSessionResponse)
  fun setupContract(contract: EventMainContract)

  val nestedNavAction: SharedFlow<Action.NestedNavigation>
  val screenData: StateFlow<ScreenData>
  var lifecycleOwner: LifecycleOwner
}

@HiltViewModel(assistedFactory = EventMainVMImpl.Factory::class)
class EventMainVMImpl @AssistedInject constructor(
  @Assisted private val setupData: EventMainVM.SetupData,
  private val mapper: EventMainMapper,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val errorDataMapper: ErrorDataMapper,
  private val eventBackendInteractor: EventBackendInteractor,
  private val gpsManager: GpsManager,
  private val permissionsManager: PermissionsManager,
  private val openAppSettingsIntentUC: OpenAppSettingsIntentUC,
  private val lifecycleMonitor: LifecycleMonitor,
  private val networkMonitor: NetworkMonitor,
  private val findWaypointFromUserLocationUC: FindWaypointFromUserLocationUC,
  private val getEventDetailsUC: GetEventDetailsUC,
  private val getSessionWaypointsUC: GetSessionWaypointsUC,
  private val observeSessionUC: ObserveSessionUC,
  lifecycleMonitorImpl: LifecycleMonitorImpl,
  ) : CustomViewModel<EventMainVM.State, EventMainVM.ScreenData, EventMainVM.Action.Navigation>(
  initialStateValue = EventMainVM.State.Initial.Content,
), EventMainVM, LifecycleEventObserver by lifecycleMonitorImpl {

  override lateinit var lifecycleOwner: LifecycleOwner

  override val screenData: StateFlow<EventMainVM.ScreenData> = _screenData

  private val _nestedNavAction: MutableSharedFlow<EventMainVM.Action.NestedNavigation> = MutableSharedFlow()
  override val nestedNavAction: SharedFlow<EventMainVM.Action.NestedNavigation>
    get() = _nestedNavAction

  private lateinit var contract: EventMainContract

  @AssistedFactory
  interface Factory : CustomViewModelFactory<EventMainVM.SetupData, EventMainVMImpl>

  init {
    initState()
  }

  fun dispatchAction(action: EventMainVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is EventMainVM.State.Initial.Content -> when (action) {
          is EventMainVM.Action.Back -> EventMainVM.Action.Navigation.Back.emit()
          is EventMainVM.Action.RetryLoad -> EventMainVM.State.Initial.Content.override()
          else -> {}
        }

        is EventMainVM.State.Initial.Error -> when (action) {
          is EventMainVM.Action.Back -> EventMainVM.Action.Navigation.Back.emit()
          else -> {}
        }

        is EventMainVM.State.Active.Error -> when (action) {
          is EventMainVM.Action.Back -> EventMainVM.State.Active.Content(
            stateData = currentState.content.stateData,
          ).override()
          else -> {}
        }

        is EventMainVM.State.PermissionDenied -> when (action) {
          is EventMainVM.Action.Back -> {
            EventMainVM.Action.Navigation.Back.emit()
          }
          is EventMainVM.Action.CheckPermission -> {
            val result = permissionsManager.requestPermission(
              permission = AppPermission.LOCATION,
            )

            when (result) {
              is PermissionResult.Granted -> EventMainVM.State.Initial.Content.override()
              is PermissionResult.Denied -> {}
              is PermissionResult.DeniedForever -> {
                EventMainVM.State.PermissionDenied(isDeniedForever = true).mutate()
              }
            }
          }
          is EventMainVM.Action.OpenAppSettings -> {
            openAppSettingsIntentUC(UseCase.Params.Empty)
          }
          else -> {}
        }

        is EventMainVM.State.Active.Content -> when (action) {
          is EventMainVM.Action.Back -> {
            eventBackendInteractor.closeSession()
            EventMainVM.Action.Navigation.Back.emit()
          }
          is EventMainVM.Action.GoToMap -> {
            if (currentState.stateData.currentTab == EventMainVM.StateData.CurrentTab.MAP) return@launch
            EventMainVM.State.Active.Content(
              stateData = currentState.stateData.copy(
                currentTab = EventMainVM.StateData.CurrentTab.MAP,
              )
            ).mutate()
            _nestedNavAction.emit(
              value = EventMainVM.Action.NestedNavigation.GoToMap(details = currentState.stateData.details),
            )
          }
          is EventMainVM.Action.GoToGame -> {
            if (currentState.stateData.currentTab == EventMainVM.StateData.CurrentTab.GAME) return@launch
            EventMainVM.State.Active.Content(
              stateData = currentState.stateData.copy(
                currentTab = EventMainVM.StateData.CurrentTab.GAME,
              )
            ).mutate()
            _nestedNavAction.emit(
              value = EventMainVM.Action.NestedNavigation.GoToGame(details = currentState.stateData.details),
            )
          }
          is EventMainVM.Action.CheckUserLocation -> either {
            contract.setCurrentUserPosition(
              position = Position(
                latitude = action.newLocation.latitude,
                longitude = action.newLocation.longitude,
              ),
            )

            val result = findWaypointFromUserLocationUC(
              params = FindWaypointFromUserLocationUC.Params(
                currentLocation = action.newLocation,
                waypoints = currentState.stateData.details.eventWaypoints,
              )
            ).getRight()

            contract.setCurrentWaypoint(waypoint = result)
          }
          is EventMainVM.Action.SetWaypointVisited -> {
            contract.setWaypointVisited(waypoint = action.lastWaypoint)
          }
          is EventMainVM.Action.OnCompleted -> {
            eventBackendInteractor.closeSession()
            EventMainVM.Action.Navigation.Completed(
              response = action.response,
              eventName = currentState.stateData.details.name,
            ).emit()
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: EventMainVM.State) {
    when (newState) {
      is EventMainVM.State.Initial.Content -> {
        runWithLoaderUC {
          either {
            if (!ensureLocationPermission()) return@runWithLoaderUC

            val details = getEventDetailsUC(
              params = GetEventDetailsUC.Params(
                eventId = setupData.eventId,
              )
            ).getRight()

            val currentSessionWaypoints = getSessionWaypointsUC(
              params = GetSessionWaypointsUC.Params(
                sessionUuid = setupData.sessionUuid,
              )
            ).getRight()

            contract.setEventDetails(eventDetails = details)
            contract.setInitialVisitedWaypoints(waypoints = currentSessionWaypoints.waypoints)

            eventBackendInteractor.openSession(sessionUuid = setupData.sessionUuid)
            EventMainVM.State.Active.Content(
              stateData = EventMainVM.StateData(
                currentTab = EventMainVM.StateData.CurrentTab.MAP,
                details = details,
              )
            ).override()
          }.onLeft { error ->
            EventMainVM.State.Initial.Error(
              errorScreenData = errorDataMapper(
                params = ErrorDataMapper.Params(
                  error = error,
                  onCloseClick = { dispatchAction(EventMainVM.Action.Back) },
                  onRetryClick = { dispatchAction(EventMainVM.Action.RetryLoad) },
                ),
              ),
            ).override()
          }
        }
      }
      is EventMainVM.State.PermissionDenied -> {
        viewModelScope.launch {
          lifecycleMonitor.monitor().collect { lifecycleState ->
            if (lifecycleState == Lifecycle.Event.ON_RESUME) {
              if (ensureLocationPermission()) {
                EventMainVM.State.Initial.Content.override()
              }
            }
          }
        }
      }
      is EventMainVM.State.Initial.Error -> {}

      is EventMainVM.State.Active.Error -> {}

      is EventMainVM.State.Active.Content -> {
        viewModelScope.launch {
          gpsManager.getLocationFlow().distinctUntilChanged().collect { location ->
            dispatchAction(
              action = EventMainVM.Action.CheckUserLocation(newLocation = location),
            )
          }
        }
        viewModelScope.launch {
          observeSessionUC(
            params = UseCase.Params.Empty,
          ).collect { event ->
            dispatchAction(
              action = EventMainVM.Action.SetWaypointVisited(
                lastWaypoint = event.lastVisitedWaypoint,
              ),
            )
          }
        }
        viewModelScope.launch {
          networkMonitor.monitor().collect { status ->
            if (status == NetworkStatus.CONNECTED) {
              eventBackendInteractor.openSession(sessionUuid = setupData.sessionUuid).getRightOrNull()
            }
          }
        }
      }
    }
  }

  override fun setupContract(contract: EventMainContract) {
    this.contract = contract
  }

  override fun onGameClick() {
    dispatchAction(EventMainVM.Action.GoToGame)
  }

  override fun onMapClick() {
    dispatchAction(EventMainVM.Action.GoToMap)
  }

  override fun onBackClick() {
    dispatchAction(EventMainVM.Action.Back)
  }

  override fun onCompleted(response: FinishSessionResponse) {
    dispatchAction(EventMainVM.Action.OnCompleted(response = response))
  }

  override fun mapScreenData(): EventMainVM.ScreenData = mapper(
    params = EventMainMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(EventMainVM.Action.Back) },
      onOpenMapClick = { dispatchAction(EventMainVM.Action.GoToMap) },
      onOpenGameClick = { dispatchAction(EventMainVM.Action.GoToGame) },
      onRequestPermissionClick = { dispatchAction(EventMainVM.Action.CheckPermission) },
      onOpenSettingsClick = { dispatchAction(EventMainVM.Action.OpenAppSettings) }
    ),
  )

  override fun onCleared() {
    viewModelScope.launch {
      eventBackendInteractor.closeSession()
    }
  }

  private suspend fun ensureLocationPermission(): Boolean {
    val result = permissionsManager.requestPermission(
      permission = AppPermission.LOCATION,
    )

    return when (result) {
      is PermissionResult.Granted -> true
      is PermissionResult.Denied -> {
        EventMainVM.State.PermissionDenied(isDeniedForever = false).override()
        false
      }
      is PermissionResult.DeniedForever -> {
        EventMainVM.State.PermissionDenied(isDeniedForever = true).override()
        false
      }
    }
  }
}
