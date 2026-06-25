package pl.dev.bkwiatkowski.technical.user.data.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UserSettingsDto(
  @SerialName(value = "userName") val userName: String,
  @SerialName(value = "salt") val salt: String,
  @SerialName(value = "ivDek") val ivDek: String,
)