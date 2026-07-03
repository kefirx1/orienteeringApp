package pl.dev.bkwiatkowski.feature.dashboard.domain.model

//TODO
data class FriendsStatsData(
  val friends: List<FriendData>,
) {
  companion object {
    val EMPTY = FriendsStatsData(
      friends = emptyList(),
    )
  }
}

data class FriendData(
  val id: Int,
  val name: String,
  val numberOfRuns: Int
)
