package pl.dev.bkwiatkowski.common.ui.component.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit

@Composable
fun CustomText(
  modifier: Modifier = Modifier,
  text: String,
  style: TextStyle = MaterialTheme.typography.bodyMedium,
  color: Color = MaterialTheme.colorScheme.onBackground,
  customSize: TextUnit? = null,
) {
  Text(
    modifier = modifier,
    text = text,
    color = color,
    style = style.copy(fontSize = customSize ?: style.fontSize),
  )
}