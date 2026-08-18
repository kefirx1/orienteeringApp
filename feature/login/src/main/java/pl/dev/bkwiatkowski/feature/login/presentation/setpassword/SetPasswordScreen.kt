package pl.dev.bkwiatkowski.feature.login.presentation.setpassword

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
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.error.ErrorScreen
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.login.presentation.setpassword.provider.SetPasswordPreviewProvider

@Composable
fun SetPasswordScreen(viewModel: SetPasswordVM) {
  val screenData by viewModel.screenData.collectAsStateWithLifecycle()

  when (val data = screenData) {
    is SetPasswordVM.ScreenData.SetPasswordScreen -> SetPasswordScreenContent(data = data)
    is SetPasswordVM.ScreenData.ErrorScreen -> ErrorScreen(data = data.errorData)
  }

  BackHandler {
    screenData.onBackClick()
  }
}

@Composable
fun SetPasswordScreenContent(
  data: SetPasswordVM.ScreenData.SetPasswordScreen,
) {
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

        TextField(textFieldData = data.passwordFieldData)
        Spacer(modifier = Modifier.height(24.dp))

        TextField(textFieldData = data.confirmPasswordFieldData)
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
private fun SetPasswordScreenPreview(
  @PreviewParameter(provider = SetPasswordPreviewProvider ::class) viewModel: SetPasswordVM,
) {
  OrienteeringAppTheme {
    SetPasswordScreen(viewModel = viewModel)
  }
}
