package pl.dev.bkwiatkowski.feature.event.presentation.game

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
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.event.presentation.game.provider.EventGamePreviewProvider

@Composable
fun EventGameScreen(viewModel: EventGameVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is EventGameVM.ScreenData.Main -> EventGameScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun EventGameScreenContent(
  data: EventGameVM.ScreenData.Main,
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
private fun EventGameScreenPreview(
  @PreviewParameter(provider = EventGamePreviewProvider::class) viewModel: EventGameVM,
) {
  OrienteeringAppTheme {
    EventGameScreen(viewModel = viewModel)
  }
}
