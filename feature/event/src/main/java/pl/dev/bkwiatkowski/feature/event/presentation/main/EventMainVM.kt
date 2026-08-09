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
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.intents.OpenAppSettingsIntentUC
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.localization.GpsManager
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
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.usecase.CompareUserLocationUC

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
    data object Initial : State
    data class PermissionDenied(
      val isDeniedForever: Boolean,
    ) : State
    data class Active(
      val stateData: StateData,
    ) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
    }

    sealed interface NestedNavigation : Action {
      data class GoToMap(val details: MobileEventDetails) : NestedNavigation
      data class GoToGame(val details: MobileEventDetails) : NestedNavigation
    }

    data class CheckUserLocation(
      val newLocation: Location,
    ) : Action
    data object OpenAppSettings : Action
    data object CheckPermission : Action
    data object GoToMap : Action
    data object GoToGame : Action
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
  }

  data class SetupData(
    val eventId: Int,
    val sessionUuid: String,
  )

  fun onMapClick()
  fun onGameClick()
  fun onBackClick()
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
  private val eventBackendInteractor: EventBackendInteractor,
  private val gpsManager: GpsManager,
  private val permissionsManager: PermissionsManager,
  private val openAppSettingsIntentUC: OpenAppSettingsIntentUC,
  private val lifecycleMonitor: LifecycleMonitor,
  private val compareUserLocationUC: CompareUserLocationUC,
  lifecycleMonitorImpl: LifecycleMonitorImpl,
  ) : CustomViewModel<EventMainVM.State, EventMainVM.ScreenData, EventMainVM.Action.Navigation>(
  initialStateValue = EventMainVM.State.Initial,
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
        is EventMainVM.State.Initial -> when (action) {
          is EventMainVM.Action.Back -> {
            EventMainVM.Action.Navigation.Back.emit()
          }
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
              is PermissionResult.Granted -> {
                EventMainVM.State.Initial.override()
              }
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

        is EventMainVM.State.Active -> when (action) {
          is EventMainVM.Action.Back -> {
            eventBackendInteractor.closeSession()
            EventMainVM.Action.Navigation.Back.emit()
          }
          is EventMainVM.Action.GoToMap -> {
            if (currentState.stateData.currentTab == EventMainVM.StateData.CurrentTab.MAP) return@launch
            EventMainVM.State.Active(
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
            EventMainVM.State.Active(
              stateData = currentState.stateData.copy(
                currentTab = EventMainVM.StateData.CurrentTab.GAME,
              )
            ).mutate()
            _nestedNavAction.emit(
              value = EventMainVM.Action.NestedNavigation.GoToGame(details = currentState.stateData.details),
            )
          }
          is EventMainVM.Action.CheckUserLocation -> either {
            val result = compareUserLocationUC(
              params = CompareUserLocationUC.Params(
                currentLocation = action.newLocation,
                waypoints = currentState.stateData.details.eventWaypoints,
              )
            ).getRight()
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: EventMainVM.State) {
    when (newState) {
      is EventMainVM.State.Initial -> {
        either {
          runWithLoaderUC {
            if (!ensureLocationPermission()) return@runWithLoaderUC

            val details = eventBackendInteractor.getMobileEventDetails(
              eventId = setupData.eventId,
            ).getRight()
            contract.setEventDetails(eventDetails = details)

            eventBackendInteractor.openSession(sessionUuid = setupData.sessionUuid).getRight()
            EventMainVM.State.Active(
              stateData = EventMainVM.StateData(
                currentTab = EventMainVM.StateData.CurrentTab.MAP,
                details = details,
              )
            ).override()
          }
        }.onLeft { error ->
          // todo handle error
        }
      }
      is EventMainVM.State.PermissionDenied -> {
        viewModelScope.launch {
          lifecycleMonitor.monitor().collect { lifecycleState ->
            if (lifecycleState == Lifecycle.Event.ON_RESUME) {
              if (ensureLocationPermission()) {
                EventMainVM.State.Initial.override()
              }
            }
          }
        }
      }
      is EventMainVM.State.Active -> {
        viewModelScope.launch {
          gpsManager.getLocationFlow().collect { location ->
            dispatchAction(
              action = EventMainVM.Action.CheckUserLocation(newLocation = location),
            )
          }
        }
        viewModelScope.launch {
          eventBackendInteractor.observeSession().collect { event ->
            println(event)
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
    super.onCleared()

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
