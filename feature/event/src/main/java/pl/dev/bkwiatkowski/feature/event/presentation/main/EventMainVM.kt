package pl.dev.bkwiatkowski.feature.event.presentation.main

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.event.domain.interactor.EventBackendInteractor
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails

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

    data object GoToMap : Action
    data object GoToGame : Action
    data object Back : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data object Loading : ScreenData {
      override val onBackClick: () -> Unit = {}
    }

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
}

@HiltViewModel(assistedFactory = EventMainVMImpl.Factory::class)
class EventMainVMImpl @AssistedInject constructor(
  @Assisted private val setupData: EventMainVM.SetupData,
  private val mapper: EventMainMapper,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val eventBackendInteractor: EventBackendInteractor,
) : CustomViewModel<EventMainVM.State, EventMainVM.ScreenData, EventMainVM.Action.Navigation>(
  initialStateValue = EventMainVM.State.Initial,
), EventMainVM {
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
      is EventMainVM.State.Active -> {
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
    ),
  )

  override fun onCleared() {
    super.onCleared()

    viewModelScope.launch {
      eventBackendInteractor.closeSession()
    }
  }
}
