package pl.dev.bkwiatkowski.common.ui.component.chips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

data class CustomFilterChipsData(
  val chipsText: String,
  val onChipsSelect: (Boolean) -> Unit,
  val selected: Boolean,
)

data class CustomFilterChipsListData(
  val chipsList: List<CustomFilterChipsData>,
  val wrap: Boolean = false,
)

@Composable
fun CustomFilterChips(
  chipsList: CustomFilterChipsListData,
) {
  if (chipsList.wrap) {
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
      chipsList.chipsList.forEachIndexed { index, chipsData ->
        var selected by remember { mutableStateOf(chipsData.selected) }

        FilterChip(
          selected = selected,
          onClick = {
            selected = !selected
            chipsList.chipsList[index].onChipsSelect(selected)
          },
          label = {
            CustomText(
              modifier = Modifier.padding(vertical = 10.dp),
              text = chipsList.chipsList[index].chipsText,
              style = MaterialTheme.typography.bodyMedium
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            labelColor = MaterialTheme.colorScheme.onPrimary
          )
        )
      }
    }
  } else {
    LazyRow {
      items(chipsList.chipsList.size) { index ->
        var selected by remember { mutableStateOf(false) }

        FilterChip(
          selected = selected,
          onClick = {
            selected = !selected
            chipsList.chipsList[index].onChipsSelect
          },
          label = {
            CustomText(
              modifier = Modifier.padding(vertical = 10.dp),
              text = chipsList.chipsList[index].chipsText,
              style = MaterialTheme.typography.bodyMedium
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            labelColor = MaterialTheme.colorScheme.onPrimary
          )
        )
        
        if (index != chipsList.chipsList.size - 1) {
          Spacer(modifier = Modifier.width(10.dp))
        }
      }
    }
  }
}

private class CustomFilterChipsProvider : PreviewParameterProvider<CustomFilterChipsListData> {
  override val values: Sequence<CustomFilterChipsListData> = sequenceOf(
    CustomFilterChipsListData(
      chipsList = listOf(
        CustomFilterChipsData(
          chipsText = "SingleChips",
          onChipsSelect = {},
          selected = false,
        ),
        CustomFilterChipsData(
          chipsText = "SingleChips2",
          onChipsSelect = {},
          selected = false,
        ),
        CustomFilterChipsData(
          chipsText = "SingleChips3",
          onChipsSelect = {},
          selected = false,
        )
      )
    ),
    CustomFilterChipsListData(
      chipsList = listOf(
        CustomFilterChipsData(
          chipsText = "SingleChips",
          onChipsSelect = {},
          selected = false,
        ),
        CustomFilterChipsData(
          chipsText = "SingleChips2",
          onChipsSelect = {},
          selected = false,
        ),
        CustomFilterChipsData(
          chipsText = "SingleChips3",
          onChipsSelect = {},
          selected = false,
        ),
        CustomFilterChipsData(
          chipsText = "SingleChips4",
          onChipsSelect = {},
          selected = false,
        ),
        CustomFilterChipsData(
          chipsText = "SingleChips5",
          onChipsSelect = {},
          selected = true,
        )
      ),
      wrap = true,
    ),
  )
}

@Preview(name = "CustomFilterChips preview")
@Composable
fun CustomFilterChipsPreview(
  @PreviewParameter(CustomFilterChipsProvider::class) customFilterChipsData: CustomFilterChipsListData,
) {
  OrienteeringAppTheme {
    CustomFilterChips(chipsList = customFilterChipsData)
  }
}