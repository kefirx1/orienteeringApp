package pl.dev.bkwiatkowski.common.core.navigation

import androidx.navigation.NavHostController
import pl.dev.bkwiatkowski.common.core.logger.Log

data class AppNavController(
  val navController: NavHostController,
) {

  fun navigate(destination: Destination) {
    Log.i(tag = "AppNavController", message = "before navigate: $destination")

    navController.currentBackStack.value.forEachIndexed { index, entry ->
      Log.i(tag = "AppNavController", message = "- $index: ${entry.destination.route}")
    }

    navController.navigate(destination.route) {
      popUpTo(destination.route) {
        saveState = false
      }
      launchSingleTop = true
      restoreState = true
    }
    Log.i(tag = "AppNavController", message = "after navigate: $destination")

    navController.currentBackStack.value.forEachIndexed { index, entry ->
      Log.i(tag = "AppNavController", message = "- $index: ${entry.destination.route}")
    }
  }

  fun popBackStack() {
    Log.i(tag = "AppNavController", message = "before popBackStack")

    navController.currentBackStack.value.forEachIndexed { index, entry ->
      Log.i(tag = "AppNavController", message = "- $index: ${entry.destination.route}")
    }
    navController.popBackStack()
    Log.i(tag = "AppNavController", message = "after popBackStack")

    navController.currentBackStack.value.forEachIndexed { index, entry ->
      Log.i(tag = "AppNavController", message = "- $index: ${entry.destination.route}")
    }
  }

}