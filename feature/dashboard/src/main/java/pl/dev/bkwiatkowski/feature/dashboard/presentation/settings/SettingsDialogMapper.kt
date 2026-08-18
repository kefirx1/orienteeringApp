package pl.dev.bkwiatkowski.feature.dashboard.presentation.settings

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.dialog.DialogData

interface SettingsDialogMapper : Mapper<SettingsDialogMapper.Params, DialogData> {
  data class Params(
    val type: DialogType,
    val onLogoutClick: () -> Unit,
  )

  sealed interface DialogType {
    data object Logout : DialogType
  }
}

class SettingsDialogMapperImpl : SettingsDialogMapper {
  override fun invoke(params: SettingsDialogMapper.Params): DialogData =
    when (params.type) {
      SettingsDialogMapper.DialogType.Logout -> DialogData(
        title = "Chcesz się wylogować?",
        content = "Spowoduje to wylogowanie z aplikacji i utratę danych zapisanych na urządzeniu w aplikacji.",
        onDismiss = {},
        onPrimaryButtonData = SmallButtonData.Tertiary(
          text = "Wyloguj",
          onClick = params.onLogoutClick,
        ),
        onSecondaryButtonData = SmallButtonData.Tertiary(
          text = "Anuluj",
          onClick = {},
        ),
      )
    }

}