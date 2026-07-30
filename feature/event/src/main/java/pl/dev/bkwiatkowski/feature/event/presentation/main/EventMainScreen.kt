package pl.dev.bkwiatkowski.feature.event.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.emptyscreen.EmptyScreen
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.event.presentation.main.provider.EventMainPreviewProvider

@Composable
fun EventMainScreen(
  viewModel: EventMainVM,
  nestedContent: @Composable () -> Unit,
) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is EventMainVM.ScreenData.Loading -> EmptyScreen()
    is EventMainVM.ScreenData.Main -> EventMainScreenContent(
      data = screenData,
      nestedContent = nestedContent,
    )
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun EventMainScreenContent(
  data: EventMainVM.ScreenData.Main,
  nestedContent: @Composable () -> Unit,
) {

  LaunchedEffect(Unit) {
    if (data.currentTab != EventMainVM.StateData.CurrentTab.MAP) {
      data.onOpenMapClick()
    }
  }

  BaseScaffold(
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize(),
      ) {
        SecondaryTabRow(
          modifier = Modifier.fillMaxWidth(),
          selectedTabIndex = data.currentTab.ordinal,
        ) {
          data.tabs.forEachIndexed { index, tab ->
            Tab(
              modifier = Modifier
                .padding(top = 16.dp),
              selected = data.currentTab.ordinal == index,
              onClick = tab.onClick,
              text = {
                CustomText(
                  text = tab.title,
                  style = MaterialTheme.typography.titleLarge,
                )
              },
            )
          }
        }
        nestedContent()
      }
    },
    bottomBar = {}
  )
}

@Preview
@Composable
private fun EventMainScreenPreview(
  @PreviewParameter(provider = EventMainPreviewProvider::class) viewModel: EventMainVM,
) {
  OrienteeringAppTheme {
    EventMainScreen(
      viewModel = viewModel,
      nestedContent = {},
    )
  }
}
