package sessions

import PlayerData
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class Session(
    val id: Long,
    @Transient val password: String? = null,
    val name: String,
    val host: PlayerData
) {
    val players: MutableList<PlayerData> = mutableListOf(host)
}