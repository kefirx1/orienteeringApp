package pl.dev.bkwiatkowski.feature.login.presentation.setpassword.mapper

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldType
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.SetPasswordVM
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.mapper.SetPasswordScreenMapper.Params

interface SetPasswordScreenMapper : Mapper<Params, SetPasswordVM.ScreenData> {
  data class Params(
    val state: SetPasswordVM.State,
    val onPasswordValueChanged: (String) -> Unit,
    val onConfirmPasswordValueChanged: (String) -> Unit,
    val onContinueClick: () -> Unit,
    val onBackClick: () -> Unit,
  )
}

class SetPasswordScreenMapperImpl : SetPasswordScreenMapper {
  override fun invoke(params: Params): SetPasswordVM.ScreenData {
    val state = params.state
    return SetPasswordVM.ScreenData.SetPasswordScreen(
      topAppBarData = TopAppBarData.Back(
        onNavigationIconClick = params.onBackClick,
      ),
      title = "Ustaw hasło",
      subtitle = "Hasło musi mieć co najmniej 8 znaków",
      passwordFieldData = TextFieldData(
        label = "Hasło",
        hint = "Wpisz hasło",
        onValueChanged = params.onPasswordValueChanged,
        textFieldType = TextFieldType.Password,
        validationState = state.content.passwordValidation,
      ),
      confirmPasswordFieldData = TextFieldData(
        label = "Potwierdź hasło",
        hint = "Wpisz ponownie hasło",
        onValueChanged = params.onConfirmPasswordValueChanged,
        textFieldType = TextFieldType.Password,
        validationState = state.content.confirmPasswordValidation,
      ),
      continueButton = LargeButtonData.Primary(
        text = "Zarejestruj się",
        onClick = params.onContinueClick,
      ),
      onBackClick = params.onBackClick,
    )
  }
}
