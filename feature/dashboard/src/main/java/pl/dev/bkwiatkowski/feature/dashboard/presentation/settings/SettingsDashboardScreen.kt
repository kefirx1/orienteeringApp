package pl.dev.bkwiatkowski.feature.dashboard.presentation.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.card.ActionCard
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.dashboard.presentation.settings.provider.SettingsDashboardPreviewProvider

@Composable
fun SettingsDashboardScreen(viewModel: SettingsDashboardVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is SettingsDashboardVM.ScreenData.Main -> SettingsDashboardScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun SettingsDashboardScreenContent(
  data: SettingsDashboardVM.ScreenData.Main,
) {
  BaseScaffold(
    topBarData = data.topBarData,
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding()
          .padding(top = 24.dp)
          .verticalScroll(rememberScrollState()),
      ) {

        ActionCard(data = data.logoutCard)
      }
    },
    bottomBar = {}
  )
}

@Preview
@Composable
private fun SettingsDashboardScreenPreview(
  @PreviewParameter(provider = SettingsDashboardPreviewProvider::class) viewModel: SettingsDashboardVM,
) {
  OrienteeringAppTheme {
    SettingsDashboardScreen(viewModel = viewModel)
  }
}

