package pl.dev.bkwiatkowski.common.loader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.flow.Flow

@Composable
fun Loader(
  visibility: Flow<Boolean>,
) {
  var loading by remember { mutableStateOf(false) }

  LaunchedEffect(
    key1 = visibility,
  ) {
    visibility.collect { value ->
      loading = value
    }

  }

  if (!loading) return

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(
        color = Color.Black.copy(alpha = 0.5f),
      )
      .clickable(enabled = false) {}
      .zIndex(1f),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    CircularProgressIndicator(
      modifier = Modifier.size(64.dp),
      color = MaterialTheme.colorScheme.secondary,
      trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )

  }
}