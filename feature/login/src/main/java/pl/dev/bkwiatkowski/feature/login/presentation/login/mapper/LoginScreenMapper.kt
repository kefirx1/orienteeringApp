package pl.dev.bkwiatkowski.feature.login.presentation.login.mapper

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldType
import pl.dev.bkwiatkowski.feature.login.presentation.login.LoginVM
import pl.dev.bkwiatkowski.feature.login.presentation.login.mapper.LoginScreenMapper.Params
import javax.inject.Inject

interface LoginScreenMapper : Mapper<Params, LoginVM.ScreenData> {
  data class Params(
    val state: LoginVM.State,
    val onBiometricOpenClick: () -> Unit,
    val onLoginClick: () -> Unit,
    val onStartClick: () -> Unit,
    val onPasswordValueChanged: (String) -> Unit,
    val onBackClick: () -> Unit,
    val onToPasswordLoginClick: () -> Unit,
    val onToBiometricLoginClick: () -> Unit,
  )
}

class LoginScreenMapperImpl @Inject constructor() : LoginScreenMapper {
  override fun invoke(params: Params): LoginVM.ScreenData =
    when (params.state) {
      LoginVM.State.Initial -> LoginVM.ScreenData.Initial(
        onBackClick = params.onBackClick,
      )
      is LoginVM.State.Biometric -> LoginVM.ScreenData.BiometricScreen(
        appName = "OrienteeringApp",
        welcomeLabel = "Witaj ${params.state.userName}!",
        onBackClick = params.onBackClick,
        loginBiometricLabel = "Zaloguj się biometrycznie",
        passwordLoginButtonData = SmallButtonData.Tertiary(
          text = "Zaloguj się przez hasło",
          onClick = params.onToPasswordLoginClick,
        ),
        onBiometricOpenClick = params.onBiometricOpenClick,
      )
      is LoginVM.State.Login -> LoginVM.ScreenData.LoginScreen(
        appName = "OrienteeringApp",
        welcomeLabel = "Witaj ${params.state.userName}!",
        textFieldData = TextFieldData(
          hint = "Wpisz hasło",
          validationState = params.state.passwordState,
          onValueChanged = params.onPasswordValueChanged,
          textFieldType = TextFieldType.Password,
        ),
        buttonData = LargeButtonData.Primary(
          text = "Zaloguj",
          onClick = {
            params.onLoginClick()
          },
        ),
        onBackClick = params.onBackClick,
        biometricLoginButtonData = SmallButtonData.Tertiary(
          text = "Zaloguj się biometrycznie",
          onClick = params.onToBiometricLoginClick,
        ).takeIf { params.state.fromBiometric }
      )
      is LoginVM.State.Registration -> LoginVM.ScreenData.RegistrationScreen(
        appName = "OrienteeringApp",
        buttonData = LargeButtonData.Primary(
          text = "Zaczynajmy",
          onClick = {
            params.onStartClick()
          },
        ),
        onBackClick = params.onBackClick,
      )
    }
}