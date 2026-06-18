package pl.dev.bkwiatkowski.feature.login.presentation.setpassword.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.SetPasswordVM
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.mapper.SetPasswordScreenMapper
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.mapper.SetPasswordScreenMapperImpl

class SetPasswordPreviewProvider : ViewModelPreviewProvider<SetPasswordVM, SetPasswordVM.ScreenData, SetPasswordScreenMapper.Params>() {
  override val mapper: SetPasswordScreenMapper = SetPasswordScreenMapperImpl()

  override val values: Sequence<SetPasswordVM> = sequenceOf(
    object : SetPasswordVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = SetPasswordVM.State.SetPassword(
          content = SetPasswordVM.StateContent(
            password = "",
            confirmPassword = "",
          )
        )))
      )
    },

    object : SetPasswordVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = SetPasswordVM.State.SetPassword(
          content = SetPasswordVM.StateContent(
            password = "password123",
            confirmPassword = "password123",
          )
        )))
      )
    }
  )

  private fun getMapperParams(state: SetPasswordVM.State): SetPasswordScreenMapper.Params =
    SetPasswordScreenMapper.Params(
      state = state,
      onPasswordValueChanged = {},
      onConfirmPasswordValueChanged = {},
      onContinueClick = {},
      onBackClick = {},
    )
}
