package pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile

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
import pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.provider.UserProfileDashboardPreviewProvider

@Composable
fun UserProfileDashboardScreen(viewModel: UserProfileDashboardVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is UserProfileDashboardVM.ScreenData.Main -> UserProfileDashboardScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun UserProfileDashboardScreenContent(
  data: UserProfileDashboardVM.ScreenData.Main,
) {
  BaseScaffold(
    topBarData = data.topBarData,
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding()
          .verticalScroll(rememberScrollState()),
      ) {

      }
    },
    bottomBar = {}
  )
}

@Preview
@Composable
private fun UserProfileDashboardScreenPreview(
  @PreviewParameter(provider = UserProfileDashboardPreviewProvider::class) viewModel: UserProfileDashboardVM,
) {
  OrienteeringAppTheme {
    UserProfileDashboardScreen(viewModel = viewModel)
  }
}
