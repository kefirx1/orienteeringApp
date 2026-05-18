package pl.dev.bkwiatkowski.common.ui.component.emptyscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun EmptyScreen() {
  Column(
    modifier = Modifier.fillMaxSize()
      .background(color = MaterialTheme.colorScheme.background),
  ){}
}