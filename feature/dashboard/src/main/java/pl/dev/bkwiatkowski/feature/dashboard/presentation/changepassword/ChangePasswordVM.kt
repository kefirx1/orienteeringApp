package pl.dev.bkwiatkowski.feature.dashboard.presentation.changepassword

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.error.ErrorDataMapper
import pl.dev.bkwiatkowski.common.core.error.ErrorScreenData
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.snackbar.SnackbarHost
import pl.dev.bkwiatkowski.common.ui.snackbar.SnackbarHostImpl
import pl.dev.bkwiatkowski.feature.dashboard.domain.usecase.ValidatePasswordUC
import pl.dev.bkwiatkowski.feature.dashboard.domain.usecase.ValidateConfirmPasswordUC
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldData
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState.Companion.getState
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState.Companion.isValid
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import androidx.compose.material3.SnackbarHostState
import pl.dev.bkwiatkowski.feature.dashboard.domain.interactor.DashboardInteractor
import javax.inject.Inject

interface ChangePasswordVM {
  data class StateContent(
    val oldPassword: String = "",
    val oldPasswordValidation: ValidationState = ValidationState.UnVerified,
    val password: String = "",
    val passwordValidation: ValidationState = ValidationState.UnVerified,
    val confirmPassword: String = "",
    val confirmPasswordValidation: ValidationState = ValidationState.UnVerified,
  )

  sealed interface State {
    val content: StateContent

    data class ChangePassword(
      override val content: StateContent,
    ) : State

    data class Error(
      override val content: StateContent,
      val errorScreenData: ErrorScreenData,
    ) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
      data object Logout : Navigation
    }

    data class UpdateOldPassword(val oldPassword: String) : Action
    data class UpdatePassword(val password: String) : Action
    data class UpdateConfirmPassword(val confirmPassword: String) : Action
    data object OnContinueClick : Action
    data object OnBackClick : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class ChangePasswordScreen(
      val snackbarHostState: SnackbarHostState,
      val topAppBarData: TopAppBarData,
      val title: String,
      val subtitle: String,
      val oldPasswordFieldData: TextFieldData,
      val passwordFieldData: TextFieldData,
      val confirmPasswordFieldData: TextFieldData,
      val continueButton: LargeButtonData,
      override val onBackClick: () -> Unit,
    ) : ScreenData

    data class ErrorScreen(
      override val onBackClick: () -> Unit,
      val errorData: ErrorScreenData,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class ChangePasswordVMImpl @Inject constructor(
  private val mapper: ChangePasswordMapper,
  private val dashboardInteractor: DashboardInteractor,
  private val validatePasswordUC: ValidatePasswordUC,
  private val validateConfirmPasswordUC: ValidateConfirmPasswordUC,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val errorDataMapper: ErrorDataMapper,
  snackbarHost: SnackbarHostImpl,
) : CustomViewModel<ChangePasswordVM.State, ChangePasswordVM.ScreenData, ChangePasswordVM.Action.Navigation>(
  initialStateValue = ChangePasswordVM.State.ChangePassword(
    content = ChangePasswordVM.StateContent()
  ),
), ChangePasswordVM, SnackbarHost by snackbarHost {

  override val screenData: StateFlow<ChangePasswordVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: ChangePasswordVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is ChangePasswordVM.State.ChangePassword -> when (action) {
          is ChangePasswordVM.Action.UpdateOldPassword -> {
            currentState.copy(
              content = currentState.content.copy(
                oldPassword = action.oldPassword,
                oldPasswordValidation = ValidationState.UnVerified,
              )
            ).mutate()
          }

          is ChangePasswordVM.Action.UpdatePassword -> {
            currentState.copy(
              content = currentState.content.copy(
                password = action.password,
                passwordValidation = ValidationState.UnVerified,
              )
            ).mutate()
          }

          is ChangePasswordVM.Action.UpdateConfirmPassword -> {
            currentState.copy(
              content = currentState.content.copy(
                confirmPassword = action.confirmPassword,
                confirmPasswordValidation = ValidationState.UnVerified,
              )
            ).mutate()
          }

          is ChangePasswordVM.Action.OnContinueClick -> {
            runWithLoaderUC {
              val newPasswordState = validatePasswordUC(
                params = ValidatePasswordUC.Params(
                  password = currentState.content.password,
                )
              ).getState()

              val newConfirmPasswordState = validateConfirmPasswordUC(
                params = ValidateConfirmPasswordUC.Params(
                  password = currentState.content.password,
                  confirmPassword = currentState.content.confirmPassword,
                )
              ).getState()

              val isOldPasswordValid = currentState.content.oldPassword.isNotBlank()

              if (!isOldPasswordValid || !newPasswordState.isValid() || !newConfirmPasswordState.isValid()) {
                currentState.copy(
                  content = currentState.content.copy(
                    oldPasswordValidation = if (isOldPasswordValid) {
                      ValidationState.Valid
                    } else {
                      ValidationState.Invalid(message = "To pole nie może być puste")
                    },
                    passwordValidation = newPasswordState,
                    confirmPasswordValidation = newConfirmPasswordState,
                  )
                ).mutate()
                return@runWithLoaderUC
              }

              dashboardInteractor.changePassword(
                oldPassword = currentState.content.oldPassword,
                newPassword = currentState.content.password,
              ).fold(
                onRight = {
                  dashboardInteractor.logout().onLeft {
                    Log.e(
                      tag = Tag(this@ChangePasswordVMImpl),
                      message = "Logout failed",
                    )
                  }
                  snackbarHost.showSnackbar(message = "Hasło zostało zmienione")
                  ChangePasswordVM.Action.Navigation.Logout.emit()
                },
                onLeft = { error ->
                  ChangePasswordVM.State.Error(
                    errorScreenData = errorDataMapper(
                      params = ErrorDataMapper.Params(
                        error = error,
                        onCloseClick = { dispatchAction(ChangePasswordVM.Action.OnBackClick) },
                      )
                    ),
                    content = currentState.content,
                  ).override()
                }
              )
            }
          }

          is ChangePasswordVM.Action.OnBackClick -> {
            ChangePasswordVM.Action.Navigation.Back.emit()
          }

          else -> {}
        }
        is ChangePasswordVM.State.Error -> when (action) {
          is ChangePasswordVM.Action.OnBackClick ->
            ChangePasswordVM.State.ChangePassword(content = currentState.content).override()
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: ChangePasswordVM.State) {}

  override fun mapScreenData(): ChangePasswordVM.ScreenData = mapper(
    params = ChangePasswordMapper.Params(
      state = state.value,
      snackbarHostState = snackbarHost,
      onOldPasswordValueChanged = { old -> dispatchAction(ChangePasswordVM.Action.UpdateOldPassword(oldPassword = old)) },
      onPasswordValueChanged = { password -> dispatchAction(ChangePasswordVM.Action.UpdatePassword(password = password)) },
      onConfirmPasswordValueChanged = { confirm -> dispatchAction(ChangePasswordVM.Action.UpdateConfirmPassword(confirmPassword = confirm)) },
      onContinueClick = { dispatchAction(ChangePasswordVM.Action.OnContinueClick) },
      onBackClick = { dispatchAction(ChangePasswordVM.Action.OnBackClick) },
    )
  )
}
