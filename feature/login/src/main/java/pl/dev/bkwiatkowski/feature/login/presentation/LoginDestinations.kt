package pl.dev.bkwiatkowski.feature.login.presentation

import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.common.core.navigation.Destination

sealed interface LoginDestinations : Destination {

  @Serializable
  data object LoginGraph : Destination

  @Serializable
  data object Login : LoginDestinations

  @Serializable
  data object Onboarding : LoginDestinations

  @Serializable
  data object SetPassword : LoginDestinations
}

sealed interface LoginResult {
  data object LoginSuccess : LoginResult
  data object ExitApp : LoginResult
}