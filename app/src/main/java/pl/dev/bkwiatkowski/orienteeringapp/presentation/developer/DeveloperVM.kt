package pl.dev.bkwiatkowski.orienteeringapp.presentation.developer

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.technical.flags.domain.model.FlagData
import pl.dev.bkwiatkowski.technical.flags.domain.model.FeatureFlag
import pl.dev.bkwiatkowski.technical.flags.domain.usecase.GetFeatureFlagUC
import pl.dev.bkwiatkowski.technical.flags.domain.usecase.SetFeatureFlagUC
import javax.inject.Inject

interface DeveloperVM {
  sealed interface State {
    data object Initial : State

    data class Initialized(
      val flags: List<FlagData>,
    ) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
    }
    data object Back : Action

    data class OnFlagToggle(val flagName: String, val isChecked: Boolean) : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class Main(
      override val onBackClick: () -> Unit,
      val flags: List<FlagData>,
      val onFlagToggleClick: (flagName: String, isChecked: Boolean) -> Unit,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class DeveloperVMImpl @Inject constructor(
  private val getFeatureFlagUC: GetFeatureFlagUC,
  private val setFeatureFlagUC: SetFeatureFlagUC,
) : CustomViewModel<DeveloperVM.State, DeveloperVM.ScreenData, DeveloperVM.Action.Navigation>(
  initialStateValue = DeveloperVM.State.Initial,
), DeveloperVM {

  override val screenData: StateFlow<DeveloperVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: DeveloperVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is DeveloperVM.State.Initial -> when (action) {
          is DeveloperVM.Action.Back -> DeveloperVM.Action.Navigation.Back.emit()
          else -> {}
        }
        is DeveloperVM.State.Initialized -> when (action) {
          is DeveloperVM.Action.Back -> DeveloperVM.Action.Navigation.Back.emit()

          is DeveloperVM.Action.OnFlagToggle -> {
            val feature = FeatureFlag.entries.find { it.name == action.flagName }
            if (feature != null) {
              setFeatureFlagUC(
                params = SetFeatureFlagUC.SetFeatureFlagParams(
                  flag = feature,
                  enabled = action.isChecked,
                ),
              ).getRightOrNull()

              val updated = currentState.flags.map { fd ->
                if (fd.name == action.flagName) fd.copy(isChecked = action.isChecked) else fd
              }

              DeveloperVM.State.Initialized(flags = updated).mutate()
            }
          }

          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: DeveloperVM.State) {
    when (newState) {
      is DeveloperVM.State.Initial -> {
        val loaded = FeatureFlag.entries.map { flag ->
          val enabled = getFeatureFlagUC(
            params = GetFeatureFlagUC.GetFeatureFlagParams(flag = flag)
          ).getRightOr(default = false)

          FlagData(
            name = flag.name,
            isChecked = enabled,
          )
        }

        DeveloperVM.State.Initialized(flags = loaded).override()
      }

      is DeveloperVM.State.Initialized -> {}
    }
  }

  override fun mapScreenData(): DeveloperVM.ScreenData =
    when (val state = state.value) {
      DeveloperVM.State.Initial -> DeveloperVM.ScreenData.Main(
        onBackClick = { dispatchAction(DeveloperVM.Action.Back) },
        flags = emptyList(),
        onFlagToggleClick = { _, _ -> },
      )
      is DeveloperVM.State.Initialized -> DeveloperVM.ScreenData.Main(
        onBackClick = { dispatchAction(DeveloperVM.Action.Back) },
        flags = state.flags,
        onFlagToggleClick = { flagName, isChecked ->
          dispatchAction(
            DeveloperVM.Action.OnFlagToggle(
              flagName = flagName,
              isChecked = isChecked,
            ),
          )
        },
      )
    }
}
