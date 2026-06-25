package pl.dev.bkwiatkowski.feature.dashboard.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.emptyscreen.EmptyScreen
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.provider.MainDashboardPreviewProvider

@Composable
fun MainDashboardScreen(viewModel: MainDashboardVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is MainDashboardVM.ScreenData.Initial -> EmptyScreen()
    is MainDashboardVM.ScreenData.Main -> MainDashboardScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun MainDashboardScreenContent(
  data: MainDashboardVM.ScreenData.Main,
) {
  BaseScaffold(
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(text = "Main Dashboard Screen")
      }
    },
    bottomBar = {}
  )
}

@Preview
@Composable
private fun MainDashboardScreenPreview(
  @PreviewParameter(provider = MainDashboardPreviewProvider::class) viewModel: MainDashboardVM,
) {
  OrienteeringAppTheme {
    MainDashboardScreen(viewModel = viewModel)
  }
}