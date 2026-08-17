package pl.dev.bkwiatkowski.common.ui.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.dev.bkwiatkowski.common.ui.R
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButton
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.icon.CustomImage
import pl.dev.bkwiatkowski.common.ui.component.icon.ImageSize
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

data class ErrorScreenData(
  val customMessage: String?,
  val onCloseButtonClick: () -> Unit,
  val onRetryButtonClick: (() -> Unit)?,
)

@Composable
fun ErrorScreen(
  data: ErrorScreenData,
) {
  BaseScaffold(
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {

        CustomImage(
          iconRes = R.drawable.outline_error_24,
          imageSize = ImageSize.EXTRA_LARGE,
          color = MaterialTheme.colorScheme.tertiary,
        )
        Spacer(modifier = Modifier.height(24.dp))

        CustomText(
          text = data.customMessage ?: "Wstąpił błąd",
          style = MaterialTheme.typography.titleLarge,
        )
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier.padding(
          horizontal = 20.dp,
          vertical = 10.dp,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        if (data.onRetryButtonClick != null) {
          LargeButton(
            buttonData = LargeButtonData.Primary(
              text = "Spróbuj ponownie",
              onClick = data.onRetryButtonClick,
            ),
          )
          Spacer(modifier = Modifier.height(12.dp))

          LargeButton(
            buttonData = LargeButtonData.Secondary(
              text = "Zamknij",
              onClick = data.onCloseButtonClick,
            ),
          )
        } else {
          LargeButton(
            buttonData = LargeButtonData.Primary(
              text = "Zamknij",
              onClick = data.onCloseButtonClick,
            ),
          )
        }
      }
    }
  )
}

@Preview(name = "ErrorScreen preview")
@Composable
fun ErrorScreenPreview() {
  OrienteeringAppTheme {
    ErrorScreen(
      data = ErrorScreenData(
        customMessage = "Tutaj jest błąd",
        onCloseButtonClick = {},
        onRetryButtonClick = {},
      ),
    )
  }
}