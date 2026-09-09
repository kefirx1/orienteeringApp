package pl.dev.bkwiatkowski.feature.dashboard.domain.model

import java.time.LocalDateTime

data class FriendsListData(
  val friends: List<FriendItem>,
) {
  companion object {
    val EMPTY = FriendsListData(
      friends = emptyList(),
    )
  }
}

data class FriendItem(
  val friendId: Int,
  val username: String,
  val createdAt: LocalDateTime,
  val status: FriendshipStatus,
  val friendStatus: FriendshipStatus,
)

enum class FriendshipStatus {
  ACCEPTED,
  NOT_ACCEPTED,
}
