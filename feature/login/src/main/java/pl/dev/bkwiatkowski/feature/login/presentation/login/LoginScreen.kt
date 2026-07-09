package pl.dev.bkwiatkowski.feature.login.presentation.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButton
import pl.dev.bkwiatkowski.common.ui.component.emptyscreen.EmptyScreen
import pl.dev.bkwiatkowski.common.ui.component.input.TextField
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.login.presentation.login.provider.LoginPreviewProvider

@Composable
fun LoginScreen(viewModel: LoginVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is LoginVM.ScreenData.Initial -> EmptyScreen()
    is LoginVM.ScreenData.NewLoginScreen -> NewUserLoginScreenContent(
      data = screenData,
    )
    is LoginVM.ScreenData.RegistrationScreen -> RegistrationScreenContent(
      data = screenData,
    )
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun NewUserLoginScreenContent(
  data: LoginVM.ScreenData.NewLoginScreen,
) {
  BaseScaffold(
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding()
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Spacer(modifier = Modifier.height(64.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
        ) {
          CustomText(
            text = data.appName,
            style = MaterialTheme.typography.headlineLarge,
            customSize = 40.sp,
          )
        }
        Spacer(modifier = Modifier.height(80.dp))

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp),
          horizontalAlignment = Alignment.Start,
        ) {
          CustomText(
            text = data.infoLabel,
            style = MaterialTheme.typography.titleLarge,
          )
          Spacer(modifier = Modifier.height(20.dp))
        }

        TextField(
          textFieldData = data.usernameInput,
        )
        Spacer(modifier = Modifier.height(24.dp))

        TextField(
          textFieldData = data.passwordInput,
        )
        Spacer(modifier = Modifier.height(24.dp))
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
        LargeButton(buttonData = data.loginButton)
        Spacer(modifier = Modifier.height(12.dp))

        LargeButton(buttonData = data.otherOptionsButton)
      }
    }
  )
}

@Composable
fun RegistrationScreenContent(
  data: LoginVM.ScreenData.RegistrationScreen,
) {
  BaseScaffold(
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding()
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Spacer(modifier = Modifier.height(64.dp))

        CustomText(
          text = data.appName,
          style = MaterialTheme.typography.headlineLarge,
          customSize = 40.sp,
        )
        Spacer(modifier = Modifier.height(80.dp))

        CustomText(
          text = data.description,
          style = MaterialTheme.typography.bodyLarge,
        )
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
        LargeButton(buttonData = data.loginButton)
        Spacer(modifier = Modifier.height(12.dp))

        LargeButton(buttonData = data.registerButton)
        Spacer(modifier = Modifier.height(12.dp))

        LargeButton(buttonData = data.guestButton)
      }
    }
  )
}

@Preview
@Composable
private fun LoginScreenPreview(
  @PreviewParameter(provider = LoginPreviewProvider::class) viewModel: LoginVM,
) {
  OrienteeringAppTheme {
    LoginScreen(viewModel = viewModel)
  }
}

