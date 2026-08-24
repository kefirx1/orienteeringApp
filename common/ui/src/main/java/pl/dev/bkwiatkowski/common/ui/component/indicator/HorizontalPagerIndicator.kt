package pl.dev.bkwiatkowski.common.ui.component.indicator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun HorizontalPagerIndicator(
  pagerState: PagerState,
) {
  val coroutineScope = rememberCoroutineScope()
  val pageCount = pagerState.pageCount

  Row(
    modifier = Modifier.padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    for (i in 0 until pageCount) {
      val isActive = i == pagerState.currentPage
      Box(
        modifier = Modifier
          .padding(horizontal = 4.dp)
          .size(8.dp)
          .background(
            color = if (isActive) MaterialTheme.colorScheme.primary else Color.LightGray,
            shape = CircleShape,
          )
          .clickable {
            coroutineScope.launch { pagerState.animateScrollToPage(i) }
          }
      )
    }
  }
}