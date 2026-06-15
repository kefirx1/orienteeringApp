package pl.dev.bkwiatkowski.feature.login.presentation.onboarding

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldData
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState.Companion.getState
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState.Companion.isValid
import pl.dev.bkwiatkowski.common.ui.component.picker.CustomDatePickerData
import pl.dev.bkwiatkowski.common.ui.component.picker.DatePickerInputData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidateBirthdateUC
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidateEmailUC
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidatePhoneUC
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidateUserNameUC
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.OnboardingVM.Action
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.OnboardingVM.ScreenData
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.OnboardingVM.State
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.mapper.OnboardingScreenMapper
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.SetPasswordVM
import java.time.LocalDateTime
import javax.inject.Inject

interface OnboardingVM {
  data class StateContent(
    val userName: String = "",
    val userNameValidation: ValidationState = ValidationState.UnVerified,
    val email: String = "",
    val emailValidation: ValidationState = ValidationState.UnVerified,
    val phone: String = "",
    val phoneValidation: ValidationState = ValidationState.UnVerified,
    val birthdate: LocalDateTime = LocalDateTime.now(),
    val birthDateValidation: ValidationState = ValidationState.UnVerified,
  )

  sealed interface State {
    val content: StateContent

    data class Onboarding(
      override val content: StateContent,
    ) : State

    data class DatePicker(
      override val content: StateContent,
    ) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
      data class ContinueOnboarding(
        val setupData: SetPasswordVM.SetupData,
      ) : Navigation
    }

    data object OpenBirthDatePicker : Action
    data class UpdateUserName(val userName: String) : Action
    data class UpdateEmail(val email: String) : Action
    data class UpdatePhone(val phone: String) : Action
    data class UpdateBirthDate(val birthDate: LocalDateTime) : Action
    data object OnContinueClick : Action
    data object OnBackClick : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class OnboardingScreen(
      val topAppBarData: TopAppBarData,
      val title: String,
      val subtitle: String,
      val userNameFieldData: TextFieldData,
      val emailFieldData: TextFieldData,
      val phoneFieldData: TextFieldData,
      val birthDateFieldData: DatePickerInputData,
      val continueButton: LargeButtonData,
      val customDatePickerData: CustomDatePickerData?,
      override val onBackClick: () -> Unit,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class OnboardingVMImpl @Inject constructor(
  private val onboardingScreenMapper: OnboardingScreenMapper,
  private val validateUserNameUC: ValidateUserNameUC,
  private val validateEmailUC: ValidateEmailUC,
  private val validatePhoneUC: ValidatePhoneUC,
  private val validateBirthDateUC: ValidateBirthdateUC,
  private val runWithLoaderUC: RunWithLoaderUC,
) : CustomViewModel<State, ScreenData, Action.Navigation>(
  initialStateValue = State.Onboarding(
    content = OnboardingVM.StateContent(),
  ),
), OnboardingVM {
  override val screenData: StateFlow<ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is State.Onboarding -> when (action) {
          is Action.UpdateUserName -> {
            currentState.copy(
              content = currentState.content.copy(
                userName = action.userName,
                userNameValidation = ValidationState.UnVerified,
              )
            ).mutate()
          }
          is Action.UpdateEmail -> {
            currentState.copy(
              content = currentState.content.copy(
                email = action.email,
                emailValidation = ValidationState.UnVerified,
              )
            ).mutate()
          }
          is Action.UpdatePhone -> {
            currentState.copy(
              content = currentState.content.copy(
                phone = action.phone,
                phoneValidation = ValidationState.UnVerified,
              )
            ).mutate()
          }
          is Action.OnContinueClick -> {
            runWithLoaderUC {
              val newUserNameState = validateUserNameUC(params = ValidateUserNameUC.Params(
                userName = currentState.content.userName,
              )).getState()
              val newEmailState = validateEmailUC(params = ValidateEmailUC.Params(
                email = currentState.content.email,
              )).getState()
              val newPhoneState = validatePhoneUC(params = ValidatePhoneUC.Params(
                phone = currentState.content.phone,
              )).getState()
              val newBirthdateState = validateBirthDateUC(params = ValidateBirthdateUC.Params(
                birthdate = currentState.content.birthdate,
              )).getState()

              if (listOf(newUserNameState, newEmailState, newPhoneState, newBirthdateState).all { state ->
                  state.isValid()
                }) {
                Action.Navigation.ContinueOnboarding(
                  setupData = SetPasswordVM.SetupData(
                    userName = currentState.content.userName,
                    email = currentState.content.email,
                    phone = currentState.content.phone.takeIf { it.isNotBlank() },
                    dateOfBirth = currentState.content.birthdate,
                  )
                ).emit()
              }

              currentState.copy(
                content = currentState.content.copy(
                  userNameValidation = newUserNameState,
                  emailValidation = newEmailState,
                  phoneValidation = newPhoneState,
                  birthDateValidation = newBirthdateState,
                )
              ).mutate()
            }
          }
          is Action.OpenBirthDatePicker -> {
            State.DatePicker(
              content = currentState.content,
            ).override()
          }
          is Action.OnBackClick -> {
            Action.Navigation.Back.emit()
          }
          else -> {}
        }
        is State.DatePicker -> when (action) {
          is Action.UpdateBirthDate -> {
            State.Onboarding(
              content = currentState.content.copy(
                birthdate = action.birthDate,
                birthDateValidation = ValidationState.UnVerified,
              )
            ).override()
          }
          is Action.OnBackClick -> {
            State.Onboarding(
              content = currentState.content,
            ).override()
          }
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: State) { }

  override fun mapScreenData() = onboardingScreenMapper(
    params = OnboardingScreenMapper.Params(
      state = state.value,
      onUserNameValueChanged = { userName: String ->
        dispatchAction(Action.UpdateUserName(userName = userName))
      },
      onEmailValueChanged = { email: String ->
        dispatchAction(Action.UpdateEmail(email = email))
      },
      onPhoneValueChanged = { phone: String ->
        dispatchAction(Action.UpdatePhone(phone = phone))
      },
      onBirthDateValueChanged = { birthDate: LocalDateTime ->
        dispatchAction(Action.UpdateBirthDate(birthDate = birthDate))
      },
      onContinueClick = {
        dispatchAction(Action.OnContinueClick)
      },
      onBackClick = {
        dispatchAction(Action.OnBackClick)
      },
      onBirthDateInputClick = {
        dispatchAction(Action.OpenBirthDatePicker)
      },
    ),
  )
}
