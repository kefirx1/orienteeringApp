package pl.dev.bkwiatkowski.common.ui.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButton
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

data class DialogData(
  val title: String,
  val content: String,
  val onDismiss: () -> Unit,
  val onPrimaryButtonData: SmallButtonData.Tertiary,
  val onSecondaryButtonData: SmallButtonData.Tertiary? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dialog(
  modifier: Modifier = Modifier,
  dialogData: DialogData,
) {
  BasicAlertDialog(
    modifier = modifier,
    onDismissRequest = dialogData.onDismiss,
    content = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(size = 20.dp),
          )
          .padding(
            horizontal = 20.dp,
            vertical = 15.dp,
          ),
      ) {
        CustomText(
          text = dialogData.title,
          style = MaterialTheme.typography.titleLarge.copy(
            fontSize = 24.sp,
          ),
        )
        Spacer(modifier = Modifier.height(20.dp))

        CustomText(
          text = dialogData.content,
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 14.sp,
          ),
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          dialogData.onSecondaryButtonData?.let { secondaryButton ->
            SmallButton(buttonData = secondaryButton)
            Spacer(Modifier.width(10.dp))
          }

          SmallButton(
            buttonData = dialogData.onPrimaryButtonData,
          )
        }
      }
    }
  )
}

private class DialogPreviewProvider : PreviewParameterProvider<DialogData> {
  override val values: Sequence<DialogData> = sequenceOf(
    DialogData(
      title = "Dialog title",
      content = "Dialog content loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum loren ipsum",
      onDismiss = {},
      onPrimaryButtonData = SmallButtonData.Tertiary(
        text = "Primary",
        onClick = {},
      ),
      onSecondaryButtonData = SmallButtonData.Tertiary(
        text = "Secondary",
        onClick = {},
      ),
    )
  )
}

@Preview(name = "Dialog preview")
@Composable
private fun DialogPreview(
  @PreviewParameter(DialogPreviewProvider::class) dialogData: DialogData,
) {
  OrienteeringAppTheme {
    Dialog(dialogData = dialogData)
  }
}