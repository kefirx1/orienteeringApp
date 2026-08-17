package pl.dev.bkwiatkowski.feature.event.presentation.success

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButton
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.card.BaseCard
import pl.dev.bkwiatkowski.common.ui.component.divider.Divider
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
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
    topBarData = data.topAppBarData,
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding(),
        horizontalAlignment = Alignment.Start,
      ) {
        Spacer(modifier = Modifier.height(32.dp))

        CustomText(
          text = data.description,
          style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(24.dp))

        BaseCard {
          Column(
            modifier = Modifier.padding(
              vertical = 8.dp,
              horizontal = 12.dp,
            )
          ) {
            CustomText(
              text = "Rozpoczęcie: ${data.startDateTime}",
              style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomText(
              text = "Zakończenie: ${data.finishDateTime}",
              style = MaterialTheme.typography.bodyLarge,
            )
          }
        }

        Divider(spacer = 24.dp)

        BaseCard {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .padding(
                vertical = 8.dp,
                horizontal = 12.dp,
              ),
          ) {
            itemsIndexed(items = data.waypoints) { index, waypoint ->
              CustomText(
                text = waypoint.label,
                style = MaterialTheme.typography.titleMedium,
              )
              Spacer(modifier = Modifier.height(4.dp))

              CustomText(
                text = waypoint.visitedTime,
                style = MaterialTheme.typography.bodySmall,
              )

              if (index != data.waypoints.lastIndex) {
                Divider(spacer = 8.dp)
              }
            }
          }
        }
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier.padding(
          horizontal = 20.dp,
          vertical = 10.dp,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        LargeButton(buttonData = data.closButtonData)
      }
    }
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
