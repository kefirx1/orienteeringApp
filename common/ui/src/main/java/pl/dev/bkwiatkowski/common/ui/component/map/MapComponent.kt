package pl.dev.bkwiatkowski.common.ui.component.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import pl.dev.bkwiatkowski.common.core.location.Position
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButton
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.card.BaseCard
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText

data class MarkerData(
  val position: Position,
  val infoCardTitle: String?,
  val infoCardBody: String? = null,
  val buttonLabel: String? = null,
  val onButtonClick: (() -> Unit)? = null
)

data class MapComponentData(
  val initialPosition: Position = Position.CENTRAL_POLAND,
  val markers: List<MarkerData> = emptyList(),
  val initialZoom: Float = 5.5f,
)

@Composable
fun MapComponent(
  data: MapComponentData,
) {
  val mapSetting by remember {
    mutableStateOf(
      value = MapUiSettings(
        compassEnabled = false,
        mapToolbarEnabled = false,
      ),
    )
  }
  val markersState = data.markers.map { marker ->
    rememberUpdatedMarkerState(position = LatLng(marker.position.latitude, marker.position.longitude))
  }
  var indexOfVisibleMarkerInfo by remember { mutableIntStateOf(value = -1) }
  var isInfoCardVisible by remember { mutableStateOf(value = false) }

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(
      LatLng(data.initialPosition.latitude, data.initialPosition.longitude),
      data.initialZoom,
    )
  }

  Box(modifier = Modifier.fillMaxSize()) {
    GoogleMap(
      modifier = Modifier.fillMaxSize(),
      cameraPositionState = cameraPositionState,
      uiSettings = mapSetting,
      onMapClick = {
        isInfoCardVisible = false
      },
    ) {
      markersState.forEachIndexed { index, markerState ->
        Marker(
          state = markerState,
          onClick = { marker ->
            indexOfVisibleMarkerInfo = index
            isInfoCardVisible = true
            true
          }
        )
      }
    }

    AnimatedVisibility(
      visible = isInfoCardVisible,
      modifier = Modifier
        .fillMaxWidth()
        .align(alignment = Alignment.BottomCenter)
    ) {
      val marker = data.markers.getOrNull(indexOfVisibleMarkerInfo) ?: return@AnimatedVisibility

      BaseCard(
        modifier = Modifier
          .addDefaultPadding()
          .fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.addDefaultPadding()
        ) {
          marker.infoCardTitle?.let { title ->
            CustomText(
              text = title,
              style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
          }
          marker.infoCardBody?.let { body ->
            CustomText(
              text = body,
              style = MaterialTheme.typography.bodyMedium,
            )
          }
          marker.buttonLabel?.let { buttonLabel ->
            Spacer(modifier = Modifier.height(16.dp))
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              LargeButton(
                buttonData = LargeButtonData.Primary(
                  text = buttonLabel,
                  onClick = {
                    marker.onButtonClick?.invoke()
                    isInfoCardVisible = false
                  }
                ),
              )
            }
          }
        }
      }
    }
  }
}