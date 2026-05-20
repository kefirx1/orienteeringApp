package pl.dev.bkwiatkowski.feature.login.presentation.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.R
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButton
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButton
import pl.dev.bkwiatkowski.common.ui.component.emptyscreen.EmptyScreen
import pl.dev.bkwiatkowski.common.ui.component.icon.CustomImage
import pl.dev.bkwiatkowski.common.ui.component.icon.ImageSize
import pl.dev.bkwiatkowski.common.ui.component.input.TextField
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText

@Composable
fun LoginScreen(viewModel: LoginVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is LoginVM.ScreenData.Initial -> EmptyScreen()
    is LoginVM.ScreenData.BiometricScreen -> BiometricScreenContent(
      data = screenData,
    )
    is LoginVM.ScreenData.LoginScreen -> LoginScreenContent(
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
fun BiometricScreenContent(
  data: LoginVM.ScreenData.BiometricScreen,
) {
  BaseScaffold(
    content = {
      Column(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Spacer(modifier = Modifier.height(70.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
        ) {
          CustomText(
            text = data.appName,
            style = MaterialTheme.typography.headlineLarge,
            customSize = 40.sp,
          )
        }
        Spacer(modifier = Modifier.height(200.dp))

        CustomText(
          text = data.loginBiometricLabel,
          style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(40.dp))

        IconButton(
          modifier = Modifier.size(ImageSize.EXTRA_LARGE.size),
          onClick = data.onBiometricOpenClick,
        ) {
          CustomImage(
            iconRes = R.drawable.outline_fingerprint_24,
            imageSize = ImageSize.EXTRA_LARGE,
            color = MaterialTheme.colorScheme.primary,
            contentDescription = "Biometric open button",
          )
        }
        Spacer(modifier = Modifier.height(10.dp))

        SmallButton(buttonData = data.passwordLoginButtonData)
      }
    },
    bottomBar = {}
  )
}

@Composable
fun LoginScreenContent(
  data: LoginVM.ScreenData.LoginScreen,
) {
  BaseScaffold(
    content = {
      Column(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Spacer(modifier = Modifier.height(70.dp))

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
            text = data.welcomeLabel,
            style = MaterialTheme.typography.titleLarge,
          )
          Spacer(modifier = Modifier.height(20.dp))
        }

        TextField(
          textFieldData = data.textFieldData,
        )
        Spacer(modifier = Modifier.height(10.dp))

        data.biometricLoginButtonData?.let { button ->
          SmallButton(buttonData = button)
        }
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier.padding(
          horizontal = 40.dp,
          vertical = 10.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        LargeButton(buttonData = data.buttonData)
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
          .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Spacer(modifier = Modifier.height(70.dp))

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
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier.padding(
          horizontal = 40.dp,
          vertical = 10.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        LargeButton(buttonData = data.buttonData)
      }
    }
  )
}

