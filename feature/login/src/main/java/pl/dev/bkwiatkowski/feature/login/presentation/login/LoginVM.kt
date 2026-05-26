package pl.dev.bkwiatkowski.feature.login.presentation.login

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldData
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState
import pl.dev.bkwiatkowski.feature.login.domain.interactor.LoginUserInteractor
import pl.dev.bkwiatkowski.feature.login.presentation.login.LoginVM.Action
import pl.dev.bkwiatkowski.feature.login.presentation.login.LoginVM.ScreenData
import pl.dev.bkwiatkowski.feature.login.presentation.login.LoginVM.State
import pl.dev.bkwiatkowski.feature.login.presentation.login.mapper.LoginScreenMapper
import javax.inject.Inject

interface LoginVM {
  sealed interface State {
    data object Initial : State
    data class Login(
      val fromBiometric: Boolean = false,
      val userName: String,
      val typedPassword: String = "",
      val passwordState: ValidationState = ValidationState.UnVerified,
    ) : State
    data class Biometric(
      val userName: String,
    ) : State
    data object Registration : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object ToDashboard : Navigation
      data object ToOnboarding : Navigation
      data object Back : Navigation
    }

    data object ToBiometricLoginScreen : Action
    data object ToOtherScreen : Action
    data object ToPasswordLoginScreen : Action
    data object ShowBiometricPrompt : Action
    data object SuccessLogin : Action
    data object Back : Action
    data object CheckPassword : Action
    data object ToOnboarding : Action
    data object ToLoginScreen : Action
    data class UpdatePassword(val password: String) : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class LoginScreen(
      val appName: String,
      val welcomeLabel: String,
      val loginButton: LargeButtonData,
      val otherOptionsButton: LargeButtonData,
      val textFieldData: TextFieldData,
      val biometricLoginButtonData: SmallButtonData.Tertiary?,
      override val onBackClick: () -> Unit,
    ) : ScreenData

    data class BiometricScreen(
      val appName: String,
      val welcomeLabel: String,
      val loginBiometricLabel: String,
      val passwordLoginButtonData: SmallButtonData.Tertiary,
      val onBiometricOpenClick: () -> Unit,
      override val onBackClick: () -> Unit,
    ) : ScreenData

    data class RegistrationScreen(
      val appName: String,
      val description: String,
      val loginButton: LargeButtonData,
      val registerButton: LargeButtonData,
      val guestButton: LargeButtonData,
      override val onBackClick: () -> Unit,
    ) : ScreenData

    data class Initial(
      override val onBackClick: () -> Unit,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}


@HiltViewModel
class LoginVMImpl @Inject constructor(
  private val loginScreenMapper: LoginScreenMapper,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val loginUserInteractor: LoginUserInteractor,
) : CustomViewModel<State, ScreenData, Action.Navigation>(
  initialStateValue = State.Initial,
), LoginVM {
  override val screenData: StateFlow<ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        State.Initial -> when (action) {
          is Action.Back -> Action.Navigation.Back.emit()
          else ->  {}
        }
        is State.Biometric -> when (action) {
          is Action.ToPasswordLoginScreen -> {
            State.Login(
              fromBiometric = true,
              userName = currentState.userName,
            ).override()
          }
          is Action.SuccessLogin -> {
            Action.Navigation.ToDashboard.emit()
          }
          is Action.ShowBiometricPrompt -> {}
          is Action.Back -> Action.Navigation.Back.emit()
          else -> {}
        }
        is State.Login -> {
          when (action) {
            is Action.ToOtherScreen -> {
              State.Registration.override()
            }
            is Action.ToBiometricLoginScreen -> {
              State.Biometric(
                userName = currentState.userName,
              ).override()
            }
            is Action.SuccessLogin -> {
              dispatchAction(Action.UpdatePassword(password = ""))
              Action.Navigation.ToDashboard.emit()
            }
            is Action.CheckPassword -> {
              runWithLoaderUC {}
            }
            is Action.UpdatePassword -> {
              currentState.copy(
                typedPassword = action.password,
                passwordState = ValidationState.UnVerified,
              ).mutate()
            }
            is Action.Back -> Action.Navigation.Back.emit()
            else -> {}
          }
        }
        State.Registration -> when (action) {
          is Action.Back -> Action.Navigation.Back.emit()
          is Action.ToOnboarding -> {
            Action.Navigation.ToOnboarding.emit()
          }
          is Action.ToLoginScreen -> {
            State.Login(
              userName = "",
            ).override()
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: State) {
    when (newState) {
      State.Initial -> runWithLoaderUC {
        loginUserInteractor.getSavedUserName().fold(
          onRight = { name ->
            //todo check refreshToken

            if (false) {
              //todo refresh token exist, try to login and if success then to dashboard, if not then to login screen with password
            } else {
              State.Login(
                userName = name,
              ).override()
            }
          },
          onLeft = {
            State.Registration.override()
          }
        )
      }
      is State.Biometric -> {
        dispatchAction(Action.ShowBiometricPrompt)
      }
      is State.Login -> {}
      State.Registration -> {}
    }
  }

  override fun mapScreenData() = loginScreenMapper(
    params = LoginScreenMapper.Params(
      state = state.value,
      onLoginClick = {
        dispatchAction(Action.CheckPassword)
      },
      onGoToRegistrationClick = {
        dispatchAction(Action.ToOnboarding)
      },
      onGoToLoginClick = {
        dispatchAction(Action.ToLoginScreen)
      },
      onPlayAsGuestClick = {

      },
      onPasswordValueChanged = { password ->
        dispatchAction(Action.UpdatePassword(password = password))
      },
      onBackClick = {
        dispatchAction(Action.Back)
      },
      onBiometricOpenClick = {
        dispatchAction(Action.ShowBiometricPrompt)
      },
      onToPasswordLoginClick = {
        dispatchAction(Action.ToPasswordLoginScreen)
      },
      onToBiometricLoginClick = {
        dispatchAction(Action.ToBiometricLoginScreen)
      },
      onGoToOtherClick = {
        dispatchAction(Action.ToOtherScreen)
      }
    ),
  )

}