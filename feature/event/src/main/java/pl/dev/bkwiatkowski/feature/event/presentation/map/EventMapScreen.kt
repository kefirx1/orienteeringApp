package pl.dev.bkwiatkowski.feature.event.presentation.map

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.R
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButton
import pl.dev.bkwiatkowski.common.ui.component.card.BaseCard
import pl.dev.bkwiatkowski.common.ui.component.emptyscreen.EmptyScreen
import pl.dev.bkwiatkowski.common.ui.component.icon.CustomImage
import pl.dev.bkwiatkowski.common.ui.component.icon.ImageSize
import pl.dev.bkwiatkowski.common.ui.component.icon.ZoomImage
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.event.presentation.map.provider.EventMapPreviewProvider

private const val DEFAULT_MAP_HEIGHT_FRACTION = 0.4f
private const val ZOOMED_MAP_HEIGHT_FRACTION = 0.8f

@Composable
fun EventMapScreen(
  viewModel: EventMapVM,
) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is EventMapVM.ScreenData.Loading -> EmptyScreen()
    is EventMapVM.ScreenData.Main -> EventMapScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun EventMapScreenContent(
  data: EventMapVM.ScreenData.Main,
) {
  BaseScaffold(
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        var heightFraction by remember { mutableFloatStateOf(DEFAULT_MAP_HEIGHT_FRACTION) }
        val animatedFraction by animateFloatAsState(
          targetValue = heightFraction,
          animationSpec = tween(durationMillis = 250),
        )

        Column(
          modifier = Modifier.fillMaxHeight(animatedFraction)
        ) {
          data.mapData?.let { mapData ->
            ZoomImage(
              zoomImageData = mapData,
              onZoomChange = { scale ->
                val isZoomed = scale > 1.01f
                heightFraction = if (isZoomed) { ZOOMED_MAP_HEIGHT_FRACTION } else { DEFAULT_MAP_HEIGHT_FRACTION }
              },
            )
          }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Column(
          modifier = Modifier.verticalScroll(state = rememberScrollState()),
        ) {
          data.wrongWaypointInfo?.let { info ->
            BaseCard {
              Column(
                modifier = Modifier
                  .padding(all = 12.dp),
              ) {
                CustomImage(
                  iconRes = R.drawable.outline_warning_24,
                  imageSize = ImageSize.LARGE,
                  color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(12.dp))

                CustomText(
                  text = info,
                  style = MaterialTheme.typography.titleLarge,
                )
              }
            }
            Spacer(modifier = Modifier.height(32.dp))
          }

          CustomText(
            text = data.nextWaypointLabel,
            style = MaterialTheme.typography.headlineLarge,
          )
        }
      }
    },
    bottomBar = {
      data.checkWaypointButton?.let { buttonData ->
        Column(
          modifier = Modifier.padding(
            horizontal = 20.dp,
            vertical = 10.dp
          ),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          LargeButton(
            buttonData = buttonData,
          )
        }
      }
    }
  )
}

@Preview
@Composable
private fun EventMapScreenPreview(
  @PreviewParameter(provider = EventMapPreviewProvider::class) viewModel: EventMapVM,
) {
  OrienteeringAppTheme {
    EventMapScreen(viewModel = viewModel)
  }
}
