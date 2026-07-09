package pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.map.MapComponent
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap.provider.EventsMapPreviewProvider

@Composable
fun EventsMapScreen(viewModel: EventsMapVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is EventsMapVM.ScreenData.Main -> EventsMapsScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun EventsMapsScreenContent(
  data: EventsMapVM.ScreenData.Main,
) {
  BaseScaffold(
    content = {
      MapComponent(
        data = data.mapComponentData
      )
    },
    bottomBar = {}
  )
}

@Preview
@Composable
private fun EventsMapScreenPreview(
  @PreviewParameter(provider = EventsMapPreviewProvider::class) viewModel: EventsMapVM,
) {
  OrienteeringAppTheme {
    EventsMapScreen(viewModel = viewModel)
  }
}

