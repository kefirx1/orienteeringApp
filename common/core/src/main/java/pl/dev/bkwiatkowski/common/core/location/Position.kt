package pl.dev.bkwiatkowski.common.core.location

data class Position(
  val latitude: Double,
  val longitude: Double,
) {
  companion object {
    val CENTRAL_POLAND = Position(latitude = 52.069335, longitude = 19.480218)
  }
}
