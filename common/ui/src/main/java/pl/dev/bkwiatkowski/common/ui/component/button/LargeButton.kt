package pl.dev.bkwiatkowski.common.ui.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.AppColors
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

sealed class LargeButtonData(
  open val text: String,
  open val onClick: () -> Unit,
) {
  data class Primary(
    override val text: String,
    override val onClick: () -> Unit,
  ) : LargeButtonData(
    text = text,
    onClick = onClick,
  )

  data class Secondary(
    override val text: String,
    override val onClick: () -> Unit,
  ) : LargeButtonData(
    text = text,
    onClick = onClick,
  )

  data class Tertiary(
    override val text: String,
    override val onClick: () -> Unit,
  ) : LargeButtonData(
    text = text,
    onClick = onClick,
  )
}

private const val DEBOUNCE_DELAY = 500L

@Composable
fun LargeButton(
  modifier: Modifier = Modifier,
  buttonData: LargeButtonData,
) {
  var lastClick by remember { mutableLongStateOf(0L) }
  val keyboardController = LocalSoftwareKeyboardController.current
  val focusManager = LocalFocusManager.current

  Button(
    modifier = modifier.fillMaxWidth(),
    onClick = {
      val currentTime = System.currentTimeMillis()
      if (currentTime - lastClick > DEBOUNCE_DELAY) {
        lastClick = currentTime
        focusManager.clearFocus()
        keyboardController?.hide()
        buttonData.onClick()
      }
    },
    shape = when (buttonData) {
      is LargeButtonData.Primary -> ButtonDefaults.shape
      is LargeButtonData.Secondary -> ButtonDefaults.shape
      is LargeButtonData.Tertiary -> ButtonDefaults.outlinedShape
    },
    border = when (buttonData) {
      is LargeButtonData.Primary,
      is LargeButtonData.Secondary -> null
      is LargeButtonData.Tertiary -> BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary,
      )
    },
    colors = ButtonDefaults.buttonColors(
      containerColor = when (buttonData) {
        is LargeButtonData.Primary -> MaterialTheme.colorScheme.primary
        is LargeButtonData.Secondary -> MaterialTheme.colorScheme.secondary
        is LargeButtonData.Tertiary -> AppColors.transparent
      },
    ),
    content = {
      CustomText(
        modifier = Modifier.padding(vertical = 5.dp),
        text = buttonData.text,
        style = MaterialTheme.typography.labelLarge.copy(
          fontSize = 20.sp,
        ),
        color = when (buttonData) {
          is LargeButtonData.Primary -> MaterialTheme.colorScheme.onPrimary
          is LargeButtonData.Secondary -> MaterialTheme.colorScheme.onSecondary
          is LargeButtonData.Tertiary -> MaterialTheme.colorScheme.onTertiary
        }
      )
    }
  )
}

private class LargeButtonPreviewProvider : PreviewParameterProvider<LargeButtonData> {
  override val values: Sequence<LargeButtonData> = sequenceOf(
    LargeButtonData.Primary(
      text = "button primary",
      onClick = {},
    ),
    LargeButtonData.Secondary(
      text = "button secondary",
      onClick = {},
    ),
    LargeButtonData.Tertiary(
      text = "button tertiary",
      onClick = {},
    ),
  )
}

@Preview(name = "LargeButton preview")
@Composable
private fun LargeButtonPreview(
  @PreviewParameter(LargeButtonPreviewProvider::class) buttonData: LargeButtonData,
) {
  OrienteeringAppTheme {
    LargeButton(buttonData = buttonData)
  }
}