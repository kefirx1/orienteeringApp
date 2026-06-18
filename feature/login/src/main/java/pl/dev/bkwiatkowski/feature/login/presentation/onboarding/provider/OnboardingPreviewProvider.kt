package pl.dev.bkwiatkowski.feature.login.presentation.onboarding.provider

import kotlinx.coroutines.flow.MutableStateFlow
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.OnboardingVM
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.mapper.OnboardingScreenMapper
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.mapper.OnboardingScreenMapperImpl
import java.time.LocalDateTime

class OnboardingPreviewProvider : ViewModelPreviewProvider<OnboardingVM, OnboardingVM.ScreenData, OnboardingScreenMapper.Params>() {
  override val mapper: OnboardingScreenMapper = OnboardingScreenMapperImpl()

  override val values: Sequence<OnboardingVM> = sequenceOf(
    object : OnboardingVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = OnboardingVM.State.Onboarding(
          content = OnboardingVM.StateContent(
            userName = "Jan Kowalski",
            email = "jan@example.com",
            phone = "123456789",
            birthdate = LocalDateTime.now(),
          )
        )))
      )
    },

    object : OnboardingVM {
      override val screenData = MutableStateFlow(
        value = mapper(params = getMapperParams(state = OnboardingVM.State.DatePicker(
          content = OnboardingVM.StateContent(
            userName = "Jan Kowalski",
            email = "jan@example.com",
            phone = "",
            birthdate = LocalDateTime.now(),
          )
        )))
      )
    }
  )


  private fun getMapperParams(state: OnboardingVM.State): OnboardingScreenMapper.Params =
    OnboardingScreenMapper.Params(
      state = state,
      onUserNameValueChanged = {},
      onEmailValueChanged = {},
      onPhoneValueChanged = {},
      onBirthDateValueChanged = {},
      onBirthDateInputClick = {},
      onContinueClick = {},
      onBackClick = {},
    )
}
