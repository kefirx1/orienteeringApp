package pl.dev.bkwiatkowski.common.ui.component.permissions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButton
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.card.BaseCard
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

data class PermissionRequesterData(
  val label: String,
  val isDeniedForever: Boolean,
  val onOpenSettingsClick: () -> Unit,
  val requestPermissionButtonData: SmallButtonData.Secondary,
)

@Composable
fun PermissionRequester(data: PermissionRequesterData) {

  BaseCard {
    Column(
      modifier = Modifier
        .padding(
          vertical = 20.dp,
          horizontal = 20.dp,
        ),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      CustomText(
        text = data.label,
        style = MaterialTheme.typography.bodyMedium,
      )
      Spacer(modifier = Modifier.height(15.dp))

      when (data.isDeniedForever) {
        true -> {
          SmallButton(
            buttonData = SmallButtonData.Secondary(
              text = "Przejdź do ustawień aplikacji",
              onClick = data.onOpenSettingsClick,
            )
          )
        }
        false -> {
          SmallButton(buttonData = data.requestPermissionButtonData)
        }
      }
    }
  }
}

private class PermissionRequesterPreview : PreviewParameterProvider<PermissionRequesterData> {
  override val values: Sequence<PermissionRequesterData> = sequenceOf(
    PermissionRequesterData(
      label = "Aby użyć map potrzebujesz zezwolić na lokalizację",
      isDeniedForever = true,
      onOpenSettingsClick = {},
      requestPermissionButtonData = SmallButtonData.Secondary(
        text = "Zezwól na lokalizację",
        onClick = {},
      )
    ),
    PermissionRequesterData(
      label = "Aby użyć map potrzebujesz zezwolić na lokalizację",
      isDeniedForever = false,
      onOpenSettingsClick = {},
      requestPermissionButtonData = SmallButtonData.Secondary(
        text = "Zezwól na lokalizację",
        onClick = {},
      )
    )
  )
}

@Preview(name = "PermissionRequester preview")
@Composable
fun PermissionRequesterPreview(
  @PreviewParameter(PermissionRequesterPreview::class) data: PermissionRequesterData,
) {
  OrienteeringAppTheme {
    PermissionRequester(data = data)
  }
}