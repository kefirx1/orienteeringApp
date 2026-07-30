package pl.dev.bkwiatkowski.feature.event.presentation.map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.event.presentation.map.provider.EventMapPreviewProvider

@Composable
fun EventMapScreen(
  viewModel: EventMapVM,
) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is EventMapVM.ScreenData.Main -> EventMapScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun EventMapScreenContent(
  data: EventMapVM.ScreenData.Main,
) {
  BaseScaffold(
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding()
          .verticalScroll(rememberScrollState()),
      ) {
        CustomText(text = data.title)
      }
    },
    bottomBar = {}
  )
}

@Preview
@Composable
private fun EventMapScreenPreview(
  @PreviewParameter(provider = EventMapPreviewProvider::class) viewModel: EventMapVM,
) {
  OrienteeringAppTheme {
    EventMapScreen(viewModel = viewModel)
  }
}
