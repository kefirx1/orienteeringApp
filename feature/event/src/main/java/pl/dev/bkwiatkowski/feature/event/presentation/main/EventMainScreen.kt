package pl.dev.bkwiatkowski.feature.event.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.emptyscreen.EmptyScreen
import pl.dev.bkwiatkowski.common.ui.component.permissions.PermissionRequester
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.event.presentation.main.provider.EventMainPreviewProvider

@Composable
fun EventMainScreen(
  viewModel: EventMainVM,
  nestedContent: @Composable () -> Unit,
) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  val lifecycleOwner = LocalLifecycleOwner.current

  LaunchedEffect(Unit) {
    viewModel.lifecycleOwner = lifecycleOwner
  }

  when (val screenData = state) {
    is EventMainVM.ScreenData.Loading -> EmptyScreen()
    is EventMainVM.ScreenData.PermissionDenied -> EventMainPermissionDeniedScreen(
      data = screenData,
    )
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
fun EventMainPermissionDeniedScreen(
  data: EventMainVM.ScreenData.PermissionDenied,
) {
  BaseScaffold(
    topBarData = data.topAppBarData,
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding(),
      ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.2f))
        PermissionRequester(
          data = data.permissionRequesterData,
        )
      }
    },
    bottomBar = {}
  )
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
    topBarData = data.topAppBarData,
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize(),
      ) {
        SecondaryTabRow(
          containerColor = MaterialTheme.colorScheme.background,
          modifier = Modifier.fillMaxWidth(),
          selectedTabIndex = data.currentTab.ordinal,
        ) {
          data.tabs.forEachIndexed { index, tab ->
            Tab(
              selected = data.currentTab.ordinal == index,
              onClick = tab.onClick,
              text = {
                CustomText(
                  text = tab.title,
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                  ),
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
