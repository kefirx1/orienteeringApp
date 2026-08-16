package pl.dev.bkwiatkowski.feature.event.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import pl.dev.bkwiatkowski.common.ui.component.card.BaseCard
import pl.dev.bkwiatkowski.common.ui.component.divider.Divider
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.event.presentation.game.provider.EventGamePreviewProvider

@Composable
fun EventGameScreen(viewModel: EventGameVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is EventGameVM.ScreenData.Main -> EventGameScreenContent(data = screenData)
    is EventGameVM.ScreenData.Empty -> EventGameScreenEmpty(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun EventGameScreenEmpty(
  data: EventGameVM.ScreenData.Empty,
) {
  BaseScaffold(
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding()
          .verticalScroll(rememberScrollState()),
      ) {
        Spacer(modifier = Modifier.height(64.dp))

        BaseCard {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(
                vertical = 12.dp,
              ),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            CustomText(
              text = data.emptyLabel,
              style = MaterialTheme.typography.bodyMedium,
            )
          }
        }
      }
    },
    bottomBar = {}
  )
}

@Composable
fun EventGameScreenContent(
  data: EventGameVM.ScreenData.Main,
) {
  BaseScaffold(
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding(),
      ) {
        Spacer(modifier = Modifier.height(64.dp))

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
              )

              if (index != data.waypoints.lastIndex) {
                Divider(spacer = 8.dp)
              }
            }
          }
        }
      }
    },
    bottomBar = {}
  )
}

@Preview
@Composable
private fun EventGameScreenPreview(
  @PreviewParameter(provider = EventGamePreviewProvider::class) viewModel: EventGameVM,
) {
  OrienteeringAppTheme {
    EventGameScreen(viewModel = viewModel)
  }
}
