package pl.dev.bkwiatkowski.feature.event.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.emptyscreen.EmptyScreen
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.event.presentation.main.provider.EventMainPreviewProvider

@Composable
fun EventMainScreen(viewModel: EventMainVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is EventMainVM.ScreenData.Loading -> EmptyScreen()
    is EventMainVM.ScreenData.Main -> EventMainScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun EventMainScreenContent(
  data: EventMainVM.ScreenData.Main,
) {
  BaseScaffold(
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding()
          .verticalScroll(rememberScrollState()),
      ) {
        Text(text = data.title)
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
    EventMainScreen(viewModel = viewModel)
  }
}
