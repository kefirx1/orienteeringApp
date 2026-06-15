package pl.dev.bkwiatkowski.feature.login.presentation.login.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.login.presentation.login.LoginVM
import pl.dev.bkwiatkowski.feature.login.presentation.login.mapper.LoginScreenMapper
import pl.dev.bkwiatkowski.feature.login.presentation.login.mapper.LoginScreenMapperImpl

class LoginPreviewProvider : ViewModelPreviewProvider<LoginVM, LoginVM.ScreenData, LoginScreenMapper.Params>() {
  override val mapper: LoginScreenMapper = LoginScreenMapperImpl()

  override val values: Sequence<LoginVM> = sequenceOf(
    object : LoginVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = LoginVM.State.Login(userName = "Jan Kowalski"))),
      )
    },

    object : LoginVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = LoginVM.State.Biometric(userName = "Jan Kowalski")))
      )
    },

    object : LoginVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = LoginVM.State.Registration))
      )
    }
  )


  private fun getMapperParams(state: LoginVM.State): LoginScreenMapper.Params =
    LoginScreenMapper.Params(
      state = state,
      onBiometricOpenClick = {},
      onLoginClick = {},
      onGoToLoginClick = {},
      onGoToOtherClick = {},
      onGoToRegistrationClick = {},
      onPlayAsGuestClick = {},
      onPasswordValueChanged = {},
      onBackClick = {},
      onToPasswordLoginClick = {},
      onToBiometricLoginClick = {}
    )
}