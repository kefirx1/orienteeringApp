package pl.dev.bkwiatkowski.feature.login.presentation.login

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.error.ErrorDataMapper
import pl.dev.bkwiatkowski.common.core.error.ErrorScreenData
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
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

    sealed interface NewLogin : State {
      data class Content(
        val typedUserName: String = "",
        val userNameState: ValidationState = ValidationState.UnVerified,
        val typedPassword: String = "",
        val passwordState: ValidationState = ValidationState.UnVerified,
      ) : NewLogin

      data class Error(
        val errorScreenData: ErrorScreenData,
      ) : NewLogin
    }

    data object Registration : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object ToDashboard : Navigation
      data object ToOnboarding : Navigation
      data object Back : Navigation
    }

    data object ToOtherScreen : Action
    data object SuccessLogin : Action
    data object Back : Action
    data object CheckPassword : Action
    data object ToOnboarding : Action
    data object ToLoginScreen : Action
    data class UpdatePassword(val password: String) : Action
    data class UpdateUserName(val userName: String) : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class ErrorScreen(
      override val onBackClick: () -> Unit,
      val errorData: ErrorScreenData,
    ) : ScreenData

    data class NewLoginScreen(
      val appName: String,
      val infoLabel: String,
      val loginButton: LargeButtonData,
      val otherOptionsButton: LargeButtonData,
      val usernameInput: TextFieldData,
      val passwordInput: TextFieldData,
      override val onBackClick: () -> Unit,
    ) : ScreenData

    data class RegistrationScreen(
      val appName: String,
      val description: String,
      val loginButton: LargeButtonData,
      val registerButton: LargeButtonData,
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
  private val errorDataMapper: ErrorDataMapper,
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
          is Action.SuccessLogin -> {
            Action.Navigation.ToDashboard.emit()
          }
          is Action.Back -> Action.Navigation.Back.emit()
          else ->  {}
        }
        is State.NewLogin.Content -> when (action) {
          is Action.Back -> Action.Navigation.Back.emit()
          is Action.ToOtherScreen -> {
            State.Registration.override()
          }
          is Action.SuccessLogin -> {
            dispatchAction(Action.UpdateUserName(userName = ""))
            dispatchAction(Action.UpdatePassword(password = ""))
            Action.Navigation.ToDashboard.emit()
          }
          is Action.CheckPassword -> {
            val isUserNameEmpty = currentState.typedUserName.isBlank()
            val isPasswordEmpty = currentState.typedPassword.isBlank()

            if (isUserNameEmpty || isPasswordEmpty) {
              currentState.copy(
                userNameState = if (isUserNameEmpty)
                  ValidationState.Invalid(message = "To pole nie może być puste") else ValidationState.Valid,
                passwordState = if (isPasswordEmpty)
                  ValidationState.Invalid(message = "To pole nie może być puste") else ValidationState.Valid,
              ).mutate()
            } else {
              runWithLoaderUC {
                loginUserInteractor.createNewLocalUser(
                  username = currentState.typedUserName,
                ).fold(
                  onRight = {
                    loginUserInteractor.loginUserRemote(
                      username = currentState.typedUserName,
                      password = currentState.typedPassword,
                    ).fold(
                      onRight = {
                        dispatchAction(Action.SuccessLogin)
                      },
                      onLeft = { error ->
                        State.NewLogin.Error(
                          errorScreenData = errorDataMapper(
                            params = ErrorDataMapper.Params(
                              error = error,
                              onCloseClick = { dispatchAction(Action.Back) },
                            )
                          )
                        ).override()
                      }
                    )
                  },
                  onLeft = { error ->
                    State.NewLogin.Error(
                      errorScreenData = errorDataMapper(
                        params = ErrorDataMapper.Params(
                          error = error,
                          onCloseClick = { dispatchAction(Action.Back) },
                        )
                      )
                    ).override()
                  }
                )
              }
            }
          }
          is Action.UpdatePassword -> {
            currentState.copy(
              typedPassword = action.password,
              passwordState = ValidationState.UnVerified,
            ).mutate()
          }
          is Action.UpdateUserName -> {
            currentState.copy(
              typedUserName = action.userName,
              userNameState = ValidationState.UnVerified,
            ).mutate()
          }
          else -> {}
        }
        is State.NewLogin.Error -> when (action) {
          is Action.Back -> State.NewLogin.Content().override()
          else -> {}
        }
        State.Registration -> when (action) {
          is Action.Back -> Action.Navigation.Back.emit()
          is Action.ToOnboarding -> {
            Action.Navigation.ToOnboarding.emit()
          }
          is Action.ToLoginScreen -> {
            State.NewLogin.Content().override()
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: State) {
    when (newState) {
      State.Initial -> runWithLoaderUC {
        loginUserInteractor.initMasterKey().fold(
          onRight = {
            loginUserInteractor.hasValidRefreshToken().fold(
              onRight = { hasValid ->
                if (hasValid) {
                  dispatchAction(Action.SuccessLogin)
                } else {
                  State.NewLogin.Content().override()
                }
              },
              onLeft = {
                State.NewLogin.Content().override()
              }
            )
          },
          onLeft = {
            State.Registration.override()
          }
        )
      }
      is State.NewLogin -> {}
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
      onPasswordValueChanged = { password ->
        dispatchAction(Action.UpdatePassword(password = password))
      },
      onBackClick = {
        dispatchAction(Action.Back)
      },
      onGoToOtherClick = {
        dispatchAction(Action.ToOtherScreen)
      },
      onUserNameValueChanged = { userName ->
        dispatchAction(Action.UpdateUserName(userName = userName))
      }
    ),
  )

}