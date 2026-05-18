package pl.dev.bkwiatkowski.common.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

@Composable
fun BaseCard(
  content: @Composable () -> Unit,
) {
  Column(
    modifier = Modifier
      .shadow(elevation = 4.dp)
      .background(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(5.dp),
      )
      .padding(
        horizontal = 10.dp,
        vertical = 5.dp,
      )
  ) {
    content()
  }
}

class BaseCardPreviewProvider : PreviewParameterProvider<@Composable () -> Unit> {
  override val values: Sequence<@Composable (() -> Unit)> = sequenceOf(
    element = {
      CustomText(text = "Basic Card")
    }
  )
}


@Preview(name = "BaseCard preview")
@Composable
fun BaseCardPreview(
  @PreviewParameter(BaseCardPreviewProvider::class) content: @Composable () -> Unit,
) {
  OrienteeringAppTheme {
    BaseCard(content = content)
  }
}