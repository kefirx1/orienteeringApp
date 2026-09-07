package pl.dev.bkwiatkowski.common.ui.component.switch

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

data class SwitchData(
  val isChecked: Boolean,
  val onCheckedChange: (Boolean) -> Unit,
)

@Composable
fun Switch(data: SwitchData) {
  Switch(
    checked = data.isChecked,
    onCheckedChange = data.onCheckedChange,
  )
}

private class SwitchProvider : PreviewParameterProvider<SwitchData> {
  override val values: Sequence<SwitchData> = sequenceOf(
    SwitchData(
      isChecked = true,
      onCheckedChange = {},
    ),
    SwitchData(
      isChecked = false,
      onCheckedChange = {},
    ),
  )
}

@Preview(name = "Switch preview")
@Composable
private fun SwitchPreview(
  @PreviewParameter(SwitchProvider::class) data: SwitchData,
) {
  OrienteeringAppTheme {
    Switch(data)
  }
}