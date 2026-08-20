package pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButton
import pl.dev.bkwiatkowski.common.ui.component.divider.Divider
import pl.dev.bkwiatkowski.common.ui.component.emptyscreen.EmptyScreen
import pl.dev.bkwiatkowski.common.ui.error.ErrorScreen
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails.provider.EventDetailsPreviewProvider

@Composable
fun EventDetailsScreen(viewModel: EventDetailsVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is EventDetailsVM.ScreenData.Loading -> EmptyScreen()
    is EventDetailsVM.ScreenData.ErrorScreen -> ErrorScreen(data = screenData.errorData)
    is EventDetailsVM.ScreenData.MainNoSession -> EventDetailsNoSessionScreenContent(data = screenData)
    is EventDetailsVM.ScreenData.MainWithSession -> EventDetailsWithSessionScreenContent(data = screenData)
    is EventDetailsVM.ScreenData.MainFinished -> EventDetailsFinishedScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun EventDetailsNoSessionScreenContent(
  data: EventDetailsVM.ScreenData.MainNoSession,
) {
  BaseScaffold(
    topBarData = data.topAppBarData,
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding()
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
      ) {
        data.map?.let { bmp ->
          Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = data.event.map.name,
            modifier = Modifier
              .fillMaxWidth()
              .fillMaxHeight(0.3f)
          )
          Spacer(modifier = Modifier.height(12.dp))
        }

        CustomText(
          text = data.event.name,
          style =MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomText(
          text = data.event.description,
          style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(12.dp))

        CustomText(
          text = data.startDateTime,
          style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(24.dp))
      }
    },
  )
}

@Composable
fun EventDetailsWithSessionScreenContent(
  data: EventDetailsVM.ScreenData.MainWithSession,
) {
  BaseScaffold(
    topBarData = data.topAppBarData,
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding()
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
      ) {
        data.map?.let { bmp ->
          Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = data.event.map.name,
            modifier = Modifier
              .fillMaxWidth()
              .fillMaxHeight(0.3f)
          )
          Spacer(modifier = Modifier.height(12.dp))
        }

        CustomText(
          text = data.event.name,
          style =MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomText(
          text = data.event.description,
          style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(12.dp))

        CustomText(
          text = data.startDateTime,
          style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(24.dp))
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier.padding(
          horizontal = 20.dp,
          vertical = 10.dp,
        ),
      ) {
        data.playButtonData?.let { button ->
          LargeButton(buttonData = button)
        }
      }
    },
    snackbarHostState = data.snackbarHostState,
  )
}

@Composable
fun EventDetailsFinishedScreenContent(
  data: EventDetailsVM.ScreenData.MainFinished,
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
        data.map?.let { bmp ->
          Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = data.event.map.name,
            modifier = Modifier
              .fillMaxWidth()
              .fillMaxHeight(0.3f)
          )
          Spacer(modifier = Modifier.height(12.dp))
        }

        CustomText(
          text = data.event.name,
          style =MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomText(
          text = data.event.description,
          style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(12.dp))

        CustomText(
          text = data.startDateTime,
          style = MaterialTheme.typography.bodySmall,
        )

        Divider(spacer = 24.dp)

        CustomText(
          text = data.userSessionSectionLabel,
          style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
          items(count = data.userSessionSection.size) { index ->
            val session = data.userSessionSection[index]
            CustomText(
              text = session.joinTime,
              style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))

            CustomText(
              text = session.finishTime,
              style = MaterialTheme.typography.bodyMedium,
            )

            if (index < data.userSessionSection.size - 1) {
              Divider(spacer = 8.dp)
            }
          }
        }
      }
    },
  )
}

@Preview
@Composable
private fun EventDetailsScreenPreview(
  @PreviewParameter(provider = EventDetailsPreviewProvider::class) viewModel: EventDetailsVM
) {
  OrienteeringAppTheme {
    EventDetailsScreen(viewModel = viewModel)
  }
}
