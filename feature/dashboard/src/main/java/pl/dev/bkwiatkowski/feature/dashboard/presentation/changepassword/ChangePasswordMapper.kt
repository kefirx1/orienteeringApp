package pl.dev.bkwiatkowski.feature.dashboard.presentation.changepassword

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import androidx.compose.material3.SnackbarHostState
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldType
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData

interface ChangePasswordMapper : Mapper<ChangePasswordMapper.Params, ChangePasswordVM.ScreenData> {
  data class Params(
    val state: ChangePasswordVM.State,
    val snackbarHostState: SnackbarHostState,
    val onOldPasswordValueChanged: (String) -> Unit,
    val onPasswordValueChanged: (String) -> Unit,
    val onConfirmPasswordValueChanged: (String) -> Unit,
    val onContinueClick: () -> Unit,
    val onBackClick: () -> Unit,
  )
}

class ChangePasswordMapperImpl : ChangePasswordMapper {
  override operator fun invoke(params: ChangePasswordMapper.Params): ChangePasswordVM.ScreenData {
    return when (val state = params.state) {
      is ChangePasswordVM.State.Error -> ChangePasswordVM.ScreenData.ErrorScreen(
        onBackClick = params.onBackClick,
        errorData = state.errorScreenData,
      )

      is ChangePasswordVM.State.ChangePassword -> ChangePasswordVM.ScreenData.ChangePasswordScreen(
        topAppBarData = TopAppBarData.Back(
          onNavigationIconClick = params.onBackClick,
        ),
        title = "Zmień hasło",
        subtitle = "Po zmianie hasła nastąpi wylogowanie z aplikacji.\n\nHasło musi mieć co najmniej 8 znaków",
        oldPasswordFieldData = TextFieldData(
          label = "Aktualne hasło",
          hint = "Wpisz aktualne hasło",
          onValueChanged = params.onOldPasswordValueChanged,
          textFieldType = TextFieldType.Password,
          initialText = state.content.oldPassword,
          validationState = state.content.oldPasswordValidation,
        ),
        passwordFieldData = TextFieldData(
          label = "Nowe hasło",
          hint = "Wpisz nowe hasło",
          onValueChanged = params.onPasswordValueChanged,
          textFieldType = TextFieldType.Password,
          initialText = state.content.password,
          validationState = state.content.passwordValidation,
        ),
        confirmPasswordFieldData = TextFieldData(
          label = "Potwierdź hasło",
          hint = "Wpisz ponownie hasło",
          onValueChanged = params.onConfirmPasswordValueChanged,
          textFieldType = TextFieldType.Password,
          initialText = state.content.confirmPassword,
          validationState = state.content.confirmPasswordValidation,
        ),
        continueButton = LargeButtonData.Primary(
          text = "Zmień hasło",
          onClick = params.onContinueClick,
        ),
        snackbarHostState = params.snackbarHostState,
        onBackClick = params.onBackClick,
      )
    }
  }
}


