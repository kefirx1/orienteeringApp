package pl.dev.bkwiatkowski.common.ui.component.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.R
import pl.dev.bkwiatkowski.common.ui.component.icon.CustomImage
import pl.dev.bkwiatkowski.common.ui.component.icon.ImageSize
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

data class ActionCardData(
  val content: @Composable () -> Unit,
  val onClick: () -> Unit,
)

@Composable
fun ActionCard(data: ActionCardData) {
  BaseCard {
    Row(
      modifier = Modifier.fillMaxWidth()
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = data.onClick,
        )
        .padding(
          vertical = 10.dp,
          horizontal = 5.dp,
        ),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column {
        data.content()
      }

      CustomImage(
        imageSize = ImageSize.MEDIUM,
        iconRes = R.drawable.baseline_chevron_right_24,
        contentDescription = "Action card arrow",
        color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}

private class ActionCardProvider : PreviewParameterProvider<ActionCardData> {
  override val values: Sequence<ActionCardData> = sequenceOf(
    ActionCardData(
      content = {
        CustomText(text = "Przykładowy tekst")
      },
      onClick = {},
    ),
  )
}

@Preview(name = "Action card preview")
@Composable
fun ActionCardPreview(
  @PreviewParameter(ActionCardProvider::class) data: ActionCardData,
) {
  OrienteeringAppTheme {
    ActionCard(data = data)
  }
}