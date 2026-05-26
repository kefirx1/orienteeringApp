package pl.dev.bkwiatkowski.common.ui.component.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButton
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.card.BaseCard
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState.Companion.isValid
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

sealed interface ValidationState {
  data object UnVerified : ValidationState
  data object Valid : ValidationState
  data class Invalid(
    val message: String,
  ) : ValidationState

  companion object {
    fun ValidationState.isValid() = when (this) {
      UnVerified -> true
      Valid -> true
      is Invalid -> false
    }

    fun ValidationResult.getState() = when (this) {
      is ValidationResult.Invalid -> Invalid(message = message)
      ValidationResult.Valid -> Valid
    }
  }
}

sealed interface TextFieldType {
  data object Default : TextFieldType
  data object Password : TextFieldType
  data object Number : TextFieldType
}

data class TextFieldData(
  val onValueChanged: (String) -> Unit = {},
  val onFocusChanged: (Boolean) -> Unit = {},
  val hint: String = "",
  val label: String? = null,
  val initialText: String = "",
  val linkTextButton: SmallButtonData.Tertiary? = null,
  val validationState: ValidationState = ValidationState.UnVerified,
  val textFieldType: TextFieldType = TextFieldType.Default,
)

@Composable
fun TextField(
  modifier: Modifier = Modifier,
  textFieldData: TextFieldData
) {
  var textValue by remember { mutableStateOf(TextFieldValue(text = textFieldData.initialText)) }
  var hasFocus by remember { mutableStateOf(false) }
  val viewRequester = remember { BringIntoViewRequester() }
  val scope = rememberCoroutineScope()

  BaseCard {
    Column(
      modifier = modifier.padding(
        vertical = 8.dp,
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(
            horizontal = 8.dp,
          )
      ) {
        textFieldData.label?.let { label ->
          CustomText(
            modifier = Modifier.padding(start = 4.dp),
            text = label,
            style = MaterialTheme.typography.labelMedium,
          )
          Spacer(modifier = Modifier.height(4.dp))
        }

        TextField(
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(viewRequester)
            .onFocusEvent { focusState ->
              if (focusState.isFocused) {
                hasFocus = true

                scope.launch {
                  viewRequester.bringIntoView()
                }
              }


              if (hasFocus) {
                textFieldData.onFocusChanged(focusState.isFocused)
              }
            },
          placeholder = {
            CustomText(
              text = textFieldData.hint,
              color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
            )
          },
          value = textValue,
          onValueChange = { text ->
            textValue = text
            textFieldData.onValueChanged(text.text)
          },
          visualTransformation = when (textFieldData.textFieldType) {
            TextFieldType.Default -> VisualTransformation.None
            TextFieldType.Password -> PasswordVisualTransformation()
            TextFieldType.Number -> VisualTransformation.None
          },
          keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = when (textFieldData.textFieldType) {
              TextFieldType.Default -> KeyboardType.Text
              TextFieldType.Password -> KeyboardType.Password
              TextFieldType.Number -> KeyboardType.Number
            }
          ),
          singleLine = true,
          colors = TextFieldDefaults.colors().copy(
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
          isError = !textFieldData.validationState.isValid()
        )
        when (textFieldData.validationState) {
          ValidationState.UnVerified -> {}
          ValidationState.Valid -> {}
          is ValidationState.Invalid -> {
            Spacer(modifier = Modifier.height(4.dp))

            CustomText(
              modifier = Modifier.padding(start = 4.dp),
              text = textFieldData.validationState.message,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.error,
            )
          }
        }
      }

      textFieldData.linkTextButton?.let { button ->
        Spacer(modifier = Modifier.height(4.dp))

        SmallButton(buttonData = button)
      }
    }
  }
}

private class TextFieldProvider : PreviewParameterProvider<TextFieldData> {
  override val values: Sequence<TextFieldData> = sequenceOf(
    TextFieldData(
      label = "Labelka ładna",
      hint = "hint",
    ),
    TextFieldData(
      hint = "hint",
      initialText = "text input",
      validationState = ValidationState.Invalid(message = "Nieprawidłowe dane"),
      linkTextButton = SmallButtonData.Tertiary(
        text = "Zapomniane hasło?",
        onClick = {},
      )
    )
  )
}

@Preview(name = "TextField preview")
@Composable
fun TextFieldPreview(
  @PreviewParameter(provider = TextFieldProvider::class) textFieldData: TextFieldData,
) {
  OrienteeringAppTheme {
    TextField(textFieldData = textFieldData)
  }
}