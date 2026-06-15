package pl.dev.bkwiatkowski.feature.login.presentation.onboarding.mapper

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldType
import pl.dev.bkwiatkowski.common.ui.component.picker.CustomDatePickerData
import pl.dev.bkwiatkowski.common.ui.component.picker.DatePickerInputData
import pl.dev.bkwiatkowski.common.ui.component.picker.DateValidationMode
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.OnboardingVM
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.mapper.OnboardingScreenMapper.Params
import java.time.LocalDateTime

interface OnboardingScreenMapper : Mapper<Params, OnboardingVM.ScreenData> {
  data class Params(
    val state: OnboardingVM.State,
    val onUserNameValueChanged: (String) -> Unit,
    val onEmailValueChanged: (String) -> Unit,
    val onPhoneValueChanged: (String) -> Unit,
    val onBirthDateValueChanged: (LocalDateTime) -> Unit,
    val onBirthDateInputClick: () -> Unit,
    val onContinueClick: () -> Unit,
    val onBackClick: () -> Unit,
  )
}

class OnboardingScreenMapperImpl : OnboardingScreenMapper {
  override fun invoke(params: Params): OnboardingVM.ScreenData =
    when (val state = params.state) {
      is OnboardingVM.State -> {
        OnboardingVM.ScreenData.OnboardingScreen(
          topAppBarData = TopAppBarData.Back(
            onNavigationIconClick = params.onBackClick,
          ),
          title = "Uzupełnij swoje dane",
          subtitle = "Pomóż nam lepiej Cię poznać",
          userNameFieldData = TextFieldData(
            label = "Nazwa użytkownika",
            hint = "Wpisz np. imię",
            onValueChanged = params.onUserNameValueChanged,
            textFieldType = TextFieldType.Default,
            validationState = state.content.userNameValidation,
          ),
          emailFieldData = TextFieldData(
            label = "Email",
            hint = "Wpisz email",
            onValueChanged = params.onEmailValueChanged,
            textFieldType = TextFieldType.Default,
            validationState = state.content.emailValidation,
          ),
          phoneFieldData = TextFieldData(
            label = "Telefon (Opcjonalne)",
            hint = "Wpisz telefon",
            onValueChanged = params.onPhoneValueChanged,
            textFieldType = TextFieldType.Default,
            validationState = state.content.phoneValidation,
          ),
          birthDateFieldData = DatePickerInputData(
            label = "Data urodzenia",
            pickedDate = params.state.content.birthdate,
            onClick = params.onBirthDateInputClick,
            validationState = state.content.birthDateValidation,
          ),
          continueButton = LargeButtonData.Primary(
            text = "Dalej",
            onClick = params.onContinueClick,
          ),
          onBackClick = params.onBackClick,
          customDatePickerData = if (params.state is OnboardingVM.State.DatePicker) {
            CustomDatePickerData(
              pickerTitle = "Wybierz datę urodzenia",
              pickedDate = state.content.birthdate,
              onDismiss = params.onBackClick,
              onNewDatePicked = params.onBirthDateValueChanged,
              validationMode = DateValidationMode.PastDatesOnly(excludeToday = true),
            )
          } else null,
        )
      }
    }
}
