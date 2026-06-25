package pl.dev.bkwiatkowski.technical.user.data.mapper

import pl.dev.bkwiatkowski.technical.user.data.model.UserSettingsDto
import pl.dev.bkwiatkowski.technical.user.domain.model.UserSettings

object UserMapper {

  fun UserSettingsDto.toDomain(): UserSettings =
    UserSettings(
      userName = userName,
      salt = salt,
      ivDek = ivDek,
    )

  fun UserSettings.toDto(): UserSettingsDto =
    UserSettingsDto(
      userName = userName,
      salt = salt,
      ivDek = ivDek,
    )
}