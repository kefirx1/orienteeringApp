package pl.dev.bkwiatkowski.feature.dashboard.presentation.dialog

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.common.ui.component.dialog.DialogData

interface DashboardDialogVM {
  data object State

  sealed interface Action {
    sealed interface Navigation : Action {
      data class OnDialogAction(
        val dialogAction: () -> Unit,
      ) : Navigation
    }
  }

  data class ScreenData(
    val dialogData: DialogData,
  )

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel(assistedFactory = DashboardDialogVMImpl.DialogVMFactory::class)
class DashboardDialogVMImpl @AssistedInject constructor(
  @Assisted val setupData: DialogData,
) : CustomViewModel<DashboardDialogVM.State, DashboardDialogVM.ScreenData, DashboardDialogVM.Action.Navigation>(
  initialStateValue = DashboardDialogVM.State,
), DashboardDialogVM {

  @AssistedFactory
  interface DialogVMFactory: CustomViewModelFactory<DialogData, DashboardDialogVMImpl> {
    override fun setup(setupData: DialogData) : DashboardDialogVMImpl
  }

  override val screenData: StateFlow<DashboardDialogVM.ScreenData> = _screenData

  override suspend fun onStateEnter(newState: DashboardDialogVM.State) {}

  override fun mapScreenData(): DashboardDialogVM.ScreenData =
    DashboardDialogVM.ScreenData(
      dialogData = setupData.copy(
        onDismiss = {
          viewModelScope.launch {
            DashboardDialogVM.Action.Navigation.OnDialogAction(
              dialogAction = setupData.onDismiss
            ).emit()
          }
        },
        onPrimaryButtonData = setupData.onPrimaryButtonData.copy(
          text = setupData.onPrimaryButtonData.text,
          onClick = {
            viewModelScope.launch {
              DashboardDialogVM.Action.Navigation.OnDialogAction(
                dialogAction = setupData.onPrimaryButtonData.onClick,
              ).emit()
            }
          }
        ),
        onSecondaryButtonData = setupData.onSecondaryButtonData?.copy(
          text = setupData.onSecondaryButtonData!!.text,
          onClick = {
            viewModelScope.launch {
              DashboardDialogVM.Action.Navigation.OnDialogAction(
                dialogAction = setupData.onSecondaryButtonData!!.onClick,
              ).emit()
            }
          }
        ),
      )
    )
}