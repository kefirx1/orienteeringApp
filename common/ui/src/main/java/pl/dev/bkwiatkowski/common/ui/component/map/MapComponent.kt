package pl.dev.bkwiatkowski.common.ui.component.map

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import pl.dev.bkwiatkowski.common.core.location.Position
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButton
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.card.BaseCard
import pl.dev.bkwiatkowski.common.ui.component.indicator.HorizontalPagerIndicator
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText

class ClusterRenderer(
  context: Context,
  map: GoogleMap,
  clusterManager: ClusterManager<MarkerData>
) : DefaultClusterRenderer<MarkerData>(
  context = context,
  mMap = map,
  mClusterManager = clusterManager,
) {

  override fun shouldRenderAsCluster(cluster: Cluster<MarkerData>): Boolean {
    return cluster.size >= 2
  }
}

data class MarkerData(
  val location: Position,
  val infoCardTitle: String?,
  val infoCardBody: String? = null,
  val buttonLabel: String? = null,
  val onButtonClick: (() -> Unit)? = null
) : ClusterItem {
  override val position: LatLng = LatLng(location.latitude, location.longitude)
  override val title: String? = infoCardTitle
  override val snippet: String? = infoCardBody
  override val zIndex: Float? = null
}

data class MapComponentData(
  val initialPosition: Position = Position.CENTRAL_POLAND,
  val markers: List<MarkerData> = emptyList(),
  val initialZoom: Float = 5.5f,
)

@SuppressLint("PotentialBehaviorOverride")
@OptIn(MapsComposeExperimentalApi::class, ExperimentalFoundationApi::class)
@Composable
fun MapComponent(
  data: MapComponentData,
) {
  val context = LocalContext.current

  val mapSetting by remember {
    mutableStateOf(
      value = MapUiSettings(
        compassEnabled = false,
        mapToolbarEnabled = false,
      ),
    )
  }

  var isMapLoaded by remember { mutableStateOf(value = false) }
  var indexOfVisibleMarkerInfo by remember { mutableIntStateOf(value = -1) }
  var isInfoCardVisible by remember { mutableStateOf(value = false) }
  var clusterItems by remember { mutableStateOf<List<MarkerData>>(emptyList()) }

  val clusterManager = remember { mutableStateOf<ClusterManager<MarkerData>?>(null) }
  val pagerState = rememberPagerState { clusterItems.size }
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(
      LatLng(data.initialPosition.latitude, data.initialPosition.longitude),
      data.initialZoom,
    )
  }

  Box(modifier = Modifier.fillMaxSize()) {
    if (!isMapLoaded) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(color = MaterialTheme.colorScheme.background)
      )
    }

    GoogleMap(
      modifier = Modifier.fillMaxSize(),
      cameraPositionState = cameraPositionState,
      uiSettings = mapSetting,
      onMapClick = {
        isInfoCardVisible = false
      },
      onMapLoaded = {
        isMapLoaded = true
      }
    ) {
      MapEffect(data.markers) { map ->
        val manager = clusterManager.value ?: ClusterManager<MarkerData>(context, map).apply {
          renderer = ClusterRenderer(
            context = context,
            map = map,
            clusterManager = this,
          )

          setOnClusterItemClickListener { marker ->
            clusterItems = emptyList()
            indexOfVisibleMarkerInfo = data.markers.indexOf(marker)
            if (indexOfVisibleMarkerInfo != -1) {
              isInfoCardVisible = true
            }
            true
          }

          setOnClusterClickListener { cluster ->
            val items = cluster.items.toList()
            if (items.isNotEmpty()) {
              clusterItems = items
              indexOfVisibleMarkerInfo = 0
              isInfoCardVisible = true
            }
            true
          }

          clusterManager.value = this
        }

        map.setOnCameraIdleListener(manager)
        map.setOnMarkerClickListener(manager)

        manager.clearItems()
        manager.addItems(data.markers)
        manager.cluster()
      }
    }

    AnimatedVisibility(
      visible = isInfoCardVisible,
      modifier = Modifier
        .fillMaxWidth()
        .align(alignment = Alignment.BottomCenter)
    ) {
      BaseCard(
        modifier = Modifier
          .addDefaultPadding()
          .fillMaxWidth()
      ) {
        if (clusterItems.isNotEmpty()) {
          HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
          ) { page ->
            val item = clusterItems[page]
            MarkerInfoContent(
              item = item,
              onClose = { isInfoCardVisible = false },
              modifier = Modifier.addDefaultPadding(),
            )
          }

          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            HorizontalPagerIndicator(pagerState = pagerState)
          }
        } else {
          val marker = data.markers.getOrNull(indexOfVisibleMarkerInfo) ?: return@BaseCard

          MarkerInfoContent(
            item = marker,
            onClose = { isInfoCardVisible = false },
            modifier = Modifier.addDefaultPadding(),
          )
        }
      }
    }
  }
}

@Composable
private fun MarkerInfoContent(
  item: MarkerData,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    item.infoCardTitle?.let { title ->
      CustomText(
        text = title,
        style = MaterialTheme.typography.titleMedium,
      )
      Spacer(modifier = Modifier.height(4.dp))
    }
    item.infoCardBody?.let { body ->
      CustomText(
        text = body,
        style = MaterialTheme.typography.bodyMedium,
      )
    }
    item.buttonLabel?.let { buttonLabel ->
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
              item.onButtonClick?.invoke()
              onClose()
            }
          ),
        )
      }
    }
  }
}