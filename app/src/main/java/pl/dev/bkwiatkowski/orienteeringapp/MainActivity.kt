package pl.dev.bkwiatkowski.orienteeringapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.dev.bkwiatkowski.common.ui.component.icon.CustomImage
import pl.dev.bkwiatkowski.common.ui.component.icon.ImageSize
import pl.dev.bkwiatkowski.common.ui.R
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.activityconnector.ActivityConnector
import pl.dev.bkwiatkowski.common.core.config.EnvironmentConfig
import pl.dev.bkwiatkowski.common.core.config.Flavor
import pl.dev.bkwiatkowski.common.core.network.NetworkMonitor
import pl.dev.bkwiatkowski.common.loader.Loader
import pl.dev.bkwiatkowski.common.loader.LoaderManager
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.orienteeringapp.presentation.MainAppNavGraph
import pl.dev.bkwiatkowski.orienteeringapp.presentation.developer.DeveloperFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var activityConnector: ActivityConnector

  @Inject
  lateinit var loaderManager: LoaderManager

  @Inject
  lateinit var networkMonitor: NetworkMonitor

  @Inject
  lateinit var environmentConfig: EnvironmentConfig

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    lifecycleScope.launch {
      activityConnector.connect(this@MainActivity)
      networkMonitor.register()
    }
    enableEdgeToEdge()

    setContent {
      OrienteeringAppTheme {
        Box {
          Loader(visibility = loaderManager.visibilityMonitor())

          MainAppNavGraph(
            onAppExit = ::finish,
          )

          AddDebugButton()
        }
      }
    }
  }

  @Composable
  private fun AddDebugButton() {
    if (environmentConfig.flavor == Flavor.DEVELOP) {
      IconButton(
        onClick = {
          val fm = this@MainActivity.supportFragmentManager
          if (fm.findFragmentByTag("developer_fragment") == null) {
            fm.beginTransaction()
              .add(android.R.id.content, DeveloperFragment(), "developer_fragment")
              .addToBackStack("developer_fragment")
              .commit()
          }
        },
        modifier = Modifier.padding(top = 16.dp, start = 8.dp),
      ) {
        CustomImage(
          iconRes = R.drawable.baseline_bug_report_24,
          imageSize = ImageSize.SMALL_X,
          contentDescription = "Debug button",
        )
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    lifecycleScope.launch {
      networkMonitor.unregister()
    }
  }
}
