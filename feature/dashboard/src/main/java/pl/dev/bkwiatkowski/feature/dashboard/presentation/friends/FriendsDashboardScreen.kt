package pl.dev.bkwiatkowski.feature.dashboard.presentation.friends

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
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
import pl.dev.bkwiatkowski.common.ui.component.addDefaultPadding
import pl.dev.bkwiatkowski.common.ui.component.basescaffold.BaseScaffold
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButton
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButton
import pl.dev.bkwiatkowski.common.ui.component.card.BaseCard
import pl.dev.bkwiatkowski.common.ui.component.divider.Divider
import pl.dev.bkwiatkowski.common.ui.component.emptyscreen.EmptyScreen
import pl.dev.bkwiatkowski.common.ui.component.input.TextField
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.error.ErrorScreen
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import pl.dev.bkwiatkowski.feature.dashboard.presentation.friends.provider.FriendsDashboardPreviewProvider

@Composable
fun FriendsDashboardScreen(viewModel: FriendsDashboardVM) {
  val state by viewModel.screenData.collectAsStateWithLifecycle()

  when (val screenData = state) {
    is FriendsDashboardVM.ScreenData.Empty -> EmptyScreen()
    is FriendsDashboardVM.ScreenData.ErrorScreen -> ErrorScreen(data = screenData.errorData)
    is FriendsDashboardVM.ScreenData.Main -> FriendsDashboardScreenContent(data = screenData)
  }

  BackHandler {
    state.onBackClick()
  }
}

@Composable
fun FriendsDashboardScreenContent(
  data: FriendsDashboardVM.ScreenData.Main,
) {
  BaseScaffold(
    topBarData = data.topBarData,
    content = {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .addDefaultPadding(),
      ) {
        item {
          Spacer(modifier = Modifier.height(16.dp))

          TextField(textFieldData = data.searchFieldData)
          Spacer(modifier = Modifier.height(16.dp))

          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            LargeButton(
              buttonData = data.searchButtonData,
            )
          }

          Divider(spacer = 32.dp)
        }

        if (data.friendsList.isEmpty()) {
          item {
            BaseCard {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(
                    vertical = 12.dp,
                  ),
                horizontalAlignment = Alignment.CenterHorizontally,
              ) {
                CustomText(
                  text = data.emptyLabel,
                  style = MaterialTheme.typography.bodyMedium,
                )
              }
            }
          }
        } else {
          items(count = data.friendsList.size) { index ->
            val friend = data.friendsList[index]

            BaseCard {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(
                    vertical = 8.dp,
                    horizontal = 12.dp,
                  ),
              ) {
                friend.acceptationLabel?.let { label ->
                  CustomText(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                }
                Row(
                  modifier = Modifier
                    .fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  CustomText(
                    text = friend.username,
                    style = MaterialTheme.typography.bodyMedium,
                  )

                  Row {
                    SmallButton(buttonData = friend.removeFriendButtonData)

                    friend.acceptFriendRequestButtonData?.let { buttonData ->
                      Spacer(modifier = Modifier.width(8.dp))

                      SmallButton(buttonData = buttonData)
                    }
                  }
                }
              }
            }
            Spacer(modifier = Modifier.height(12.dp))
          }
        }
      }
    },
  )
}

@Preview
@Composable
private fun FriendsDashboardScreenPreview(
  @PreviewParameter(provider = FriendsDashboardPreviewProvider::class) viewModel: FriendsDashboardVM,
) {
  OrienteeringAppTheme {
    FriendsDashboardScreen(viewModel = viewModel)
  }
}