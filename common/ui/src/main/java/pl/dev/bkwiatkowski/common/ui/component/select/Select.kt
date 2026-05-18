package pl.dev.bkwiatkowski.common.ui.component.select

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

data class SelectItemData(
  val id: Int,
  val label: String,
)

data class SelectData(
  val content: List<SelectItemData>,
  val selectedOption: Int,
  val onSelect: (Int) -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Select(data: SelectData) {
  var isExpanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    modifier = Modifier.fillMaxWidth(),
    expanded = isExpanded,
    onExpandedChange = { isExpanded = it },
  ) {
    OutlinedTextField(
      modifier = Modifier
        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        .fillMaxWidth(),
      readOnly = true,
      value = data.content.find { it.id == data.selectedOption }?.label ?: "",
      onValueChange = {},
      maxLines = 1,
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
      colors = ExposedDropdownMenuDefaults.textFieldColors(
        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
        focusedContainerColor = MaterialTheme.colorScheme.background,
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
      ),
    )

    ExposedDropdownMenu(
      modifier = Modifier,
      expanded = isExpanded,
      onDismissRequest = { isExpanded = false },
    ) {
      data.content.forEachIndexed { index, option ->
        DropdownMenuItem(
          text = { Text(option.label) },
          onClick = {
            data.onSelect(option.id)
            isExpanded = false
          },
          contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        )
      }
    }
  }
}

private class SelectProvider : PreviewParameterProvider<SelectData> {
  override val values: Sequence<SelectData> = sequenceOf(
    SelectData(
      content = listOf(
        SelectItemData(label = "Option 1", id = 1),
        SelectItemData(label = "Option 2", id = 2),
        SelectItemData(label = "Option 3", id = 3),
      ),
      selectedOption = 1,
      onSelect = {},
    )
  )
}

@Preview(name = "Select preview")
@Composable
private fun SelectPreview(
  @PreviewParameter(SelectProvider::class) data: SelectData,
) {
  OrienteeringAppTheme {
    Select(data)
  }
}