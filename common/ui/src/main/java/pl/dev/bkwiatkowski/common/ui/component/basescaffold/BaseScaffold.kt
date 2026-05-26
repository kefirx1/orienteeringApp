package pl.dev.bkwiatkowski.common.ui.component.basescaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButton
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.tab.CustomTopAppBar
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

data class FabData(
  val fab: @Composable () -> Unit = {},
  val fabPosition: FabPosition = FabPosition.End,
)

@Composable
fun BaseScaffold(
  modifier: Modifier = Modifier,
  topBarData: TopAppBarData? = null,
  bottomBar: @Composable () -> Unit = {},
  fabData: FabData? = null,
  content: @Composable () -> Unit,
) {
  Scaffold(
    modifier = modifier.imePadding(),
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      topBarData?.let {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background),
        ) {
          CustomTopAppBar(topAppBarData = topBarData)
        }
      }
    },
    content = { padding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .background(color = MaterialTheme.colorScheme.background)
          .padding(paddingValues = padding)
          .padding(
            horizontal = 20.dp,
            vertical = 5.dp,
          )
      ) {
        content()
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier
          .windowInsetsPadding(insets = WindowInsets.navigationBars)
          .fillMaxWidth()
          .background(color = MaterialTheme.colorScheme.background)
      ) {
        bottomBar()
      }
    },
    floatingActionButton = fabData?.fab ?: {},
    floatingActionButtonPosition = fabData?.fabPosition ?: FabPosition.End,
  )
}

@Preview(name = "BaseScaffold preview")
@Composable
fun BaseScaffoldPreview() {
  OrienteeringAppTheme {
    BaseScaffold(
      topBarData = TopAppBarData.BackAndTitleAction(
        title = "Tytuł",
        onNavigationIconClick = {},
        onActionIconClick = {},
      ),
      content = {

      },
      bottomBar = {
        Column(
          modifier = Modifier.padding(
            horizontal = 20.dp,
            vertical = 10.dp,
          ),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          LargeButton(buttonData = LargeButtonData.Primary(text = "Button", onClick = {}))
        }
      }
    )
  }
}