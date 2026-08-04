package pl.dev.bkwiatkowski.feature.event.presentation

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameContract
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainContract
import pl.dev.bkwiatkowski.feature.event.presentation.map.EventMapContract
import javax.inject.Inject

interface EventShared {
  data class State(
    val eventDetails: MobileEventDetails?,
  )

  sealed interface Action {
    interface Navigation : Action

    data class SetEventDetails(
      val eventDetails: MobileEventDetails,
    ) : Action
  }

  data object ScreenData
}

@HiltViewModel
class EventSharedVM @Inject constructor(
) : CustomViewModel<EventShared.State, EventShared.ScreenData, EventShared.Action.Navigation>(
  initialStateValue = EventShared.State(eventDetails = null),
), EventShared, EventMainContract, EventMapContract, EventGameContract {
  override suspend fun onStateEnter(newState: EventShared.State) {}
  override fun mapScreenData(): EventShared.ScreenData = EventShared.ScreenData

  init {
    initState()
  }

  fun dispatchAction(action: EventShared.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        else -> when (action) {
          is EventShared.Action.SetEventDetails -> {
            currentState.copy(eventDetails = action.eventDetails).mutate()
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun getEventDetails(): Either<DomainError, MobileEventDetails> = either {
    state.value.eventDetails ?: raise(error = DomainError.Custom(NullPointerException("Event details not found")))
  }

  override suspend fun setEventDetails(eventDetails: MobileEventDetails) {
    dispatchAction(EventShared.Action.SetEventDetails(eventDetails))
  }

}