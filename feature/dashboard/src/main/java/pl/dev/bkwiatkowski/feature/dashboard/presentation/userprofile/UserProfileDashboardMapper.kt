package pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile

import pl.dev.bkwiatkowski.common.core.time.DateFormatter
import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.UserSessionData

interface UserProfileDashboardMapper : Mapper<UserProfileDashboardMapper.Params, UserProfileDashboardVM.ScreenData> {
  data class Params(
    val state: UserProfileDashboardVM.State,
    val onBackClick: () -> Unit,
  )
}

class UserProfileDashboardMapperImpl(
  private val dateFormatter: DateFormatter,
) : UserProfileDashboardMapper {
  override fun invoke(params: UserProfileDashboardMapper.Params): UserProfileDashboardVM.ScreenData =
    when (params.state) {
      is UserProfileDashboardVM.State.Initial.Content -> UserProfileDashboardVM.ScreenData.Empty(
        onBackClick = params.onBackClick,
      )
      is UserProfileDashboardVM.State.Initial.Error -> UserProfileDashboardVM.ScreenData.Error(
        onBackClick = params.onBackClick,
        errorScreenData = params.state.errorScreenData,
      )
      is UserProfileDashboardVM.State.Initialized -> {
        val grouped: Map<String, List<UserProfileDashboardVM.ScreenData.Main.UserSessionScreenData>> =
          params.state.sessionsData.sessions
            .groupBy { data ->
              dateFormatter.format(
                dateTime = data.startedAt,
                format = DateFormatter.Format.DATE_ONLY,
              )
            }.mapValues { entry ->
              entry.value.map { value ->
                UserProfileDashboardVM.ScreenData.Main.UserSessionScreenData(
                  eventName = value.eventName,
                  mapName = "Mapa: ${value.mapName}",
                  startDate = dateFormatter.format(
                    dateTime = value.startedAt,
                    format = DateFormatter.Format.DATE_TIME,
                  ).let { date ->
                    "Rozpoczęto: $date"
                  },
                  finishDate = dateFormatter.format(
                    dateTime = value.finishedAt,
                    format = DateFormatter.Format.DATE_TIME,
                  ).let { date ->
                    "Zakończono: $date"
                  },
                  visitedWaypoints = "Odwiedzone punkty: ${value.visitedWaypointsCount}",
                )
              }
            }

        UserProfileDashboardVM.ScreenData.Main(
          onBackClick = params.onBackClick,
          topBarData = TopAppBarData.BackAndTitle(
            title = "Profil użytkownika",
            onNavigationIconClick = params.onBackClick,
          ),
          groupedSessions = grouped,
          sessionsLabel = "Rozegrane biegi:",
          userName = params.state.userName,
        )
      }
    }
}
