package pl.dev.bkwiatkowski.common.core.navigation

interface Destination {
  val route: String
    get() = this.toString()
}
