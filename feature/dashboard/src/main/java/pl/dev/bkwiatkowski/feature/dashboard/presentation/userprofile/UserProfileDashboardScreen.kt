package pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.R
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.card.BaseCard
import pl.dev.bkwiatkowski.common.ui.component.emptyscreen.EmptyScreen
import pl.dev.bkwiatkowski.common.ui.component.icon.CustomImage
import pl.dev.bkwiatkowski.common.ui.component.icon.ImageSize
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.error.ErrorScreen
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.provider.UserProfileDashboardPreviewProvider

@Composable
fun UserProfileDashboardScreen(viewModel: UserProfileDashboardVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is UserProfileDashboardVM.ScreenData.Empty -> EmptyScreen()
    is UserProfileDashboardVM.ScreenData.Error -> ErrorScreen(data = screenData.errorScreenData)
    is UserProfileDashboardVM.ScreenData.Main -> UserProfileDashboardScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun UserProfileDashboardScreenContent(
  data: UserProfileDashboardVM.ScreenData.Main,
) {
  BaseScaffold(
    topBarData = data.topBarData,
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding(),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          CustomImage(
            imageSize = ImageSize.MEDIUM_X,
            iconRes = R.drawable.outline_person_24,
            color = MaterialTheme.colorScheme.primary,
            contentDescription = "User profile icon",
          )
          Spacer(Modifier.width(12.dp))

          CustomText(
            text = data.userName,
            style = MaterialTheme.typography.headlineMedium,
          )
        }
        Spacer(modifier = Modifier.height(48.dp))

        CustomText(
          text = data.sessionsLabel,
          style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn {
          items(count = data.groupedSessions.size) { index ->
            val entry = data.groupedSessions.entries.elementAt(index)

            CustomText(
              text = entry.key,
              style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))

            entry.value.forEach { sessionData ->
              UserSessionCard(sessionData = sessionData)
              Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
          }
        }
      }
    },
    bottomBar = {},
  )
}

@Composable
private fun UserSessionCard(
  sessionData: UserProfileDashboardVM.ScreenData.Main.UserSessionScreenData,
) {
  BaseCard {
    Column(
      modifier = Modifier
        .padding(
          vertical = 8.dp,
          horizontal = 12.dp,
        )
    ) {
      CustomText(
        text = sessionData.eventName,
        style = MaterialTheme.typography.titleMedium,
      )
      Spacer(modifier = Modifier.height(4.dp))

      CustomText(
        text = sessionData.mapName,
        style = MaterialTheme.typography.bodyMedium,
      )
      Spacer(modifier = Modifier.height(4.dp))

      CustomText(
        text = sessionData.startDate,
        style = MaterialTheme.typography.bodyMedium,
      )
      Spacer(modifier = Modifier.height(4.dp))

      CustomText(
        text = sessionData.finishDate,
        style = MaterialTheme.typography.bodyMedium,
      )
      Spacer(modifier = Modifier.height(4.dp))

      CustomText(
        text = sessionData.visitedWaypoints,
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}

@Preview
@Composable
private fun UserProfileDashboardScreenPreview(
  @PreviewParameter(provider = UserProfileDashboardPreviewProvider::class) viewModel: UserProfileDashboardVM,
) {
  OrienteeringAppTheme {
    UserProfileDashboardScreen(viewModel = viewModel)
  }
}
