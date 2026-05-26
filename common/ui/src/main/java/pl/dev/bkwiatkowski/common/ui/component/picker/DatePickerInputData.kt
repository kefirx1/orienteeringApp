package pl.dev.bkwiatkowski.common.ui.component.picker

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import pl.dev.bkwiatkowski.common.ui.R
import pl.dev.bkwiatkowski.common.ui.component.card.BaseCard
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState.Companion.isValid
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class DatePickerInputData(
  val pickedDate: LocalDateTime,
  val label: String? = null,
  val onClick: () -> Unit,
  val validationState: ValidationState = ValidationState.UnVerified,
)

private const val ACTION_DEBOUNCE = 100L

@OptIn(FlowPreview::class)
@Composable
fun DatePickerInput(
  data: DatePickerInputData,
  modifier: Modifier = Modifier,
) {
  val interactionSource = remember { MutableInteractionSource() }

  LaunchedEffect(interactionSource) {
    interactionSource.interactions.debounce(ACTION_DEBOUNCE).collect { interaction ->
      when (interaction) {
        is PressInteraction -> data.onClick()
      }
    }
  }

  BaseCard {
    Column(
      modifier = modifier
        .fillMaxWidth()
        .padding(
          vertical = 8.dp,
          horizontal = 8.dp,
        )
    ) {
      data.label?.let { label ->
        CustomText(
          modifier = Modifier.padding(start = 4.dp),
          text = label,
          style = MaterialTheme.typography.labelMedium,
        )
        Spacer(modifier = Modifier.height(4.dp))
      }

       OutlinedTextField(
         modifier = Modifier
           .fillMaxWidth(),
         shape = RoundedCornerShape(8.dp),
         readOnly = true,
         value = data.pickedDate.getFormattedDate(),
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
           errorSupportingTextColor = MaterialTheme.colorScheme.onPrimary,
           errorTextColor = MaterialTheme.colorScheme.onPrimary,
           focusedTextColor = MaterialTheme.colorScheme.onPrimary,
           disabledTextColor = MaterialTheme.colorScheme.onPrimary,
           unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
           errorContainerColor = MaterialTheme.colorScheme.background,
           focusedContainerColor = MaterialTheme.colorScheme.background,
           disabledContainerColor = MaterialTheme.colorScheme.background,
           unfocusedContainerColor = MaterialTheme.colorScheme.background,
         ),
         interactionSource = interactionSource,
         isError = !data.validationState.isValid()
       )
       when (data.validationState) {
         ValidationState.UnVerified -> {}
         ValidationState.Valid -> {}
         is ValidationState.Invalid -> {
           Spacer(modifier = Modifier.height(4.dp))

           CustomText(
             modifier = Modifier.padding(start = 4.dp),
             text = data.validationState.message,
             style = MaterialTheme.typography.labelSmall,
             color = MaterialTheme.colorScheme.error,
           )
         }
       }
    }
  }
}

private fun LocalDateTime.getFormattedDate(): String =
  this.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

private class DatePickerInputProvider : PreviewParameterProvider<DatePickerInputData> {
  override val values: Sequence<DatePickerInputData> = sequenceOf(
    DatePickerInputData(
      pickedDate = LocalDateTime.of(2025,1,1, 0,0),
      onClick = {},
      label = "Wybierz date wylotu",
    ),
    DatePickerInputData(
      pickedDate = LocalDateTime.of(2025,1,1, 0,0),
      onClick = {},
      label = "Wybierz date wylotu",
      validationState = ValidationState.Invalid(message = "Nieprawidłowa data"),
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