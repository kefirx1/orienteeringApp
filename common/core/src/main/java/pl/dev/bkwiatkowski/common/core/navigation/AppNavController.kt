package pl.dev.bkwiatkowski.common.core.navigation

import androidx.navigation.NavHostController
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag

data class AppNavController(
  val navController: NavHostController,
) {

  fun navigate(destination: Destination) {
    Log.i(tag = Tag(this), message = "before navigate: $destination")

    navController.currentBackStack.value.forEachIndexed { index, entry ->
      Log.i(tag = Tag(this), message = "- $index: ${entry.destination.route}")
    }

    navController.navigate(destination.route) {
      popUpTo(destination.route) {
        saveState = false
      }
      launchSingleTop = true
      restoreState = true
    }
    Log.i(tag = Tag(this), message = "after navigate: $destination")

    navController.currentBackStack.value.forEachIndexed { index, entry ->
      Log.i(tag = Tag(this), message = "- $index: ${entry.destination.route}")
    }
  }

  fun popBackStack() {
    Log.i(tag = Tag(this), message = "before popBackStack")

    navController.currentBackStack.value.forEachIndexed { index, entry ->
      Log.i(tag = Tag(this), message = "- $index: ${entry.destination.route}")
    }
    navController.popBackStack()
    Log.i(tag = Tag(this), message = "after popBackStack")

    navController.currentBackStack.value.forEachIndexed { index, entry ->
      Log.i(tag = Tag(this), message = "- $index: ${entry.destination.route}")
    }
  }

}