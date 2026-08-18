package pl.dev.bkwiatkowski.feature.login.presentation.setpassword

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.error.ErrorDataMapper
import pl.dev.bkwiatkowski.common.core.error.ErrorScreenData
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModelFactory
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldData
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState.Companion.getState
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState.Companion.isValid
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.login.domain.interactor.LoginUserInteractor
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidateConfirmPasswordUC
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidatePasswordUC
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.SetPasswordVM.Action
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.SetPasswordVM.ScreenData
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.SetPasswordVM.State
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.mapper.SetPasswordScreenMapper
import java.time.LocalDateTime

interface SetPasswordVM {
  data class StateContent(
    val password: String = "",
    val passwordValidation: ValidationState = ValidationState.UnVerified,
    val confirmPassword: String = "",
    val confirmPasswordValidation: ValidationState = ValidationState.UnVerified,
  )

  sealed interface State {
    val content: StateContent

    data class SetPassword(
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
      data object RegistrationSuccess : Navigation
    }

    data class UpdatePassword(val password: String) : Action
    data class UpdateConfirmPassword(val confirmPassword: String) : Action
    data object OnContinueClick : Action
    data object OnBackClick : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class SetPasswordScreen(
      val topAppBarData: TopAppBarData,
      val title: String,
      val subtitle: String,
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

  data class SetupData(
    val userName: String,
    val email: String,
    val phone: String?,
    val dateOfBirth: LocalDateTime,
  )

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel(assistedFactory = SetPasswordVMImpl.Factory::class)
class SetPasswordVMImpl @AssistedInject constructor(
  @Assisted private val setupData: SetPasswordVM.SetupData,
  private val setPasswordScreenMapper: SetPasswordScreenMapper,
  private val validatePasswordUC: ValidatePasswordUC,
  private val validateConfirmPasswordUC: ValidateConfirmPasswordUC,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val errorDataMapper: ErrorDataMapper,
  private val loginUserInteractor: LoginUserInteractor,
) : CustomViewModel<State, ScreenData, Action.Navigation>(
  initialStateValue = State.SetPassword(
    content = SetPasswordVM.StateContent(),
  ),
), SetPasswordVM {
  override val screenData: StateFlow<ScreenData> = _screenData

  @AssistedFactory
  interface Factory : CustomViewModelFactory<SetPasswordVM.SetupData, SetPasswordVMImpl>

  init {
    initState()
  }

  fun dispatchAction(action: Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is State.SetPassword -> when (action) {
          is Action.UpdatePassword -> {
            currentState.copy(
              content = currentState.content.copy(
                password = action.password,
                passwordValidation = ValidationState.UnVerified,
              )
            ).mutate()
          }
          is Action.UpdateConfirmPassword -> {
            currentState.copy(
              content = currentState.content.copy(
                confirmPassword = action.confirmPassword,
                confirmPasswordValidation = ValidationState.UnVerified,
              )
            ).mutate()
          }
          is Action.OnContinueClick -> {
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

              if (listOf(newPasswordState, newConfirmPasswordState).all { it.isValid() }) {
                loginUserInteractor.createNewUser(
                  username = setupData.userName,
                  email = setupData.email,
                  password = currentState.content.password,
                  phoneNumber = setupData.phone,
                  dateOfBirth = setupData.dateOfBirth,
                ).onRight {
                  Action.Navigation.RegistrationSuccess.emit()
                }.onLeft { error ->
                   State.Error(
                     errorScreenData = errorDataMapper(
                       params = ErrorDataMapper.Params(
                         error = error,
                         onCloseClick = { dispatchAction(Action.OnBackClick) },
                       )
                     ),
                     content = currentState.content,
                   ).override()
                 }
              }

              currentState.copy(
                content = currentState.content.copy(
                  passwordValidation = newPasswordState,
                  confirmPasswordValidation = newConfirmPasswordState,
                )
              ).mutate()
            }
          }
          is Action.OnBackClick -> {
            Action.Navigation.Back.emit()
          }
          else -> {}
        }
        is State.Error -> when (action) {
          is Action.OnBackClick -> {
            State.SetPassword(content = currentState.content).override()
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: State) { }

  override fun mapScreenData() = setPasswordScreenMapper(
    params = SetPasswordScreenMapper.Params(
      state = state.value,
      onPasswordValueChanged = { password ->
        dispatchAction(Action.UpdatePassword(password = password))
      },
      onConfirmPasswordValueChanged = { confirmPassword ->
        dispatchAction(Action.UpdateConfirmPassword(confirmPassword = confirmPassword))
      },
      onContinueClick = {
        dispatchAction(Action.OnContinueClick)
      },
      onBackClick = {
        dispatchAction(Action.OnBackClick)
      },
    ),
  )
}
