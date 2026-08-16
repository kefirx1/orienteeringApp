
package pl.dev.bkwiatkowski.technical.backend.domain.model

data class BEFinishSessionResponse (
    val participant: BESessionParticipant,
    val sessionWaypointDetails: List<BESessionWaypointDetail>
)
