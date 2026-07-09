package pl.dev.bkwiatkowski.orienteeringapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.activityconnector.ActivityConnector
import pl.dev.bkwiatkowski.common.loader.Loader
import pl.dev.bkwiatkowski.common.loader.LoaderManager
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.orienteeringapp.presentation.MainAppNavGraph
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var activityConnector: ActivityConnector

  @Inject
  lateinit var loaderManager: LoaderManager


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    lifecycleScope.launch {
      activityConnector.connect(this@MainActivity)

    }
    enableEdgeToEdge(

    )

    setContent {
      OrienteeringAppTheme {
        Box {
          Loader(visibility = loaderManager.visibilityMonitor())

          MainAppNavGraph(
            onAppExit = ::finish,
          )
        }
      }
    }
  }
}
