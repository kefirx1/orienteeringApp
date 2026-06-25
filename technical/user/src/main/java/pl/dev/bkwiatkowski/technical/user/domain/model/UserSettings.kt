package pl.dev.bkwiatkowski.technical.user.domain.model

data class UserSettings(
  val userName: String,
  val salt: String,
  val ivDek: String,
)