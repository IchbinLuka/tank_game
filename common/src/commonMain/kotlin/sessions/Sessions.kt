package sessions

import Player
import kotlinx.serialization.Serializable

@Serializable
class SessionPreview(
    val sessionId: Int,
    val sessionName: String,
)

@Serializable
class Session(
    val password: String? = null,
    val name: String,
    val host: Player
) {
    val players: MutableList<Player> = mutableListOf(host)
}