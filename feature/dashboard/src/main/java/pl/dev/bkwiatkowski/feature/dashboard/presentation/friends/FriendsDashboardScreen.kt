package pl.dev.bkwiatkowski.feature.dashboard.presentation.friends

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
import pl.dev.bkwiatkowski.common.ui.component.emptyscreen.EmptyScreen
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.provider.FriendsDashboardPreviewProvider

@Composable
fun FriendsDashboardScreen(viewModel: FriendsDashboardVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is FriendsDashboardVM.ScreenData.Empty -> EmptyScreen()
    is FriendsDashboardVM.ScreenData.Main -> FriendsDashboardScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun FriendsDashboardScreenContent(
  data: FriendsDashboardVM.ScreenData.Main,
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
  )
}

@Preview
@Composable
private fun FriendsDashboardScreenPreview(
  @PreviewParameter(provider = FriendsDashboardPreviewProvider::class) viewModel: FriendsDashboardVM,
) {
  OrienteeringAppTheme {
    FriendsDashboardScreen(viewModel = viewModel)
  }
}