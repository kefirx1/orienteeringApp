package pl.dev.bkwiatkowski.common.ui.component.picker

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import pl.dev.bkwiatkowski.common.ui.R
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

data class DatePickerInputData(
  val pickedDate: String,
  val onClick: () -> Unit,
)

private const val ACTION_DEBOUNCE = 100L

@OptIn(FlowPreview::class)
@Composable
fun DatePickerInput(data: DatePickerInputData) {
  val interactionSource = remember { MutableInteractionSource() }

  LaunchedEffect(interactionSource) {
    interactionSource.interactions.debounce(ACTION_DEBOUNCE).collect { interaction ->
      when (interaction) {
        is PressInteraction -> data.onClick()
      }
    }
  }

  OutlinedTextField(
    modifier = Modifier
      .fillMaxWidth(),
    readOnly = true,
    value = data.pickedDate,
    onValueChange = {},
    maxLines = 1,
    trailingIcon = {
      Icon(
        painter = painterResource(R.drawable.baseline_date_range_24),
        contentDescription = "Date picker icon",
        tint = MaterialTheme.colorScheme.primary,
      )
    },
    colors = OutlinedTextFieldDefaults.colors().copy(
      focusedTextColor = MaterialTheme.colorScheme.onPrimary,
      unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
      focusedContainerColor = MaterialTheme.colorScheme.background,
      unfocusedContainerColor = MaterialTheme.colorScheme.background,
      focusedIndicatorColor = MaterialTheme.colorScheme.primary,
      unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
      focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
      unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
    ),
    interactionSource = interactionSource,
  )
}

private class DatePickerInputProvider : PreviewParameterProvider<DatePickerInputData> {
  override val values: Sequence<DatePickerInputData> = sequenceOf(
    DatePickerInputData(
      pickedDate = "01-01-2025",
      onClick = {},
    )
  )
}

@Preview(name = "DatePickerInput preview")
@Composable
private fun DatePickerInputPreview(
  @PreviewParameter(DatePickerInputProvider::class) data: DatePickerInputData,
) {
  OrienteeringAppTheme {
    DatePickerInput(data)
  }
}