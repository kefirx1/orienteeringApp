package pl.dev.bkwiatkowski.orienteeringapp.presentation.developer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.switch.Switch
import pl.dev.bkwiatkowski.common.ui.component.switch.SwitchData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText

@Composable
fun DeveloperScreen(
  onBack: () -> Unit,
) {
  val viewModel: DeveloperVMImpl = hiltViewModel<DeveloperVMImpl>()

  LaunchedEffect(Unit) {
    viewModel.navAction.collect { action ->
      when (action) {
        is DeveloperVM.Action.Navigation.Back -> onBack()
      }
    }
  }

  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is DeveloperVM.ScreenData.Main -> DeveloperScreenContent(
      data = screenData,
    )
  }

  BackHandler(
    onBack = state.onBackClick,
  )
}

@Composable
fun DeveloperScreenContent(
  data: DeveloperVM.ScreenData.Main,
) {
  BaseScaffold(
    topBarData = TopAppBarData.BackAndTitle(
      title = "Developer",
      onNavigationIconClick = data.onBackClick,
    ),
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
      ) {
        CustomText(text = "Feature flags")
        Spacer(modifier = Modifier.padding(24.dp))

        data.flags.forEach { flag ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
          ) {
            CustomText(text = flag.name)
            Spacer(modifier = Modifier.width(16.dp))

            Switch(
              data = SwitchData(
                isChecked = flag.isChecked,
                onCheckedChange = { isChecked ->
                  data.onFlagToggleClick(flag.name,  isChecked)
                },
              )
            )
          }
        }
      }
    },
    bottomBar = {}
  )
}
