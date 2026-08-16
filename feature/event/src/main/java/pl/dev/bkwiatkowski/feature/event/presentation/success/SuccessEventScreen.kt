package pl.dev.bkwiatkowski.feature.event.presentation.success

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
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.event.presentation.success.provider.SuccessEventPreviewProvider

@Composable
fun SuccessEventScreen(viewModel: SuccessEventVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is SuccessEventVM.ScreenData.Main -> SuccessEventScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun SuccessEventScreenContent(
  data: SuccessEventVM.ScreenData.Main,
) {
  BaseScaffold(
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding()
          .verticalScroll(rememberScrollState()),
      ) {
        // TODO: add UI for success screen
      }
    },
    bottomBar = {}
  )
}

@Preview
@Composable
private fun SuccessEventScreenPreview(
  @PreviewParameter(provider = SuccessEventPreviewProvider::class) viewModel: SuccessEventVM,
) {
  OrienteeringAppTheme {
    SuccessEventScreen(viewModel = viewModel)
  }
}
