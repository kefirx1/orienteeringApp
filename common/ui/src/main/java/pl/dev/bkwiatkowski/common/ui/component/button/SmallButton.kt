package pl.dev.bkwiatkowski.common.ui.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.AppColors
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

sealed class SmallButtonData(
  open val text: String,
  open val onClick: () -> Unit,
) {
  data class Primary(
    override val text: String,
    override val onClick: () -> Unit,
  ) : SmallButtonData(
    text = text,
    onClick = onClick,
  )

  data class Secondary(
    override val text: String,
    override val onClick: () -> Unit,
  ) : SmallButtonData(
    text = text,
    onClick = onClick,
  )


  data class Tertiary(
    override val text: String,
    override val onClick: () -> Unit,
  ) : SmallButtonData(
    text = text,
    onClick = onClick,
  )
}

private const val DEBOUNCE_DELAY = 500L

@Composable
fun SmallButton(
  modifier: Modifier = Modifier,
  buttonData: SmallButtonData,
) {
  var lastClick by remember { mutableLongStateOf(0L) }

  Button(
    modifier = modifier,
    onClick = {
      val currentTime = System.currentTimeMillis()
      if (currentTime - lastClick > DEBOUNCE_DELAY) {
        lastClick = currentTime
        buttonData.onClick()
      }
    },
    shape = when (buttonData) {
      is SmallButtonData.Primary -> ButtonDefaults.shape
      is SmallButtonData.Secondary -> ButtonDefaults.outlinedShape
      is SmallButtonData.Tertiary -> ButtonDefaults.shape
    },
    border = when (buttonData) {
      is SmallButtonData.Primary -> null
      is SmallButtonData.Secondary -> BorderStroke(
        width = (0.5).dp,
        color = MaterialTheme.colorScheme.secondary
      )
      is SmallButtonData.Tertiary -> null
    },
    colors = ButtonDefaults.buttonColors().copy(
      containerColor = when (buttonData) {
        is SmallButtonData.Primary -> MaterialTheme.colorScheme.primary
        is SmallButtonData.Secondary -> AppColors.transparent
        is SmallButtonData.Tertiary -> AppColors.transparent
      },
    ),
    contentPadding = ButtonDefaults.TextButtonContentPadding,
    content = {
      CustomText(
        text = buttonData.text,
        style = MaterialTheme.typography.bodyMedium,
        color = when (buttonData) {
          is SmallButtonData.Primary -> MaterialTheme.colorScheme.onPrimary
          is SmallButtonData.Secondary -> MaterialTheme.colorScheme.primary
          is SmallButtonData.Tertiary -> MaterialTheme.colorScheme.primary
        },
      )
    }
  )
}

private class SmallButtonPreviewProvider : PreviewParameterProvider<SmallButtonData> {
  override val values: Sequence<SmallButtonData> = sequenceOf(
    SmallButtonData.Primary(
      text = "button primary",
      onClick = {},
    ),
    SmallButtonData.Secondary(
      text = "button secondary",
      onClick = {},
    ),
    SmallButtonData.Tertiary(
      text = "button tertiary",
      onClick = {},
    ),
  )
}

@Preview(name = "SmallButton preview")
@Composable
private fun SmallButtonPreview(
  @PreviewParameter(SmallButtonPreviewProvider::class) buttonData: SmallButtonData,
) {
  OrienteeringAppTheme(darkTheme = true) {
    SmallButton(buttonData = buttonData)
  }
}