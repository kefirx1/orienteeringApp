package pl.dev.bkwiatkowski.orienteeringapp.config

import pl.dev.bkwiatkowski.common.core.config.EnvironmentConfig
import pl.dev.bkwiatkowski.orienteeringapp.BuildConfig

class EnvironmentConfigImpl : EnvironmentConfig {
  override val baseUrl: String = BuildConfig.API_BASE_URL
}
