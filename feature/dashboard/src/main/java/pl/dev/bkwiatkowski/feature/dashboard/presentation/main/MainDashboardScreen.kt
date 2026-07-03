package pl.dev.bkwiatkowski.feature.dashboard.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButton
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.card.ActionCard
import pl.dev.bkwiatkowski.common.ui.component.card.BaseCard
import pl.dev.bkwiatkowski.common.ui.component.emptyscreen.EmptyScreen
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendsStatsData
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.provider.MainDashboardPreviewProvider

@Composable
fun MainDashboardScreen(viewModel: MainDashboardVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is MainDashboardVM.ScreenData.Initial -> EmptyScreen()
    is MainDashboardVM.ScreenData.Main -> MainDashboardScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun MainDashboardScreenContent(
  data: MainDashboardVM.ScreenData.Main,
) {
  BaseScaffold(
    topBarData = data.topBarData,
    content = {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Spacer(modifier = Modifier.height(16.dp))

        WelcomeCard(
          welcomeLabel = data.welcomeLabel,
          welcomeDescription = data.welcomeDescription,
          checkNewRunsButtonData = data.checkNewRunsButton,
        )
        Spacer(modifier = Modifier.height(16.dp))

        FriendsCard(
          friendsCardTitle = data.friendsCardTitle,
          friendsCardEmptyState = data.friendsCardEmptyState,
          friendsStatsData = data.friendsData,
          addNewFriendsButtonData = data.addNewFriendsButton,
        )
        Spacer(modifier = Modifier.height(32.dp))

        ActionCard(data = data.myProfileCard)
        Spacer(modifier = Modifier.height(16.dp))

        ActionCard(data = data.settingsCard)
      }
    },
    fabData = data.newRunFab,
    bottomBar = {}
  )
}

@Composable
private fun WelcomeCard(
  welcomeLabel: String,
  welcomeDescription: String,
  checkNewRunsButtonData: SmallButtonData,
) {
  BaseCard {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
    ) {
      CustomText(
        text = welcomeLabel,
        style = MaterialTheme.typography.headlineMedium,
      )
      Spacer(modifier = Modifier.height(8.dp))

      CustomText(
        text = welcomeDescription,
        style = MaterialTheme.typography.bodyMedium,
      )
      Spacer(modifier = Modifier.height(16.dp))

      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        SmallButton(buttonData = checkNewRunsButtonData)
      }
    }
  }
}

@Composable
private fun FriendsCard(
  addNewFriendsButtonData: SmallButtonData,
  friendsCardTitle: String,
  friendsCardEmptyState: String,
  friendsStatsData: FriendsStatsData,
) {
  BaseCard {
    if (friendsStatsData.friends.isEmpty()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        CustomText(
          text = friendsCardEmptyState,
          style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))

        SmallButton(buttonData = addNewFriendsButtonData)
      }
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
      ) {
        CustomText(
          text = friendsCardTitle,
          style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(8.dp))

        friendsStatsData.friends.forEachIndexed { index, friend ->
          CustomText(
            text = friend.name,
            style = MaterialTheme.typography.bodyMedium,
          )
          if (index < friendsStatsData.friends.size - 1) {
            Spacer(modifier = Modifier.height(4.dp))
          }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          SmallButton(buttonData = addNewFriendsButtonData)
        }
      }
    }
  }
}

@Preview
@Composable
private fun MainDashboardScreenPreview(
  @PreviewParameter(provider = MainDashboardPreviewProvider::class) viewModel: MainDashboardVM,
) {
  OrienteeringAppTheme {
    MainDashboardScreen(viewModel = viewModel)
  }
}