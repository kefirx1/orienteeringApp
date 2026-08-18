package pl.dev.bkwiatkowski.feature.login.presentation.login.mapper

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldType
import pl.dev.bkwiatkowski.feature.login.presentation.login.LoginVM
import pl.dev.bkwiatkowski.feature.login.presentation.login.mapper.LoginScreenMapper.Params

interface LoginScreenMapper : Mapper<Params, LoginVM.ScreenData> {
  data class Params(
    val state: LoginVM.State,
    val onLoginClick: () -> Unit,
    val onGoToLoginClick: () -> Unit,
    val onGoToOtherClick: () -> Unit,
    val onGoToRegistrationClick: () -> Unit,
    val onUserNameValueChanged: (String) -> Unit,
    val onPasswordValueChanged: (String) -> Unit,
    val onBackClick: () -> Unit,
  )
}

class LoginScreenMapperImpl : LoginScreenMapper {
  override fun invoke(params: Params): LoginVM.ScreenData =
    when (params.state) {
      LoginVM.State.Initial -> LoginVM.ScreenData.Initial(
        onBackClick = params.onBackClick,
      )
      is LoginVM.State.NewLogin -> LoginVM.ScreenData.NewLoginScreen(
        appName = "OrienteeringApp",
        infoLabel = "Zaloguj się do swojego konta!",
        usernameInput = TextFieldData(
          hint = "Wpisz nazwę użytkownika",
          validationState = params.state.userNameState,
          onValueChanged = params.onUserNameValueChanged,
          textFieldType = TextFieldType.Default,
          initialText = params.state.typedUserName,
        ),
        passwordInput = TextFieldData(
          hint = "Wpisz hasło",
          validationState = params.state.passwordState,
          onValueChanged = params.onPasswordValueChanged,
          textFieldType = TextFieldType.Password,
          initialText = params.state.typedPassword,
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
        onBackClick = params.onBackClick,
      )
    }
}