package sessions

import Player
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class Session(
    val id: Long,
    @Transient val password: String? = null,
    val name: String,
    val host: Player
) {
    val players: MutableList<Player> = mutableListOf(host)
}