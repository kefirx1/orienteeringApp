package pl.dev.bkwiatkowski.common.core.config

interface EnvironmentConfig {
  val baseUrl: String
  val flavor: Flavor
}

enum class Flavor {
  DEVELOP,
  PROD,
}
