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
    val onGoToLoginClick: () -> Unit,
    val onGoToOtherClick: () -> Unit,
    val onGoToRegistrationClick: () -> Unit,
    val onPlayAsGuestClick: () -> Unit,
    val onPasswordValueChanged: (String) -> Unit,
    val onBackClick: () -> Unit,
    val onToPasswordLoginClick: () -> Unit,
    val onToBiometricLoginClick: () -> Unit,
  )
}

class LoginScreenMapperImpl : LoginScreenMapper {
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
        loginButton = LargeButtonData.Primary(
          text = "Zaloguj",
          onClick = {
            params.onLoginClick()
          },
        ),
        otherOptionsButton = LargeButtonData.Secondary(
          text = "Inne opcje",
          onClick = {
            params.onGoToOtherClick()
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
        description = "Odkryj na nowo klasyczne gry na orientację i zamień swój telefon w cyfrowy kompas. Ta aplikacja to Twój przewodnik po świecie biegów i marszów na orientację, dzięki któremu łatwo odnajdziesz ukryte w lesie zielone punkty kontrolne.",
        loginButton = LargeButtonData.Primary(
          text = "Posiadam konto",
          onClick = {
            params.onGoToLoginClick()
          },
        ),
        registerButton = LargeButtonData.Secondary(
          text = "Utwórz konto",
          onClick = {
            params.onGoToRegistrationClick()
          },
        ),
        guestButton = LargeButtonData.Tertiary(
          text = "Graj jako gość",
          onClick = {
            params.onPlayAsGuestClick()
          },
        ),
        onBackClick = params.onBackClick,
      )
    }
}