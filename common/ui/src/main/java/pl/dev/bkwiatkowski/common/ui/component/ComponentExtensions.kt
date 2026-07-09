package pl.dev.bkwiatkowski.common.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.addDefaultPadding(): Modifier = this.padding(
  horizontal = 20.dp,
  vertical = 5.dp
)