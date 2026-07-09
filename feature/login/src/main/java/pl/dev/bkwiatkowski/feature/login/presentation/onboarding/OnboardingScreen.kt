package pl.dev.bkwiatkowski.feature.login.presentation.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButton
import pl.dev.bkwiatkowski.common.ui.component.input.TextField
import pl.dev.bkwiatkowski.common.ui.component.picker.CustomDatePicker
import pl.dev.bkwiatkowski.common.ui.component.picker.DatePickerInput
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.provider.OnboardingPreviewProvider

@Composable
fun OnboardingScreen(viewModel: OnboardingVM) {
  val screenData by viewModel.screenData.collectAsStateWithLifecycle()

  when (val data = screenData) {
    is OnboardingVM.ScreenData.OnboardingScreen -> OnboardingScreenContent(data = data)
  }

  BackHandler {
    screenData.onBackClick()
  }
}

@Composable
fun OnboardingScreenContent(
  data: OnboardingVM.ScreenData.OnboardingScreen,
) {
  data.customDatePickerData?.let { picker ->
    CustomDatePicker(data = picker)
  }

  BaseScaffold(
    topBarData = data.topAppBarData,
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding()
          .verticalScroll(rememberScrollState()),
      ) {
        CustomText(
          text = data.title,
          style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomText(
          text = data.subtitle,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(48.dp))

        TextField(textFieldData = data.userNameFieldData)
        Spacer(modifier = Modifier.height(24.dp))

        TextField(textFieldData = data.emailFieldData)
        Spacer(modifier = Modifier.height(24.dp))

        TextField(textFieldData = data.phoneFieldData)
        Spacer(modifier = Modifier.height(24.dp))

        DatePickerInput(data = data.birthDateFieldData)
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier.padding(
          horizontal = 20.dp,
          vertical = 10.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        LargeButton(buttonData = data.continueButton)
      }
    }
  )
}

@Preview
@Composable
private fun OnboardingScreenPreview(
  @PreviewParameter(provider = OnboardingPreviewProvider ::class) viewModel: OnboardingVM,
) {
  OrienteeringAppTheme {
    OnboardingScreen(viewModel = viewModel)
  }
}
