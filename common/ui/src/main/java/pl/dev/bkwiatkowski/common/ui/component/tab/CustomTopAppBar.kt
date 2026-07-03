package pl.dev.bkwiatkowski.common.ui.component.tab

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import pl.dev.bkwiatkowski.common.ui.R
import pl.dev.bkwiatkowski.common.ui.component.icon.CustomImage
import pl.dev.bkwiatkowski.common.ui.component.icon.ImageSize
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme

sealed class TopAppBarData(
  open val title: String?,
  val navigationIconId: Int? = null,
  open val onNavigationIconClick: () -> Unit = {},
  open val actionIconId: Int? = null,
  open val onActionIconClick: () -> Unit = {},
) {
  data class BackAndTitle(
    override val title: String,
    override val onNavigationIconClick: () -> Unit,
  ): TopAppBarData(
    title = title,
    navigationIconId = R.drawable.baseline_arrow_back_24,
    onNavigationIconClick = onNavigationIconClick,
  )

  data class BackAndTitleAction(
    override val title: String,
    override val onNavigationIconClick: () -> Unit,
    override val onActionIconClick: () -> Unit,
  ): TopAppBarData(
    title = title,
    navigationIconId = R.drawable.baseline_arrow_back_24,
    onNavigationIconClick = onNavigationIconClick,
    onActionIconClick = onActionIconClick,
    actionIconId = R.drawable.baseline_close_24,
  )

  data class BackAndAction(
    override val onNavigationIconClick: () -> Unit,
    override val onActionIconClick: () -> Unit,
  ): TopAppBarData(
    title = null,
    navigationIconId = R.drawable.baseline_arrow_back_24,
    onNavigationIconClick = onNavigationIconClick,
    onActionIconClick = onActionIconClick,
    actionIconId = R.drawable.baseline_close_24,
  )

  data class Back(
    override val onNavigationIconClick: () -> Unit,
  ): TopAppBarData(
    title = null,
    navigationIconId = R.drawable.baseline_arrow_back_24,
    onNavigationIconClick = onNavigationIconClick,
  )

  data class Title(
    override val title: String,
  ): TopAppBarData(
    title = title,
  )

  data class Action(
    override val onActionIconClick: () -> Unit,
    override val actionIconId: Int?,
  ): TopAppBarData(
    title = null,
    onActionIconClick = onActionIconClick,
    actionIconId = actionIconId,
  )
}

private const val DEBOUNCE_DELAY = 500L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
  topAppBarData: TopAppBarData,
) {
  var lastClick by remember { mutableLongStateOf(0L) }

  TopAppBar(
    navigationIcon = {
      if (topAppBarData.navigationIconId != null) {
        IconButton(
          onClick = {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClick > DEBOUNCE_DELAY) {
              lastClick = currentTime
              topAppBarData.onNavigationIconClick()
            }
          },
        ) {
          CustomImage(
            iconRes = topAppBarData.navigationIconId,
            contentDescription = "TAB navigation icon",
            color = MaterialTheme.colorScheme.onBackground,
          )
        }
      }
    },
    title = {
      topAppBarData.title?.let { title ->
        Spacer(modifier = Modifier.width(40.dp))

        CustomText(
          text = title,
          style = MaterialTheme.typography.headlineMedium,
        )
      }
    },
    actions = {
      if (topAppBarData.actionIconId != null) {
        IconButton(
          onClick = {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClick > DEBOUNCE_DELAY) {
              lastClick = currentTime
              topAppBarData.onActionIconClick()
            }
          },
        ) {
          CustomImage(
            iconRes = topAppBarData.actionIconId!!,
            contentDescription = "TAB action icon",
            color = MaterialTheme.colorScheme.onBackground,
          )
        }
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.background,
      titleContentColor = MaterialTheme.colorScheme.onBackground,
      actionIconContentColor = MaterialTheme.colorScheme.onBackground,
      navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
      scrolledContainerColor = MaterialTheme.colorScheme.background,
    ),
  )
}

private class CustomTopAppBarProvider : PreviewParameterProvider<TopAppBarData> {
  override val values: Sequence<TopAppBarData> = sequenceOf(
    TopAppBarData.BackAndTitle(
      title = "Title",
      onNavigationIconClick = {},
    ),
    TopAppBarData.Back(
      onNavigationIconClick = {},
    ),
    TopAppBarData.Action(
      onActionIconClick = {},
      actionIconId = R.drawable.baseline_circle_notifications_24,
    ),
    TopAppBarData.BackAndAction(
      onNavigationIconClick = {},
      onActionIconClick = {},
    ),
  )
}

@Preview(name = "CustomTopAppBar preview")
@Composable
private fun CustomTopAppBarPreview(
  @PreviewParameter(CustomTopAppBarProvider::class) topAppBarData: TopAppBarData,
) {
  OrienteeringAppTheme(darkTheme = true) {
    CustomTopAppBar(topAppBarData = topAppBarData)
  }
}