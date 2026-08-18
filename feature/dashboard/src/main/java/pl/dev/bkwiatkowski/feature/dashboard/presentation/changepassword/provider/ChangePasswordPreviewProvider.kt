package pl.dev.bkwiatkowski.feature.dashboard.presentation.changepassword.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.dashboard.presentation.changepassword.ChangePasswordMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.changepassword.ChangePasswordMapperImpl
import pl.dev.bkwiatkowski.feature.dashboard.presentation.changepassword.ChangePasswordVM

class ChangePasswordPreviewProvider : ViewModelPreviewProvider<ChangePasswordVM, ChangePasswordVM.ScreenData, ChangePasswordMapper.Params>() {
  override val mapper: ChangePasswordMapper = ChangePasswordMapperImpl()

  override val values: Sequence<ChangePasswordVM> = sequenceOf(
    object : ChangePasswordVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = ChangePasswordVM.State.ChangePassword(ChangePasswordVM.StateContent())))
      )
    }
  )

  private fun getMapperParams(state: ChangePasswordVM.State): ChangePasswordMapper.Params =
    ChangePasswordMapper.Params(
      state = state,
      snackbarHostState = mock.snackbarHost,
      onOldPasswordValueChanged = {},
      onPasswordValueChanged = {},
      onConfirmPasswordValueChanged = {},
      onContinueClick = {},
      onBackClick = {},
    )
}
