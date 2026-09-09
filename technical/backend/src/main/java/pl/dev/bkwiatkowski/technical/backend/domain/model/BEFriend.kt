package pl.dev.bkwiatkowski.technical.backend.domain.model

import java.time.LocalDateTime

data class BEFriend(
  val friendId: Int,
  val username: String,
  val createdAt: LocalDateTime,
  val status: BEFriendshipStatus,
  val friendStatus: BEFriendshipStatus,
  val joinedAt: LocalDateTime,
  val attendedEventsCount: Int,
)

enum class BEFriendshipStatus {
  ACCEPTED,
  NOT_ACCEPTED,
}
